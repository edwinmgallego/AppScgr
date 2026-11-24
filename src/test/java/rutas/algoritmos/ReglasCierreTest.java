package rutas.algoritmos;

import static rutas.TestUtil.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import models.Incidente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import rutas.modelo.Grafo;

class ReglasCierreTest {
    private static final double RADIO = 50.0;

    private Grafo g;
    private ReglasCierre reglas;

    @BeforeEach
    void preparar() {
        g = grafoEjemplo();
        reglas = new ReglasCierre();
    }

    private static Incidente inc(String tipo, double lat, double lon) {
        return new Incidente(1, tipo, lat, lon, "prueba");
    }

    /** ¿Hay una arista abierta de origen a destino? */
    private boolean abierto(int origen, int destino) {
        return g.getAdyacentes(origen).stream().anyMatch(a -> a.getDestino() == destino);
    }

    // ---------------- qué tipos bloquean ----------------

    @Test
    @DisplayName("Por defecto solo VIA_CERRADA y DERRUMBE bloquean la vía")
    void tiposBloqueantesPorDefecto() {
        assertTrue(reglas.bloquea(inc("VIA_CERRADA", 0, 0)));
        assertTrue(reglas.bloquea(inc("DERRUMBE", 0, 0)));
        assertFalse(reglas.bloquea(inc("FALLA_SERVICIO", 0, 0)));
        assertFalse(reglas.bloquea(inc("SIN_SENAL", 0, 0)));
        assertFalse(reglas.bloquea(inc("DANO_ESTRUCTURAL", 0, 0)));
    }

    @Test
    void elTipoNoDistingueMayusculasNiEspacios() {
        assertTrue(reglas.bloquea(inc("  via_cerrada ", 0, 0)));
        assertTrue(reglas.bloquea(inc("Derrumbe", 0, 0)));
    }

    @Test
    void tipoNullOIncidenteNullNoBloquean() {
        assertFalse(reglas.bloquea(inc(null, 0, 0)));
        assertFalse(reglas.bloquea(null));
    }

    @Test
    void sePuedenConfigurarOtrosTiposBloqueantes() {
        ReglasCierre estricta = new ReglasCierre(Set.of("DANO_ESTRUCTURAL"));
        assertTrue(estricta.bloquea(inc("DANO_ESTRUCTURAL", 0, 0)));
        assertFalse(estricta.bloquea(inc("DERRUMBE", 0, 0)));
    }

    // ---------------- cierre de tramos ----------------

    @Test
    @DisplayName("VIA_CERRADA sobre el tramo 5-6 lo cierra en ambos sentidos")
    void cierraElTramoMasCercano() {
        ResumenCierres r = reglas.aplicar(g, List.of(inc("VIA_CERRADA", LAT_MEDIO_5_6, LON_MEDIO_5_6)), RADIO);
        assertEquals(1, r.getTramosCerrados().size());
        assertFalse(abierto(5, 6));
        assertFalse(abierto(6, 5));
    }

    @Test
    void derrumbeTambienCierra() {
        reglas.aplicar(g, List.of(inc("DERRUMBE", LAT_MEDIO_3_4, LON_MEDIO_3_4)), RADIO);
        assertFalse(abierto(3, 4));
        assertFalse(abierto(4, 3));
    }

    @Test
    void soloSeCierraElTramoAfectado() {
        reglas.aplicar(g, List.of(inc("VIA_CERRADA", LAT_MEDIO_3_4, LON_MEDIO_3_4)), RADIO);
        assertTrue(abierto(1, 3));
        assertTrue(abierto(3, 5));
        assertTrue(abierto(5, 6));
        assertTrue(abierto(4, 7));
    }

    @Test
    @DisplayName("Cerca de un nodo se elige el tramo más cercano, no cualquiera de los vecinos")
    void eligeElTramoMasCercanoCercaDeUnaInterseccion() {
        // ~40 m de distancia del nodo 3, pero exactamente sobre el tramo 3-4
        reglas.aplicar(g, List.of(inc("VIA_CERRADA", 3.4593, -76.5292)), RADIO);
        assertFalse(abierto(3, 4));
        assertTrue(abierto(1, 3));
        assertTrue(abierto(3, 5));
    }

    @Test
    void elCierreCambiaLaRutaDeDijkstra() {
        reglas.aplicar(g, List.of(inc("VIA_CERRADA", LAT_MEDIO_5_6, LON_MEDIO_5_6)), RADIO);
        RutaResultado ruta = new Dijkstra().calcular(g, 1, 8);
        assertEquals(lista(1, 3, 4, 7, 8), ids(ruta));
        assertEquals(5000.0, ruta.getCosto(), 0.0001);
    }

    @Test
    void variosIncidentesSobreElMismoTramoCuentanUnaVez() {
        ResumenCierres r = reglas.aplicar(g, List.of(
                inc("VIA_CERRADA", LAT_MEDIO_5_6, LON_MEDIO_5_6),
                inc("DERRUMBE", LAT_MEDIO_5_6 + 0.0001, LON_MEDIO_5_6)), RADIO);
        assertEquals(1, r.getTramosCerrados().size());
    }

