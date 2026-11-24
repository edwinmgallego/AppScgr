package rutas.modelo;

import static org.junit.jupiter.api.Assertions.*;

import rutas.TestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class GrafoTest {
    private Grafo g;

    @BeforeEach
    void preparar() {
        g = TestUtil.grafoConNodos(3);
    }

    @Test
    @DisplayName("Agregar un nodo permite recuperarlo por id")
    void agregarYRecuperarNodo() {
        g.agregarNodo(new Nodo(10, "Centro", 3.45, -76.53));
        assertEquals(4, g.numNodos());
        assertEquals("Centro", g.getNodo(10).getNombre());
    }

    @Test
    void nodoInexistenteDevuelveNull() {
        assertNull(g.getNodo(99));
    }

    @Test
    void nodoDuplicadoLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> g.agregarNodo(new Nodo(1, "Otro", 0, 0)));
        assertEquals(3, g.numNodos());
    }

    @Test
    void nodoNullLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> g.agregarNodo(null));
    }

    @Test
    void aristaNoDirigidaCreaAmbosSentidos() {
        g.agregarArista(1, 2, 5, false);
        assertEquals(1, g.getAdyacentes(1).size());
        assertEquals(2, g.getAdyacentes(1).get(0).getDestino());
        assertEquals(1, g.getAdyacentes(2).size());
        assertEquals(1, g.getAdyacentes(2).get(0).getDestino());
    }

    @Test
    void aristaDirigidaSoloUnSentido() {
        g.agregarArista(1, 2, 5, true);
        assertEquals(1, g.getAdyacentes(1).size());
        assertTrue(g.getAdyacentes(2).isEmpty());
    }

    @Test
    void pesoNegativoLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> g.agregarArista(1, 2, -1, false));
    }

    @Test
    void pesoCeroEsValido() {
        g.agregarArista(1, 2, 0, false);
        assertEquals(0.0, g.getAdyacentes(1).get(0).getPeso(), 0.0001);
    }

    @Test
    void aristaConNodoInexistenteLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> g.agregarArista(1, 99, 5, false));
        assertThrows(IllegalArgumentException.class, () -> g.agregarArista(99, 1, 5, false));
    }

    @Test
    void adyacentesDeNodoInexistenteLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> g.getAdyacentes(99));
    }

    @Test
    @DisplayName("Cerrar una arista la oculta de getAdyacentes en ambos sentidos")
    void cerrarArista() {
        g.agregarArista(1, 2, 5, false);
        g.cerrarArista(1, 2);
        assertTrue(g.getAdyacentes(1).isEmpty());
        assertTrue(g.getAdyacentes(2).isEmpty());
    }

    @Test
    void cerrarAristaNoAfectaOtrasAristas() {
        g.agregarArista(1, 2, 5, false);
        g.agregarArista(1, 3, 7, false);
        g.cerrarArista(1, 2);
        assertEquals(1, g.getAdyacentes(1).size());
        assertEquals(3, g.getAdyacentes(1).get(0).getDestino());
    }

    @Test
    void abrirAristaLaRestaura() {
        g.agregarArista(1, 2, 5, false);
        g.cerrarArista(1, 2);
        g.abrirArista(1, 2);
        assertEquals(1, g.getAdyacentes(1).size());
        assertEquals(1, g.getAdyacentes(2).size());
    }

    @Test
    void cerrarAristaInexistenteLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> g.cerrarArista(1, 2));
    }

    @Test
    void cerrarAristaConNodoInexistenteLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> g.cerrarArista(1, 99));
    }

    @Test
    void getNodosDevuelveTodos() {
        assertEquals(3, g.getNodos().size());
    }

    @Test
    @DisplayName("getTodasLasAristas incluye las bloqueadas (a diferencia de getAdyacentes)")
    void getTodasLasAristasIncluyeBloqueadas() {
        g.agregarArista(1, 2, 5, false);   // dos arcos: 1->2 y 2->1
        g.agregarArista(1, 3, 7, true);    // un arco
        assertEquals(3, g.getTodasLasAristas().size());
        g.cerrarArista(1, 2);
        assertEquals(3, g.getTodasLasAristas().size());
        assertEquals(1, g.getAdyacentes(1).size());
    }

    @Test
    void getTodasLasAristasEnGrafoSinAristasEstaVacio() {
        assertTrue(g.getTodasLasAristas().isEmpty());
    }

    @Test
    void pesoNaNOInfinitoLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> g.agregarArista(1, 2, Double.NaN, false));
        assertThrows(IllegalArgumentException.class, () -> g.agregarArista(1, 2, Double.POSITIVE_INFINITY, false));
    }

    @Test
    void nodoConCoordenadasFueraDeRangoLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> g.agregarNodo(new Nodo(20, "A", 91, 0)));
        assertThrows(IllegalArgumentException.class, () -> g.agregarNodo(new Nodo(21, "B", 0, -181)));
        assertThrows(IllegalArgumentException.class, () -> g.agregarNodo(new Nodo(22, "C", Double.NaN, 0)));
        assertEquals(3, g.numNodos());
    }
}
