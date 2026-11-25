package controllers;

import models.ReporteCoberturaMovil;
import models.ReporteServicioZona;
import models.dao.CoberturaMovilDAO;
import models.dao.ServicioZonaDAO;
import org.junit.jupiter.api.Test;
import views.ServiciosPanelView;
import static org.junit.jupiter.api.Assertions.*;

public class ServiciosControllerTest {

    @Test
    public void testFlujoCompletoControladorServiciosYCobertura() {
        ServiciosPanelView vista = new ServiciosPanelView();
        ServicioZonaDAO servicioDAO = new ServicioZonaDAO();
        CoberturaMovilDAO coberturaDAO = new CoberturaMovilDAO();
        ServiciosController controller = new ServiciosController(vista, servicioDAO, coberturaDAO, 1);

        // Probar flujo RF-06
        String barrioSrv = "CtrlTest_Melendez_" + System.currentTimeMillis();
        vista.getTxtBarrioServicio().setText(barrioSrv);
        vista.getCmbTipoServicio().setSelectedItem("AGUA");
        vista.getCmbEstadoServicio().setSelectedItem("CAIDO");
        vista.getTxtObservacionesServicio().setText("Prueba controlador");
        controller.registrarServicioZona();

        assertEquals("", vista.getTxtBarrioServicio().getText(), "Debe limpiar el formulario tras guardar");

        for (ReporteServicioZona r : servicioDAO.obtenerServiciosPorBarrio(barrioSrv)) {
            servicioDAO.eliminarReporte(r.getId());
        }

        // Probar flujo RF-08
        String barrioMov = "CtrlTest_ValleLili_" + System.currentTimeMillis();
        vista.getTxtBarrioMovil().setText(barrioMov);
        vista.getCmbOperadorMovil().setSelectedItem("Tigo");
        vista.getCmbNivelFallaMovil().setSelectedItem("INTERMITENTE");
        vista.getTxtLatitudMovil().setText("3.3680");
        vista.getTxtLongitudMovil().setText("-76.5300");
        controller.registrarFallaCobertura();

        assertEquals("", vista.getTxtBarrioMovil().getText(), "Debe limpiar el formulario móvil tras guardar");

        for (ReporteCoberturaMovil r : coberturaDAO.buscarPorOperadorOZona(barrioMov)) {
            coberturaDAO.eliminarReporteCobertura(r.getId());
        }
    }
}