package rutas.algoritmos;

import org.junit.jupiter.api.Test;
import rutas.modelo.Grafo;
import rutas.modelo.Nodo;

import static org.junit.jupiter.api.Assertions.*;

/** Sobrecarga de Localizador con filtro de nodos admisibles (usada con la red de OpenStreetMap). */
class LocalizadorFiltroTest {

    @Test
    void respetaElFiltro() {
        Grafo g = new Grafo();
        g.agregarNodo(new Nodo(1, "cerca", 3.4500, -76.5300));
        g.agregarNodo(new Nodo(2, "lejos", 3.4600, -76.5300));
        assertEquals(1, Localizador.nodoMasCercano(g, 3.4501, -76.5300).getId());
        assertEquals(2, Localizador.nodoMasCercano(g, 3.4501, -76.5300, n -> n.getId() != 1).getId());
        assertNull(Localizador.nodoMasCercano(g, 3.4501, -76.5300, n -> false));
        assertThrows(IllegalArgumentException.class, () -> Localizador.nodoMasCercano(g, 3.45, -76.53, null));
    }
}