    @Test
    void variosIncidentesEnTramosDistintosCierranVarios() {
        ResumenCierres r = reglas.aplicar(g, List.of(
                inc("VIA_CERRADA", LAT_MEDIO_5_6, LON_MEDIO_5_6),
                inc("DERRUMBE", LAT_MEDIO_4_7, LON_MEDIO_4_7)), RADIO);
        assertEquals(2, r.getTramosCerrados().size());
        assertFalse(new Dijkstra().calcular(g, 1, 8).isRutaEncontrada());
    }

    @Test
    @DisplayName("Un incidente sobre una vía ya cerrada no cierra una vía vecina")
    void incidenteSobreViaYaCerradaNoCierraOtra() {
        g.cerrarArista(5, 6);                       // por ejemplo, desde cierres.csv
        ResumenCierres r = reglas.aplicar(g, List.of(inc("VIA_CERRADA", LAT_MEDIO_5_6, LON_MEDIO_5_6)), RADIO);
        assertEquals(1, r.getTramosCerrados().size());
        assertTrue(abierto(2, 5));
        assertTrue(abierto(3, 5));
        assertTrue(abierto(6, 8));
    }

    // ---------------- radio ----------------

    @Test
    void incidenteLejanoQuedaSinTramoCercano() {
        Incidente lejos = inc("VIA_CERRADA", 3.50, -76.60);
        ResumenCierres r = reglas.aplicar(g, List.of(lejos), RADIO);
        assertEquals(1, r.getSinTramoCercano().size());
        assertSame(lejos, r.getSinTramoCercano().get(0));
        assertTrue(r.getTramosCerrados().isEmpty());
        assertTrue(abierto(5, 6));
    }

    @Test
    void respetaElRadioIndicado() {
        // ~108 m del tramo 5-6
        Incidente algoLejos = inc("VIA_CERRADA", LAT_MEDIO_5_6, LON_MEDIO_5_6 + 0.001);
        assertEquals(1, reglas.aplicar(g, List.of(algoLejos), 50).getSinTramoCercano().size());
        assertTrue(abierto(5, 6));
        assertEquals(1, reglas.aplicar(g, List.of(algoLejos), 200).getTramosCerrados().size());
        assertFalse(abierto(5, 6));
    }

    // ---------------- datos que no se cierran ----------------

    @Test
    void incidentesNoBloqueantesNoCierranNada() {
        List<Incidente> lista = new ArrayList<>();
        for (String t : new String[]{"FALLA_SERVICIO", "SIN_SENAL", "DANO_ESTRUCTURAL"}) {
            lista.add(inc(t, LAT_MEDIO_5_6, LON_MEDIO_5_6));
        }
        ResumenCierres r = reglas.aplicar(g, lista, RADIO);
        assertEquals(3, r.getNoBloqueantes().size());
        assertTrue(r.getTramosCerrados().isEmpty());
        assertTrue(abierto(5, 6));
    }

    @Test
    void coordenadasInvalidasSeReportanSinLanzarExcepcion() {
        ResumenCierres r = reglas.aplicar(g, List.of(
                inc("VIA_CERRADA", 95, 0),
                inc("VIA_CERRADA", Double.NaN, 0),
                inc("VIA_CERRADA", 0, 200),
                inc("VIA_CERRADA", LAT_MEDIO_5_6, LON_MEDIO_5_6)), RADIO);
        assertEquals(3, r.getInvalidos().size());
        assertEquals(1, r.getTramosCerrados().size());   // el válido sí se procesa
    }

    @Test
    void incidenteNullEnLaListaSeReportaComoInvalido() {
        List<Incidente> lista = new ArrayList<>();
        lista.add(null);
        assertEquals(1, reglas.aplicar(g, lista, RADIO).getInvalidos().size());
    }

    @Test
    void listaVaciaNoHaceNada() {
        ResumenCierres r = reglas.aplicar(g, List.of(), RADIO);
        assertTrue(r.getTramosCerrados().isEmpty());
        assertTrue(abierto(5, 6));
    }

    @Test
    void grafoSinAristasDejaLosIncidentesSinTramo() {
        ResumenCierres r = reglas.aplicar(grafoConNodos(2), List.of(inc("VIA_CERRADA", 0, 0)), RADIO);
        assertEquals(1, r.getSinTramoCercano().size());
    }

    // ---------------- argumentos inválidos ----------------

    @Test
    void argumentosInvalidosLanzanExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> reglas.aplicar(null, List.of(), RADIO));
        assertThrows(IllegalArgumentException.class, () -> reglas.aplicar(g, null, RADIO));
        assertThrows(IllegalArgumentException.class, () -> reglas.aplicar(g, List.of(), 0));
        assertThrows(IllegalArgumentException.class, () -> reglas.aplicar(g, List.of(), -5));
        assertThrows(IllegalArgumentException.class, () -> reglas.aplicar(g, List.of(), Double.NaN));
        assertThrows(IllegalArgumentException.class, () -> new ReglasCierre(null));
    }
}
