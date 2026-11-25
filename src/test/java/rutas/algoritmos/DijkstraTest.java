package rutas.algoritmos;

import static rutas.TestUtil.*;
import static org.junit.jupiter.api.Assertions.*;

import rutas.modelo.Grafo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DijkstraTest {
    private final AlgoritmoRuta dijkstra = new Dijkstra();

    @Test
    @DisplayName("Ruta simple entre dos nodos conectados")
    void rutaSimple() {
        Grafo g = grafoConNodos(2);
        g.agregarArista(1, 2, 10, false);
        RutaResultado r = dijkstra.calcular(g, 1, 2);
        assertTrue(r.isRutaEncontrada());
        assertEquals(lista(1, 2), ids(r));
        assertEquals(10.0, r.getCosto(), 0.0001);
    }

    @Test
    @DisplayName("Elige la ruta más barata entre varias posibles")
    void eligeLaMasCorta() {
        Grafo g = grafoConNodos(3);
        g.agregarArista(1, 3, 10, false);   // directa: 10
        g.agregarArista(1, 2, 3, false);    // indirecta: 3 + 4 = 7
        g.agregarArista(2, 3, 4, false);
        RutaResultado r = dijkstra.calcular(g, 1, 3);
        assertEquals(lista(1, 2, 3), ids(r));
        assertEquals(7.0, r.getCosto(), 0.0001);
    }

    @Test
    void destinoInalcanzable() {
        Grafo g = grafoConNodos(3);
        g.agregarArista(1, 2, 5, false);    // el nodo 3 está aislado
        RutaResultado r = dijkstra.calcular(g, 1, 3);
        assertFalse(r.isRutaEncontrada());
        assertTrue(r.getCamino().isEmpty());
        assertNotNull(r.getMensaje());
    }

    @Test
    void origenIgualDestino() {
        Grafo g = grafoConNodos(2);
        g.agregarArista(1, 2, 5, false);
        RutaResultado r = dijkstra.calcular(g, 1, 1);
        assertTrue(r.isRutaEncontrada());
        assertEquals(lista(1), ids(r));
        assertEquals(0.0, r.getCosto(), 0.0001);
    }

    @Test
    void grafoConUnSoloNodo() {
        Grafo g = grafoConNodos(1);
        RutaResultado r = dijkstra.calcular(g, 1, 1);
        assertTrue(r.isRutaEncontrada());
        assertEquals(lista(1), ids(r));
    }

    @Test
    @DisplayName("Una arista cerrada obliga a usar la ruta alterna")
    void aristaCerradaObligaRutaAlterna() {
        Grafo g = grafoEjemplo();
        assertEquals(lista(1, 2, 5, 6, 8), ids(dijkstra.calcular(g, 1, 8)));

        g.cerrarArista(5, 6);
        RutaResultado r = dijkstra.calcular(g, 1, 8);
        assertEquals(lista(1, 3, 4, 7, 8), ids(r));
        assertEquals(5000.0, r.getCosto(), 0.0001);
    }

    @Test
    void reabrirAristaRestauraLaMejorRuta() {
        Grafo g = grafoEjemplo();
        g.cerrarArista(5, 6);
        g.abrirArista(5, 6);
        RutaResultado r = dijkstra.calcular(g, 1, 8);
        assertEquals(4200.0, r.getCosto(), 0.0001);
    }

    @Test
    void todosLosCaminosCerrados() {
        Grafo g = grafoEjemplo();
        g.cerrarArista(5, 6);
        g.cerrarArista(4, 7);
        RutaResultado r = dijkstra.calcular(g, 1, 8);
        assertFalse(r.isRutaEncontrada());
    }

    @Test
    void respetaElSentidoUnico() {
        Grafo g = grafoConNodos(2);
        g.agregarArista(1, 2, 5, true);     // solo 1 -> 2
        assertTrue(dijkstra.calcular(g, 1, 2).isRutaEncontrada());
        assertFalse(dijkstra.calcular(g, 2, 1).isRutaEncontrada());
    }

    @Test
    void grafoDesconectadoEnDosComponentes() {
        Grafo g = grafoConNodos(4);
        g.agregarArista(1, 2, 1, false);
        g.agregarArista(3, 4, 1, false);
        assertTrue(dijkstra.calcular(g, 1, 2).isRutaEncontrada());
        assertFalse(dijkstra.calcular(g, 1, 4).isRutaEncontrada());
    }

    @Test
    void pesosCero() {
        Grafo g = grafoConNodos(3);
        g.agregarArista(1, 2, 0, false);
        g.agregarArista(2, 3, 0, false);
        RutaResultado r = dijkstra.calcular(g, 1, 3);
        assertEquals(lista(1, 2, 3), ids(r));
        assertEquals(0.0, r.getCosto(), 0.0001);
    }

    @Test
    @DisplayName("Un ciclo no provoca bucle infinito")
    void grafoConCiclos() {
        Grafo g = grafoConNodos(4);
        g.agregarArista(1, 2, 1, false);
        g.agregarArista(2, 3, 1, false);
        g.agregarArista(3, 1, 1, false);    // ciclo 1-2-3
        g.agregarArista(3, 4, 5, false);
        RutaResultado r = dijkstra.calcular(g, 1, 4);
        assertEquals(lista(1, 3, 4), ids(r));
        assertEquals(6.0, r.getCosto(), 0.0001);
    }

    @Test
    void nodoInexistenteLanzaExcepcion() {
        Grafo g = grafoConNodos(2);
        assertThrows(IllegalArgumentException.class, () -> dijkstra.calcular(g, 1, 99));
        assertThrows(IllegalArgumentException.class, () -> dijkstra.calcular(g, 99, 1));
    }

    @Test
    void grafoNullLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> dijkstra.calcular(null, 1, 2));
    }
}
