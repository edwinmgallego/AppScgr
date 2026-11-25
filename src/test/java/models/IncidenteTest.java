package models;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas del estado de vía de un Incidente (no necesitan base de datos).
 *
 * @author equipo3/samuellopez
 */
public class IncidenteTest {

    @Test
    public void testPorDefectoLaViaEstaCerrada() {
        Incidente inc = new Incidente(1, "VIA_CERRADA", 3.45, -76.53, "Cierre por obras");
        assertEquals(EstadoVia.CERRADA, inc.getEstadoVia());
        assertFalse(inc.estaAbierta());
    }

    @Test
    public void testEstadoNuloQuedaCerrado() {
        Incidente inc = new Incidente(1, "DERRUMBE", 3.45, -76.53, "", null);
        assertEquals(EstadoVia.CERRADA, inc.getEstadoVia());
        inc.setEstadoVia(null);
        assertEquals(EstadoVia.CERRADA, inc.getEstadoVia());
    }

    @Test
    public void testReabrirYVolverACerrar() {
        Incidente inc = new Incidente(1, "VIA_CERRADA", 3.45, -76.53, "");
        inc.marcarComoReabierta();
        assertTrue(inc.estaAbierta());
        inc.marcarComoCerrada();
        assertFalse(inc.estaAbierta());
    }

    @Test
    public void testSoloViasYDerrumbesAfectanLaVia() {
        assertTrue(new Incidente(1, "VIA_CERRADA", 3.45, -76.53, "").afectaVia());
        assertTrue(new Incidente(2, " derrumbe ", 3.45, -76.53, "").afectaVia());
        assertFalse(new Incidente(3, "FALLA_SERVICIO", 3.45, -76.53, "").afectaVia());
        assertFalse(new Incidente(4, "SIN_SENAL", 3.45, -76.53, "").afectaVia());
        assertFalse(new Incidente(5, "DANO_ESTRUCTURAL", 3.45, -76.53, "").afectaVia());
        assertFalse(new Incidente(6, null, 3.45, -76.53, "").afectaVia());
    }

    @Test
    public void testIncidenteEsGeolocalizable() {
        Geolocalizable g = new Incidente(1, "DERRUMBE", 3.45, -76.53, "");
        assertEquals(3.45, g.getLatitud(), 0.0001);
        assertEquals(-76.53, g.getLongitud(), 0.0001);
    }
}
