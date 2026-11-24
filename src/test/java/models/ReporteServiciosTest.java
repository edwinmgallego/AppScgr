package models;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ReporteServiciosTest {

    @Test
    public void testCrearYPolimorfismoReporteServicioZona() {
        ReporteServicioZona reporte = new ReporteServicioZona(
                1, "San Fernando", "AGUA", "CAIDO", "Corte por daño en tubería matriz");
        reporte.setId(10);
        reporte.setVerificaciones(4);

        assertEquals(10, reporte.getId());
        assertEquals("San Fernando", reporte.getZonaBarrio());
        assertEquals("AGUA", reporte.getServicio());
        assertTrue(reporte.esCritico(), "Estado CAIDO debe evaluarse como crítico");
        assertTrue(reporte.generarResumenEstado().contains("San Fernando"));
    }

    @Test
    public void testCrearYValidacionGeograficaCoberturaMovil() {
        ReporteCoberturaMovil cobertura = new ReporteCoberturaMovil(
                1, "Ciudad Jardín", "Claro", "SIN_SENAL", 3.3745, -76.5312, "Sin datos móviles");

        assertEquals("Claro", cobertura.getOperador());
        assertTrue(cobertura.esCritico(), "Nivel SIN_SENAL debe evaluarse como crítico");
        assertEquals(3.3745, cobertura.getLatitud(), 0.0001);
        assertEquals(-76.5312, cobertura.getLongitud(), 0.0001);

        assertThrows(IllegalArgumentException.class, () -> cobertura.setLatitud(95.0));
        assertThrows(IllegalArgumentException.class, () -> cobertura.setLongitud(-190.0));
    }
}
