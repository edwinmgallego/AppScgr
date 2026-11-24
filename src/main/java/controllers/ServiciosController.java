package controllers;

import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import models.ReporteCoberturaMovil;
import models.ReporteServicioZona;
import models.dao.CoberturaMovilDAO;
import models.dao.ServicioZonaDAO;
import views.ServiciosPanelView;

public class ServiciosController {

    private final ServiciosPanelView vista;
    private final ServicioZonaDAO servicioDAO;
    private final CoberturaMovilDAO coberturaDAO;
    private final int usuarioIdActual;

    public ServiciosController(ServiciosPanelView vista) {
        this(vista, new ServicioZonaDAO(), new CoberturaMovilDAO(), 1);
    }

    public ServiciosController(ServiciosPanelView vista, ServicioZonaDAO servicioDAO,
                               CoberturaMovilDAO coberturaDAO, int usuarioIdActual) {
        this.vista = vista;
        this.servicioDAO = servicioDAO;
        this.coberturaDAO = coberturaDAO;
        this.usuarioIdActual = usuarioIdActual > 0 ? usuarioIdActual : 1;
        inicializarEventos();
        cargarTablaServicios(null);
        cargarTablaCobertura();
    }

    private void inicializarEventos() {
        vista.getBtnGuardarServicio().addActionListener(e -> registrarServicioZona());
        vista.getBtnBuscarBarrio().addActionListener(e -> filtrarServiciosPorBarrio());
        vista.getBtnGuardarMovil().addActionListener(e -> registrarFallaCobertura());
    }

    public void registrarServicioZona() {
        String barrio = vista.getTxtBarrioServicio().getText().trim();
        if (barrio.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Debe ingresar el barrio o zona.",
                    "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String servicio = vista.getCmbTipoServicio().getSelectedItem().toString();
        String estado = vista.getCmbEstadoServicio().getSelectedItem().toString();
        String observaciones = vista.getTxtObservacionesServicio().getText().trim();

        ReporteServicioZona reporte = new ReporteServicioZona(
                usuarioIdActual, barrio, servicio, estado, observaciones);

        if (servicioDAO.registrarReporteServicio(reporte)) {
            JOptionPane.showMessageDialog(vista, "Reporte de servicio registrado correctamente.",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            vista.limpiarFormularioServicios();
            cargarTablaServicios(null);
        } else {
            JOptionPane.showMessageDialog(vista, "No se pudo registrar el reporte de servicio.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void filtrarServiciosPorBarrio() {
        String criterio = vista.getTxtBuscarBarrio().getText();
        cargarTablaServicios(criterio);
    }

    public void cargarTablaServicios(String filtroBarrio) {
        DefaultTableModel modelo = vista.getModeloTablaServicios();
        modelo.setRowCount(0);
        List<ReporteServicioZona> lista = servicioDAO.obtenerServiciosPorBarrio(filtroBarrio);
        for (ReporteServicioZona r : lista) {
            modelo.addRow(new Object[]{
                r.getId(),
                r.getZonaBarrio(),
                r.getServicio(),
                r.getEstado(),
                r.getObservaciones(),
                r.getVerificaciones(),
                r.getFechaReporte()
            });
        }
    }

    public void registrarFallaCobertura() {
        String barrio = vista.getTxtBarrioMovil().getText().trim();
        if (barrio.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Debe ingresar el barrio o sector.",
                    "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double latitud;
        double longitud;
        try {
            latitud = Double.parseDouble(vista.getTxtLatitudMovil().getText().trim());
            longitud = Double.parseDouble(vista.getTxtLongitudMovil().getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(vista, "Las coordenadas GPS deben ser valores numéricos válidos.",
                    "Error de formato", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String operador = vista.getCmbOperadorMovil().getSelectedItem().toString();
        String nivelFalla = vista.getCmbNivelFallaMovil().getSelectedItem().toString();
        String observaciones = vista.getTxtObservacionesMovil().getText().trim();

        ReporteCoberturaMovil reporte;
        try {
            reporte = new ReporteCoberturaMovil(
                    usuarioIdActual, barrio, operador, nivelFalla, latitud, longitud, observaciones);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(vista, ex.getMessage(),
                    "Coordenadas fuera de rango", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (coberturaDAO.registrarFallaCobertura(reporte)) {
            JOptionPane.showMessageDialog(vista, "Falla de cobertura móvil registrada.",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            vista.limpiarFormularioMovil();
            cargarTablaCobertura();
        } else {
            JOptionPane.showMessageDialog(vista, "No se pudo registrar la falla de cobertura.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void cargarTablaCobertura() {
        DefaultTableModel modelo = vista.getModeloTablaMovil();
        modelo.setRowCount(0);
        List<ReporteCoberturaMovil> lista = coberturaDAO.obtenerReportesSinSenal();
        for (ReporteCoberturaMovil r : lista) {
            modelo.addRow(new Object[]{
                r.getId(),
                r.getObservaciones(),
                r.getLatitud(),
                r.getLongitud(),
                r.getVerificaciones(),
                r.getFechaReporte()
            });
        }
    }
}