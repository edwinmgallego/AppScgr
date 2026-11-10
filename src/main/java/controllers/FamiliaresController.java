package controllers;

import config.Sesion;
import models.Familiar;
import models.Usuario;
import models.dao.FamiliarDAO;
import models.dao.UsuarioDAO;
import views.FamiliaresView;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 * Controla la red familiar del usuario que inició sesión:
 * buscar usuarios, vincularlos, cambiar su estado y desvincularlos.
 *
 * @author Equipo 1 - Autenticación y Red Familiar
 */
public class FamiliaresController {
    private final FamiliaresView vista;
    private final UsuarioDAO usuarioDAO;
    private final FamiliarDAO familiarDAO;
    private final Usuario usuarioActual;

    public FamiliaresController(FamiliaresView vista) {
        this.vista = vista;
        this.usuarioDAO = new UsuarioDAO();
        this.familiarDAO = new FamiliarDAO();
        this.usuarioActual = Sesion.getUsuarioActual();

        vista.getBtnBuscar().addActionListener(e -> buscar());
        vista.getBtnVincular().addActionListener(e -> vincular());
        vista.getBtnCambiarEstado().addActionListener(e -> cambiarEstado());
        vista.getBtnDesvincular().addActionListener(e -> desvincular());
        cargarFamiliares();
    }

    private void buscar() {
        String texto = vista.getTxtBuscar().getText().trim();
        if (texto.length() < 3) {
            JOptionPane.showMessageDialog(vista, "Escriba al menos 3 caracteres para buscar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        DefaultTableModel modelo = vista.getModeloResultados();
        modelo.setRowCount(0);
        List<Usuario> encontrados = usuarioDAO.buscarUsuarios(texto, usuarioActual.getId());
        for (Usuario u : encontrados) {
            modelo.addRow(new Object[]{u.getId(), u.getDocumento(), u.getNombreCompleto(), u.getTelefono()});
        }
        if (encontrados.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "No se encontraron usuarios registrados con ese dato.");
        }
    }

    private void vincular() {
        Integer idUsuario = idSeleccionado(vista.getTblResultados(), vista.getModeloResultados());
        if (idUsuario == null) {
            JOptionPane.showMessageDialog(vista, "Seleccione un usuario de los resultados.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (familiarDAO.estaVinculado(usuarioActual.getId(), idUsuario)) {
            JOptionPane.showMessageDialog(vista, "Ese usuario ya está en su red familiar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Usuario familiar = usuarioDAO.obtenerPorId(idUsuario);
        if (familiarDAO.vincularFamiliar(usuarioActual.getId(), familiar)) {
            JOptionPane.showMessageDialog(vista, familiar.getNombreCompleto() + " fue agregado a su red familiar.");
            cargarFamiliares();
        } else {
            // Se le explica al usuario el motivo exacto que dejó el DAO
            JOptionPane.showMessageDialog(vista, "No se pudo vincular el familiar.\nMotivo: " + familiarDAO.getUltimoError(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cambiarEstado() {
        Integer idVinculo = idSeleccionado(vista.getTblFamiliares(), vista.getModeloFamiliares());
        if (idVinculo == null) {
            JOptionPane.showMessageDialog(vista, "Seleccione un familiar de la lista.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String estado = (String) vista.getCbxEstado().getSelectedItem();
        if (familiarDAO.actualizarEstado(idVinculo, usuarioActual.getId(), estado)) {
            cargarFamiliares();
        } else {
            JOptionPane.showMessageDialog(vista, "No se pudo cambiar el estado.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void desvincular() {
        Integer idVinculo = idSeleccionado(vista.getTblFamiliares(), vista.getModeloFamiliares());
        if (idVinculo == null) {
            JOptionPane.showMessageDialog(vista, "Seleccione un familiar de la lista.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int respuesta = JOptionPane.showConfirmDialog(vista, "¿Quitar a este familiar de su red?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (respuesta == JOptionPane.YES_OPTION && familiarDAO.desvincularFamiliar(idVinculo, usuarioActual.getId())) {
            cargarFamiliares();
        }
    }

    private void cargarFamiliares() {
        DefaultTableModel modelo = vista.getModeloFamiliares();
        modelo.setRowCount(0);
        for (Familiar f : familiarDAO.obtenerFamiliares(usuarioActual.getId())) {
            modelo.addRow(new Object[]{f.getId(), f.getDocumento(), f.getNombreFamiliar(), f.getTelefono(), f.getEstadoEmergencia()});
        }
    }

    /** Lee el ID (columna oculta 0 del modelo) de la fila seleccionada, o null si no hay selección. */
    private Integer idSeleccionado(JTable tabla, DefaultTableModel modelo) {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            return null;
        }
        return (Integer) modelo.getValueAt(tabla.convertRowIndexToModel(fila), 0);
    }

    public void iniciar() {
        vista.setVisible(true);
    }
}
