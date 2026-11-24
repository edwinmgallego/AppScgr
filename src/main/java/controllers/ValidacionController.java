/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controllers;

import config.Sesion;
import models.Incidente;
import models.TipoVoto;
import models.dao.IncidenteDAO;
import models.dao.ValidacionDAO;
import views.ValidacionView;

import javax.swing.JOptionPane;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador del módulo de validación comunitaria (RF-13).
 * Carga los reportes, sus votos, clasifica el estado de validación y registra
 * los votos del usuario autenticado impidiendo duplicados y auto-votos.
 *
 * @author Grupo 9 Gabriela Moreno - Validación Comunitaria y Dashboard
 */
public class ValidacionController extends BaseController<ValidacionView> {

    private static final int UMBRAL_VERIFICADO = 3;

    private final ValidacionDAO validacionDAO;
    private final IncidenteDAO incidenteDAO;
    private final Map<Integer, Incidente> reportes = new HashMap<>();

    public ValidacionController(ValidacionView vista) {
        super(vista);
        this.validacionDAO = new ValidacionDAO();
        this.incidenteDAO = new IncidenteDAO();
        inicializarEventos();
    }

    @Override
    protected void inicializarEventos() {
        vista.getBtnActualizar().addActionListener(e -> cargarReportes());
        vista.getBtnConfirmar().addActionListener(e -> votar(TipoVoto.CONFIRMA));
        vista.getBtnCuestionar().addActionListener(e -> votar(TipoVoto.CUESTIONA));
        vista.getBtnCerrar().addActionListener(e -> vista.dispose());
    }

    @Override
    public void iniciar() {
        cargarReportes();
        super.iniciar();
    }

    private void cargarReportes() {
        vista.getModeloTabla().setRowCount(0);
        reportes.clear();

        List<Incidente> lista = incidenteDAO.obtenerTodosLosIncidentes();
        List<Integer> ids = new ArrayList<>();
        for (Incidente inc : lista) {
            ids.add(inc.getId());
            reportes.put(inc.getId(), inc);
        }

        Map<Integer, int[]> votos = validacionDAO.contarVotos(ids);

        for (Incidente inc : lista) {
            int[] contadores = votos.getOrDefault(inc.getId(), new int[]{0, 0});
            int confirmaciones = contadores[0];
            int cuestionamientos = contadores[1];

            String origen = validacionDAO.obtenerOrigenReporte(inc.getId());
            if (origen == null || origen.isBlank()) {
                origen = "CIUDADANO";
            }
            String estado = clasificar(confirmaciones, cuestionamientos, origen);

            vista.getModeloTabla().addRow(new Object[]{
                    inc.getId(),
                    inc.getTipo(),
                    inc.getDescripcion(),
                    inc.getLatitud(),
                    inc.getLongitud(),
                    estado,
                    confirmaciones,
                    cuestionamientos
            });
        }
        vista.setEstado("Reportes cargados: " + lista.size()
                + ". Umbral para verificación comunitaria: " + UMBRAL_VERIFICADO + " confirmaciones.");
    }

    /**
     * Regla de negocio RF-13:
     * - Reporte marcado como OFICIAL => información oficial verificada.
     * - 3 o más confirmaciones => verificado por la comunidad.
     * - Más cuestionamientos que confirmaciones => cuestionado.
     * - En otro caso => reporte ciudadano sin validar.
     */
    private String clasificar(int confirmaciones, int cuestionamientos, String origen) {
        if ("OFICIAL".equalsIgnoreCase(origen)) {
            return "OFICIAL VERIFICADO";
        }
        if (confirmaciones >= UMBRAL_VERIFICADO) {
            return "VERIFICADO POR COMUNIDAD";
        }
        if (cuestionamientos > confirmaciones && cuestionamientos > 0) {
            return "CUESTIONADO";
        }
        return "REPORTE CIUDADANO";
    }

    private int getSelectedId() {
        int fila = vista.getTabla().getSelectedRow();
        if (fila < 0) {
            return -1;
        }
        Object valor = vista.getModeloTabla().getValueAt(fila, 0);
        if (valor instanceof Number) {
            return ((Number) valor).intValue();
        }
        try {
            return Integer.parseInt(String.valueOf(valor));
        } catch (NumberFormatException ex) {
            return -1;
        }
    }

    private void votar(TipoVoto tipo) {
        if (!Sesion.hayUsuario()) {
            JOptionPane.showMessageDialog(vista, "Debe iniciar sesión para validar reportes.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int reporteId = getSelectedId();
        if (reporteId < 0) {
            JOptionPane.showMessageDialog(vista, "Seleccione un reporte de la tabla.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int usuarioId = Sesion.getUsuarioActual().getId();

        int propietario = validacionDAO.obtenerUsuarioIdReporte(reporteId);
        if (propietario != 0 && propietario == usuarioId) {
            JOptionPane.showMessageDialog(vista, "No puede votar su propio reporte.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (validacionDAO.yaVoto(reporteId, usuarioId)) {
            TipoVoto anterior = validacionDAO.votoDeUsuario(reporteId, usuarioId);
            JOptionPane.showMessageDialog(vista,
                    "Ya votó este reporte" + (anterior != null ? " (" + anterior.getEtiqueta() + ")" : "") + ".",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean registrado = validacionDAO.registrarVoto(reporteId, usuarioId, tipo);
        if (registrado) {
            JOptionPane.showMessageDialog(vista,
                    "Voto registrado: " + tipo.getEtiqueta() + ".",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            cargarReportes();
        } else {
            JOptionPane.showMessageDialog(vista,
                    "No se pudo registrar el voto (posible voto duplicado).",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
