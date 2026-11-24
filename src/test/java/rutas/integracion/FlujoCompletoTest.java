package rutas.integracion;

import static rutas.TestUtil.*;
import static org.junit.jupiter.api.Assertions.*;

import rutas.algoritmos.Dijkstra;
import rutas.algoritmos.RutaResultado;
import controllers.RutasController;
import rutas.persistencia.LectorCSV;
import java.io.IOException;
import models.Incidente;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Prueba de integración: lector CSV real + grafo + Dijkstra + controlador,
 * usando los archivos de ejemplo de /data (Maven ejecuta las pruebas desde la raíz del proyecto).
 */
class FlujoCompletoTest {
    private static final String NODOS = "data/nodos.csv";
    private static final String TRAMOS = "data/tramos.csv";
    private static final String CIERRES = "data/cierres.csv";

    @TempDir
    Path tmp;

    private RutasController controlador;

    @BeforeEach
    void preparar() throws IOException {
        controlador = new RutasController(new LectorCSV(), new Dijkstra());
        controlador.cargarDatos(NODOS, TRAMOS);
    }

    @Test
    void cargaElDatasetDeEjemplo() {
        assertEquals(8, controlador.getGrafo().numNodos());
    }

    @Test
    @DisplayName("Sin cierres: la mejor ruta de 1 a 8 cuesta 4200 m")
    void rutaSinCierres() {
        RutaResultado r = controlador.calcularRuta(1, 8);
        assertEquals(lista(1, 2, 5, 6, 8), ids(r));
        assertEquals(4200.0, r.getCosto(), 0.0001);
    }

    @Test
    @DisplayName("Con el cierre 5-6 la ruta se desvía y cuesta 5000 m")
    void rutaConCierres() throws IOException {
        controlador.aplicarCierres(CIERRES);
        RutaResultado r = controlador.calcularRuta(1, 8);
        assertEquals(lista(1, 3, 4, 7, 8), ids(r));
        assertEquals(5000.0, r.getCosto(), 0.0001);
    }

    @Test
    @DisplayName("El nodo 8 queda aislado: el tramo 7->8 es de sentido único y 5-6 está cerrado")
    void sentidoUnicoMasCierreDejanAislado() throws IOException {
        controlador.aplicarCierres(CIERRES);
        assertFalse(controlador.calcularRuta(8, 1).isRutaEncontrada());
    }

    @Test
    void sinCierresSePuedeVolverDe8A1() {
        RutaResultado r = controlador.calcularRuta(8, 1);
        assertEquals(lista(8, 6, 5, 2, 1), ids(r));
        assertEquals(4200.0, r.getCosto(), 0.0001);
    }

    @Test
    void rutasAlternasConElDataset() {
        List<RutaResultado> rutas = controlador.calcularRutasAlternas(1, 8, 3);
        assertEquals(2, rutas.size());
        assertEquals(4200.0, rutas.get(0).getCosto(), 0.0001);
        assertEquals(5000.0, rutas.get(1).getCosto(), 0.0001);
    }

    @Test
    @DisplayName("Flujo completo: cargar, cerrar, calcular y exportar el CSV de la ruta")
    void flujoCompletoExportandoLaRuta() throws IOException {
        controlador.aplicarCierres(CIERRES);
        RutaResultado r = controlador.calcularRuta(1, 8);
        Path salida = tmp.resolve("ruta.csv");
        controlador.exportarRuta(r, salida.toString());

        List<String> lineas = Files.readAllLines(salida, StandardCharsets.UTF_8);
        assertEquals(6, lineas.size());
        assertEquals("orden,id,nombre,latitud,longitud", lineas.get(0));
        assertTrue(lineas.get(1).contains("Centro"));
        assertTrue(lineas.get(5).contains("Punto de Encuentro"));
    }

    @Test
    void origenIgualDestinoEnElDataset() {
        RutaResultado r = controlador.calcularRuta(3, 3);
        assertEquals(lista(3), ids(r));
        assertEquals(0.0, r.getCosto(), 0.0001);
    }

    @Test
    void nodoInexistenteEnElDataset() {
        assertThrows(IllegalArgumentException.class, () -> controlador.calcularRuta(1, 99));
    }

    @Test
    void archivoDeCierresInexistenteLanzaIOException() {
        assertThrows(IOException.class, () -> controlador.aplicarCierres("data/no_existe.csv"));
    }

    @Test
    @DisplayName("Un incidente VIA_CERRADA (lista en memoria) produce el mismo desvío que el cierre del CSV")
    void incidenteEnMemoriaDesviaLaRuta() {
        controlador.aplicarIncidentes(List.of(
                new Incidente(1, "VIA_CERRADA", LAT_MEDIO_5_6, LON_MEDIO_5_6, "Inundación")));
        RutaResultado r = controlador.calcularRuta(1, 8);
        assertEquals(lista(1, 3, 4, 7, 8), ids(r));
        assertEquals(5000.0, r.getCosto(), 0.0001);
    }

    @Test
    void incidentesNoBloqueantesNoCambianLaRuta() {
        controlador.aplicarIncidentes(List.of(
                new Incidente(1, "FALLA_SERVICIO", LAT_MEDIO_5_6, LON_MEDIO_5_6, "Sin agua"),
                new Incidente(2, "SIN_SENAL", LAT_MEDIO_3_4, LON_MEDIO_3_4, "Sin cobertura"),
                new Incidente(3, "DANO_ESTRUCTURAL", LAT_MEDIO_4_7, LON_MEDIO_4_7, "Grieta")));
        assertEquals(4200.0, controlador.calcularRuta(1, 8).getCosto(), 0.0001);
    }

    @Test
    void cierresDelCsvEIncidentesSeCombinan() throws IOException {
        controlador.aplicarCierres(CIERRES);                    // cierra 5-6
        controlador.aplicarIncidentes(List.of(
                new Incidente(1, "DERRUMBE", LAT_MEDIO_4_7, LON_MEDIO_4_7, "Derrumbe")));   // cierra 4-7
        assertFalse(controlador.calcularRuta(1, 8).isRutaEncontrada());
        assertTrue(controlador.calcularRutasAlternas(1, 8, 3).isEmpty());
    }

    @Test
    @DisplayName("De la ubicación del usuario al punto de encuentro, evitando el incidente")
    void rutaDesdeCoordenadasConIncidente() {
        controlador.aplicarIncidentes(List.of(
                new Incidente(1, "VIA_CERRADA", LAT_MEDIO_5_6, LON_MEDIO_5_6, "Inundación")));
        RutaResultado r = controlador.calcularRutaDesdeCoordenadas(3.4517, -76.5321, 3.4101, -76.5301);
        assertEquals(lista(1, 3, 4, 7, 8), ids(r));
    }

    @Test
    void incidenteSinTramoCercanoNoAfectaLaRuta() {
        var resumen = controlador.aplicarIncidentes(List.of(
                new Incidente(1, "VIA_CERRADA", 3.50, -76.60, "Muy lejos de la red")));
        assertEquals(1, resumen.getSinTramoCercano().size());
        assertEquals(4200.0, controlador.calcularRuta(1, 8).getCosto(), 0.0001);
    }
}
