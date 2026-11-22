package models.dao;

import java.util.List;
import models.ReporteServicioZona;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ServicioZonaDAOTest {

    private ServicioZonaDAO dao;

    @BeforeEach
    public void setUp() {
        dao = new ServicioZonaDAO();
    }

    @Test
    public void testRegistrarConsultarYActualizarServicioPorBarrio() {
        String barrioPrueba = "JUnit_Granada_" + System.currentTimeMillis();
        ReporteServicioZona nuevo = new ReporteServicioZona(
                1, barrioPrueba, "ENERGIA", "CON_FALLAS", "Fluctuación de voltaje");

        assertTrue(dao.registrarReporteServicio(nuevo), "Debe registrar el reporte de servicio");

        List<ReporteServicioZona> resultados = dao.obtenerServiciosPorBarrio(barrioPrueba);
        assertFalse(resultados.isEmpty(), "Debe encontrar el reporte recién insertado");

        int idCreado = resultados.get(0).getId();
        assertTrue(dao.actualizarEstadoServicio(idCreado, "OPERANDO"), "Debe actualizar el estado");
        assertTrue(dao.eliminarReporte(idCreado), "Debe limpiar el registro de prueba");
    }

    @Test
    public void testRegistrarReporteNuloRetornaFalse() {
        assertFalse(dao.registrarReporteServicio(null));
    }
}