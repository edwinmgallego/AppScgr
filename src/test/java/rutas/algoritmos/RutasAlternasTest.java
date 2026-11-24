package rutas.algoritmos;

import static rutas.TestUtil.*;
import static org.junit.jupiter.api.Assertions.*;

import rutas.modelo.Grafo;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RutasAlternasTest {
    private final RutasAlternas alternas = new RutasAlternas(new Dijkstra());

    @Test
    void devuelveMejorRutaYAlternaOrdenadasPorCosto() {
        List<RutaResultado> rutas = alternas.calcular(grafoEjemplo(), 1, 8, 3);
        assertEquals(2, rutas.size());
        assertEquals(lista(1, 2, 5, 6, 8), ids(rutas.get(0)));
        assertEquals(lista(1, 3, 4, 7, 8), ids(rutas.get(1)));
        assertTrue(rutas.get(0).getCosto() <= rutas.get(1).getCosto());
    }

    @Test
    void conKIgualUnoSoloDevuelveLaMejor() {
        assertEquals(1, alternas.calcular(grafoEjemplo(), 1, 8, 1).size());
    }

    @Test
    void sinRutaDevuelveListaVacia() {
        Grafo g = grafoConNodos(2);
        assertTrue(alternas.calcular(g, 1, 2, 3).isEmpty());
    }

    @Test
    void rutasDevueltasSonDistintas() {
        List<RutaResultado> rutas = alternas.calcular(grafoEjemplo(), 1, 8, 5);
        for (int i = 0; i < rutas.size(); i++) {
            for (int j = i + 1; j < rutas.size(); j++) {
                assertNotEquals(ids(rutas.get(i)), ids(rutas.get(j)));
            }
        }
    }

    @Test
    @DisplayName("El grafo queda intacto tras calcular alternas")
    void elGrafoQuedaIntactoTrasCalcularAlternas() {
        Grafo g = grafoEjemplo();
        alternas.calcular(g, 1, 8, 3);
        // todas las aristas siguen abiertas: la mejor ruta original se mantiene
        RutaResultado r = new Dijkstra().calcular(g, 1, 8);
        assertEquals(4200.0, r.getCosto(), 0.0001);
        assertEquals(2, g.getAdyacentes(1).size());
    }

    @Test
    void respetaCierresPreexistentes() {
        Grafo g = grafoEjemplo();
        g.cerrarArista(5, 6);
        List<RutaResultado> rutas = alternas.calcular(g, 1, 8, 3);
        assertEquals(lista(1, 3, 4, 7, 8), ids(rutas.get(0)));
        // el cierre original sigue vigente: ir de 5 a 6 ya no puede costar 1400 (tramo directo)
        assertTrue(new Dijkstra().calcular(g, 5, 6).getCosto() > 1400.0);
    }

    @Test
    void kInvalidoLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> alternas.calcular(grafoEjemplo(), 1, 8, 0));
    }
}
