package controllers;

import static rutas.TestUtil.*;
import static org.junit.jupiter.api.Assertions.*;

import rutas.algoritmos.BFS;
import rutas.algoritmos.Dijkstra;
import rutas.algoritmos.RutaResultado;
import rutas.modelo.Grafo;
import rutas.persistencia.RepositorioGrafo;
import java.io.IOException;
import models.Incidente;
import rutas.algoritmos.ResumenCierres;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Prueba el controlador aislado de los archivos usando un repositorio falso. */
class RutasControllerTest {

    /** Repositorio en memoria que registra las llamadas recibidas. */
    static class RepositorioFalso implements RepositorioGrafo {
        String rutaNodos;
        String rutaTramos;
        String rutaCierres;
        String rutaSalida;
        RutaResultado exportada;
        Grafo grafoADevolver;   // si es null, cada carga devuelve un grafo de ejemplo nuevo

        @Override
        public Grafo cargar(String rutaNodos, String rutaTramos) {
            this.rutaNodos = rutaNodos;
            this.rutaTramos = rutaTramos;
            return grafoADevolver != null ? grafoADevolver : grafoEjemplo();
        }

        @Override
        public void aplicarCierres(Grafo g, String rutaCierres) {
            this.rutaCierres = rutaCierres;
            g.cerrarArista(5, 6);
        }

        @Override
        public void exportarRuta(RutaResultado r, String rutaSalida) throws IOException {
            this.exportada = r;
            this.rutaSalida = rutaSalida;
        }
    }

    private RepositorioFalso repo;
    private RutasController controlador;

    @BeforeEach
    void preparar() {
        repo = new RepositorioFalso();
        controlador = new RutasController(repo, new Dijkstra());
    }

    @Test
    void calcularSinCargarDatosLanzaIllegalState() {
        assertThrows(IllegalStateException.class, () -> controlador.calcularRuta(1, 8));
    }

    @Test
    void aplicarCierresSinCargarDatosLanzaIllegalState() {
        assertThrows(IllegalStateException.class, () -> controlador.aplicarCierres("c.csv"));
    }

    @Test
    void cargarDatosDelegaEnElRepositorio() throws IOException {
        controlador.cargarDatos("n.csv", "t.csv");
        assertEquals("n.csv", repo.rutaNodos);
        assertEquals("t.csv", repo.rutaTramos);
        assertNotNull(controlador.getGrafo());
    }

    @Test
    void calculaLaMejorRuta() throws IOException {
        controlador.cargarDatos("n.csv", "t.csv");
        RutaResultado r = controlador.calcularRuta(1, 8);
        assertEquals(lista(1, 2, 5, 6, 8), ids(r));
    }

    @Test
    void losCierresCambianLaRuta() throws IOException {
        controlador.cargarDatos("n.csv", "t.csv");
        controlador.aplicarCierres("c.csv");
        assertEquals("c.csv", repo.rutaCierres);
        assertEquals(lista(1, 3, 4, 7, 8), ids(controlador.calcularRuta(1, 8)));
    }

    @Test
    @DisplayName("Se puede cambiar el algoritmo en caliente (Strategy)")
    void cambiaDeAlgoritmo() throws IOException {
        controlador.cargarDatos("n.csv", "t.csv");
        controlador.setAlgoritmo(new BFS());
        RutaResultado r = controlador.calcularRuta(1, 8);
        assertTrue(r.isRutaEncontrada());
        assertEquals(5, r.getCamino().size());
    }

    @Test
    void calculaRutasAlternas() throws IOException {
        controlador.cargarDatos("n.csv", "t.csv");
        List<RutaResultado> rutas = controlador.calcularRutasAlternas(1, 8, 3);
        assertEquals(2, rutas.size());
    }

    @Test
    void exportarDelegaEnElRepositorio() throws IOException {
        controlador.cargarDatos("n.csv", "t.csv");
        RutaResultado r = controlador.calcularRuta(1, 8);
        controlador.exportarRuta(r, "salida.csv");
        assertSame(r, repo.exportada);
        assertEquals("salida.csv", repo.rutaSalida);
    }

