package rutas.algoritmos;

import static rutas.TestUtil.*;
import static org.junit.jupiter.api.Assertions.*;

import rutas.modelo.Grafo;
import org.junit.jupiter.api.Test;

class BFSTest {
    private final AlgoritmoRuta bfs = new BFS();

    @Test
    void prefiereMenosTramosAunqueCuesteMas() {
        Grafo g = grafoConNodos(4);
        g.agregarArista(1, 4, 100, false);  // 1 tramo, costo 100
        g.agregarArista(1, 2, 1, false);    // 3 tramos, costo 3
        g.agregarArista(2, 3, 1, false);
        g.agregarArista(3, 4, 1, false);
        RutaResultado r = bfs.calcular(g, 1, 4);
        assertEquals(lista(1, 4), ids(r));
        assertEquals(100.0, r.getCosto(), 0.0001);
    }

    @Test
    void destinoInalcanzable() {
        Grafo g = grafoConNodos(3);
        g.agregarArista(1, 2, 1, false);
        assertFalse(bfs.calcular(g, 1, 3).isRutaEncontrada());
    }

    @Test
    void origenIgualDestino() {
        RutaResultado r = bfs.calcular(grafoConNodos(2), 2, 2);
        assertEquals(lista(2), ids(r));
        assertEquals(0.0, r.getCosto(), 0.0001);
    }

    @Test
    void respetaLosCierres() {
        Grafo g = grafoEjemplo();
        g.cerrarArista(5, 6);
        g.cerrarArista(4, 7);
        assertFalse(bfs.calcular(g, 1, 8).isRutaEncontrada());
    }

    @Test
    void encuentraRutaEnElGrafoEjemplo() {
        RutaResultado r = bfs.calcular(grafoEjemplo(), 1, 8);
        assertTrue(r.isRutaEncontrada());
        assertEquals(5, r.getCamino().size());   // 4 tramos
        assertEquals(1, r.getCamino().get(0).getId());
        assertEquals(8, r.getCamino().get(4).getId());
    }

    @Test
    void nodoInexistenteLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> bfs.calcular(grafoConNodos(2), 1, 99));
    }
}
