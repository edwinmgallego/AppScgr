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
 * buscar usuarios, vincularlos, cambiar su estado, marcar el contacto principal
 * y desvincularlos.
 *
 * @author Equipo 1 - Autenticación y Red Familiar
 */
public class FamiliaresController {
    private final FamiliaresView vista;
    private final UsuarioDAO usuarioDAO;
    private final FamiliarDAO familiarDAO;
    private final Usuario usuarioActual;
    private boolean identidadVerificada = false; // se pide una sola vez mientras esta ventana esté abierta

    public FamiliaresController(FamiliaresView vista) {
        this.vista = vista;
        this.usuarioDAO = new UsuarioDAO();
        this.familiarDAO = new FamiliarDAO();
        this.usuarioActual = Sesion.getUsuarioActual();

        vista.getBtnBuscar().addActionListener(e -> buscar());
        vista.getBtnVincular().addActionListener(e -> vincular());
        vista.getBtnCambiarEstado().addActionListener(e -> cambiarEstado());
        vista.getBtnDesvincular().addActionListener(e -> desvincular());
        vista.getBtnPrincipal().addActionListener(e -> cambiarPrincipal());
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
            JOptionPane.showMessageDialog(vista, mensajeSinResultados(texto), "Sin resultados", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /** Arma el mensaje que sale cuando la búsqueda no encuentra a nadie. */
    static String mensajeSinResultados(String texto) {
        // Si escribieron algo muy largo, se recorta para que la ventanita no quede gigante
        String buscado = (texto.length() > 40) ? texto.substring(0, 40) + "..." : texto;
        return "No encontramos a nadie con \"" + buscado + "\".\n\n"
                + "• Revise que el documento o el nombre estén bien escritos.\n"
                + "• Si su familiar todavía no tiene cuenta, pídale que se registre primero en la app.";
    }

    private void vincular() {
        Integer idUsuario = idSeleccionado(vista.getTblResultados(), vista.getModeloResultados());
        if (idUsuario == null) {
            JOptionPane.showMessageDialog(vista, "Seleccione un usuario de los resultados.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // Antes de vincular, el usuario debe demostrar que es quien dice ser
        if (!confirmarIdentidad()) {
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

    // Pide un dato personal (documento o teléfono) y lo compara con el del usuario que inició sesión.
    // Devuelve true si coincide; si cancela o se equivoca devuelve false y no se vincula a nadie.
    private boolean confirmarIdentidad() {
        if (identidadVerificada) {
            return true;
        }
        String dato = JOptionPane.showInputDialog(vista,
                "Para vincular un familiar debe confirmar su identidad.\nEscriba su número de documento o su teléfono:",
                "Confirmar identidad", JOptionPane.QUESTION_MESSAGE);
        if (dato == null) { // presionó Cancelar
            return false;
        }
        if (coincideConUsuario(dato.trim())) {
            identidadVerificada = true;
            return true;
        }
        JOptionPane.showMessageDialog(vista, "El dato no coincide con su cuenta. No se vinculó al familiar.", "Identidad no confirmada", JOptionPane.ERROR_MESSAGE);
        return false;
    }

    // Compara lo escrito con el documento o el teléfono del usuario actual
    private boolean coincideConUsuario(String dato) {
        if (dato.isEmpty()) {
            return false;
        }
        if (dato.equals(usuarioActual.getDocumento())) {
            return true;
        }
        String telefono = usuarioActual.getTelefono();
        if (telefono == null) { // el teléfono es opcional en el registro
            return false;
        }
        // Se comparan solo los dígitos para ignorar espacios o el "+" (ej. "+57 300 123" = "57300123")
        String telefonoNumeros = telefono.replaceAll("\\D", "");
        return !telefonoNumeros.isEmpty() && telefonoNumeros.equals(dato.replaceAll("\\D", ""));
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

    // Marca al familiar seleccionado como contacto principal; si ya lo era, pregunta si quitarlo
    private void cambiarPrincipal() {
        Integer idVinculo = idSeleccionado(vista.getTblFamiliares(), vista.getModeloFamiliares());
        if (idVinculo == null) {
            JOptionPane.showMessageDialog(vista, "Seleccione un familiar de la lista.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (idVinculo == familiarDAO.obtenerIdPrincipal(usuarioActual.getId())) {
            int respuesta = JOptionPane.showConfirmDialog(vista, "Este familiar ya es su contacto principal.\n¿Desea quitarlo?",
                    "Contacto principal", JOptionPane.YES_NO_OPTION);
            if (respuesta == JOptionPane.YES_OPTION) {
                familiarDAO.quitarPrincipal(usuarioActual.getId());
                cargarFamiliares();
            }
            return;
        }

        if (familiarDAO.marcarPrincipal(idVinculo, usuarioActual.getId())) {
            cargarFamiliares();
            JOptionPane.showMessageDialog(vista, "Contacto principal actualizado.");
        } else {
            JOptionPane.showMessageDialog(vista, "No se pudo marcar como contacto principal.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarFamiliares() {
        DefaultTableModel modelo = vista.getModeloFamiliares();
        modelo.setRowCount(0);
        int idPrincipal = familiarDAO.obtenerIdPrincipal(usuarioActual.getId());
        for (Familiar f : familiarDAO.obtenerFamiliares(usuarioActual.getId())) {
            modelo.addRow(new Object[]{f.getId(), f.getDocumento(), (f.getId() == idPrincipal ? "★ " : "") + f.getNombreFamiliar(), f.getTelefono(), f.getEstadoEmergencia()});
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