package controllers;

import config.Sesion;
import models.ReporteCoberturaMovil;
import models.dao.CoberturaMovilDAO;
import views.CoberturaMovilView;
import javax.swing.JOptionPane;
import java.util.List;

/**
 * Controlador del módulo Cobertura móvil (RF-08).
 *
 * @author santi
 */
public class CoberturaMovilController extends BaseController<CoberturaMovilView> {

    private final CoberturaMovilDAO dao;
    private int seleccionadoId = -1;

    public CoberturaMovilController(CoberturaMovilView vista) {
        super(vista);
        this.dao = new CoberturaMovilDAO();
        inicializarEventos();
    }

    @Override
    protected void inicializarEventos() {
        vista.getBtnGuardar().addActionListener(e -> guardar());
        vista.getBtnActualizar().addActionListener(e -> actualizar());
        vista.getBtnEliminar().addActionListener(e -> eliminar());
        vista.getBtnLimpiar().addActionListener(e -> limpiar());
        vista.getTabla().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                cargarSeleccion();
            }
        });
    }

    @Override
    public void iniciar() {
        cargarTabla();
        super.iniciar();
    }

    private void cargarTabla() {
        vista.getModeloTabla().setRowCount(0);
        for (ReporteCoberturaMovil r : dao.listar()) {
            vista.getModeloTabla().addRow(new Object[]{
                r.getId(),
                r.getZonaBarrio(),
                r.getOperador(),
                r.getNivelFalla(),
                r.getLatitud(),
                r.getLongitud(),
                r.getFechaReporte()
            });
        }
    }

    private void guardar() {
        try {
            ReporteCoberturaMovil r = leerFormulario();
            r.setUsuarioId(usuarioActual());
            int id = dao.insertar(r);
            if (id > 0) {
                vista.setEstado("Cobertura guardada con ID " + id + ".");
                limpiar();
                cargarTabla();
            } else {
                vista.setEstado("No se pudo guardar la cobertura.");
            }
        } catch (IllegalArgumentException ex) {
            vista.setEstado(ex.getMessage());
        }
    }

    private void actualizar() {
        if (seleccionadoId < 0) {
            vista.setEstado("Seleccione un registro de la tabla.");
            return;
        }
        try {
            ReporteCoberturaMovil r = leerFormulario();
            r.setId(seleccionadoId);
            r.setUsuarioId(usuarioActual());
            if (dao.actualizar(r)) {
                vista.setEstado("Cobertura actualizada.");
                limpiar();
                cargarTabla();
            } else {
                vista.setEstado("No se pudo actualizar.");
            }
        } catch (IllegalArgumentException ex) {
            vista.setEstado(ex.getMessage());
        }
    }

    private void eliminar() {
        if (seleccionadoId < 0) {
            vista.setEstado("Seleccione un registro de la tabla.");
            return;
        }
        if (JOptionPane.showConfirmDialog(vista, "¿Eliminar el registro #" + seleccionadoId + "?",
                "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            dao.eliminar(seleccionadoId);
            limpiar();
            cargarTabla();
        }
    }

    private void limpiar() {
        seleccionadoId = -1;
        vista.getTabla().clearSelection();
        vista.getTxtZona().setText("");
        vista.getTxtOperador().setText("");
        vista.getTxtLatitud().setText("");
        vista.getTxtLongitud().setText("");
        vista.getTxtObservaciones().setText("");
        vista.getCbxNivelFalla().setSelectedIndex(0);
    }

    private void cargarSeleccion() {
        int fila = vista.getTabla().getSelectedRow();
        if (fila < 0) {
            return;
        }
        int id = (Integer) vista.getModeloTabla().getValueAt(fila, 0);
        ReporteCoberturaMovil r = dao.buscarPorId(id);
        if (r == null) {
            return;
        }
        seleccionadoId = r.getId();
        vista.getTxtZona().setText(r.getZonaBarrio());
        vista.getTxtOperador().setText(r.getOperador());
        vista.getCbxNivelFalla().setSelectedItem(r.getNivelFalla());
        vista.getTxtLatitud().setText(String.valueOf(r.getLatitud()));
        vista.getTxtLongitud().setText(String.valueOf(r.getLongitud()));
        vista.getTxtObservaciones().setText(r.getObservaciones() == null ? "" : r.getObservaciones());
    }

    private ReporteCoberturaMovil leerFormulario() {
        String zona = vista.getTxtZona().getText().trim();
        if (zona.isEmpty()) {
            throw new IllegalArgumentException("La zona o barrio es obligatorio.");
        }
        String operador = vista.getTxtOperador().getText().trim();
        if (operador.isEmpty()) {
            throw new IllegalArgumentException("El operador es obligatorio.");
        }
        double lat;
        double lon;
        try {
            lat = Double.parseDouble(vista.getTxtLatitud().getText().trim().replace(',', '.'));
            lon = Double.parseDouble(vista.getTxtLongitud().getText().trim().replace(',', '.'));
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Latitud y longitud deben ser números válidos.");
        }
        return new ReporteCoberturaMovil(
                0,
                zona,
                operador,
                (String) vista.getCbxNivelFalla().getSelectedItem(),
                lat,
                lon,
                vista.getTxtObservaciones().getText().trim());
    }

    private int usuarioActual() {
        return Sesion.hayUsuario() ? Sesion.getUsuarioActual().getId() : 1;
    }
}