    @Test
    void recargarDatosDescartaLosCierres() throws IOException {
        controlador.cargarDatos("n.csv", "t.csv");
        controlador.aplicarCierres("c.csv");
        controlador.cargarDatos("n.csv", "t.csv");
        assertEquals(4200.0, controlador.calcularRuta(1, 8).getCosto(), 0.0001);
    }

    @Test
    void constructorYSetterRechazanNull() {
        assertThrows(IllegalArgumentException.class, () -> new RutasController(null, new Dijkstra()));
        assertThrows(IllegalArgumentException.class, () -> new RutasController(repo, null));
        assertThrows(IllegalArgumentException.class, () -> controlador.setAlgoritmo(null));
    }

    @Test
    void aplicarIncidentesSinCargarDatosLanzaIllegalState() {
        assertThrows(IllegalStateException.class, () -> controlador.aplicarIncidentes(List.of()));
    }

    @Test
    @DisplayName("Un incidente VIA_CERRADA sobre el tramo 5-6 desvía la ruta")
    void incidenteBloqueanteDesviaLaRuta() throws IOException {
        controlador.cargarDatos("n.csv", "t.csv");
        ResumenCierres resumen = controlador.aplicarIncidentes(List.of(
                new Incidente(1, "VIA_CERRADA", LAT_MEDIO_5_6, LON_MEDIO_5_6, "Inundación")));
        assertEquals(1, resumen.getTramosCerrados().size());
        assertEquals(lista(1, 3, 4, 7, 8), ids(controlador.calcularRuta(1, 8)));
    }

    @Test
    void incidenteNoBloqueanteNoCambiaLaRuta() throws IOException {
        controlador.cargarDatos("n.csv", "t.csv");
        ResumenCierres resumen = controlador.aplicarIncidentes(List.of(
                new Incidente(2, "FALLA_SERVICIO", LAT_MEDIO_5_6, LON_MEDIO_5_6, "Sin agua")));
        assertEquals(1, resumen.getNoBloqueantes().size());
        assertEquals(lista(1, 2, 5, 6, 8), ids(controlador.calcularRuta(1, 8)));
    }

    @Test
    void radioPersonalizadoSePasaALasReglas() throws IOException {
        controlador.cargarDatos("n.csv", "t.csv");
        // ~108 m del tramo 5-6: fuera del radio por defecto (50 m), dentro de 200 m
        Incidente lejano = new Incidente(3, "DERRUMBE", LAT_MEDIO_5_6, LON_MEDIO_5_6 + 0.001, "x");
        assertEquals(1, controlador.aplicarIncidentes(List.of(lejano)).getSinTramoCercano().size());
        assertEquals(1, controlador.aplicarIncidentes(List.of(lejano), 200).getTramosCerrados().size());
    }

    @Test
    void calculaRutaDesdeCoordenadasDelMapa() throws IOException {
        controlador.cargarDatos("n.csv", "t.csv");
        RutaResultado r = controlador.calcularRutaDesdeCoordenadas(3.4517, -76.5321, 3.4101, -76.5301);
        assertEquals(lista(1, 2, 5, 6, 8), ids(r));
    }

    @Test
    void rutaDesdeCoordenadasConRedVaciaNoEncuentraRuta() throws IOException {
        repo.grafoADevolver = new Grafo();
        controlador.cargarDatos("n.csv", "t.csv");
        assertFalse(controlador.calcularRutaDesdeCoordenadas(3.45, -76.53, 3.41, -76.53).isRutaEncontrada());
    }

    @Test
    void rutaDesdeCoordenadasInvalidasLanzaExcepcion() throws IOException {
        controlador.cargarDatos("n.csv", "t.csv");
        assertThrows(IllegalArgumentException.class,
                () -> controlador.calcularRutaDesdeCoordenadas(95, -76.53, 3.41, -76.53));
    }
}
