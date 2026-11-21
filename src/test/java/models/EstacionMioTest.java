package models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class EstacionMioTest {

    @Test
    public void testCrearEstacionValida() {
        EstacionMio e = new EstacionMio("Estación Universidades", 3.3688, -76.5290, true);
        assertEquals("Estación Universidades", e.getNombre());
        assertEquals(3.3688, e.getLatitud(), 0.0001);
        assertEquals(-76.5290, e.getLongitud(), 0.0001);
        assertTrue(e.isOperativa());
        assertEquals("Operativa", e.getEstadoTexto());
    }

    @Test
    public void testImplementaGeolocalizable() {
        Geolocalizable geo = new EstacionMio("Estación Capri", 3.3958, -76.5449);
        assertEquals(3.3958, geo.getLatitud(), 0.0001);
        assertEquals(-76.5449, geo.getLongitud(), 0.0001);
    }

    @Test
    public void testCambiarEstadoANoOperativa() {
        EstacionMio e = new EstacionMio("Estación Estadio", 3.4285, -76.5401, true);
        e.setOperativa(false);
        assertFalse(e.isOperativa());
        assertEquals("No operativa", e.getEstadoTexto());
    }

    @Test
    public void testNombreVacioLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> {
            new EstacionMio("   ", 3.40, -76.53, true);
        });
    }

    @Test
    public void testCoordenadasInvalidasLanzanExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> {
            new EstacionMio("Estación Inválida", 95.0, -76.53, true);
        });
    }
}