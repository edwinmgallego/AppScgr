package rutas.algoritmos;

import static rutas.TestUtil.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import rutas.modelo.Grafo;

class LocalizadorTest {

    @Test
    void encuentraElNodoMasCercano() {
        Grafo g = grafoEjemplo();
        assertEquals(1, Localizador.nodoMasCercano(g, 3.4517, -76.5321).getId());
        assertEquals(8, Localizador.nodoMasCercano(g, 3.4090, -76.5290).getId());
    }

    @Test
    void grafoSinNodosDevuelveNull() {
        assertNull(Localizador.nodoMasCercano(new Grafo(), 3.45, -76.53));
    }

    @Test
    void coordenadasInvalidasLanzanExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> Localizador.nodoMasCercano(grafoEjemplo(), 91, 0));
    }

    @Test
    void grafoNullLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> Localizador.nodoMasCercano(null, 3.45, -76.53));
    }
}
