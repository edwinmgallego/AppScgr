package models.dao;

import java.util.List;
import models.ReporteCoberturaMovil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CoberturaMovilDAOTest {

    private CoberturaMovilDAO dao;

    @BeforeEach
    public void setUp() {
        dao = new CoberturaMovilDAO();
    }

    @Test
    public void testRegistrarBuscarYRestablecerCobertura() {
        String barrioPrueba = "JUnit_Pance_" + System.currentTimeMillis();
        ReporteCoberturaMovil reporte = new ReporteCoberturaMovil(
                1, barrioPrueba, "Movistar", "SIN_SENAL", 3.3310, -76.5401, "Caída total de antena");

        assertTrue(dao.registrarFallaCobertura(reporte), "Debe registrar la falla de cobertura");

        List<ReporteCoberturaMovil> encontrados = dao.buscarPorOperadorOZona(barrioPrueba);
        assertFalse(encontrados.isEmpty(), "Debe localizar el reporte por barrio");
        assertEquals("Movistar", encontrados.get(0).getOperador());

        int idCreado = encontrados.get(0).getId();
        assertTrue(dao.marcarSenalRestablecida(idCreado), "Debe marcar señal como restablecida");
        assertTrue(dao.eliminarReporteCobertura(idCreado), "Debe eliminar el registro de prueba");
    }

    @Test
    public void testRegistrarCoberturaInvalidaRetornaFalse() {
        assertFalse(dao.registrarFallaCobertura(null));
    }
}