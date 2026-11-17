package controllers;

import config.Sesion;
import models.Usuario;
import models.dao.UsuarioDAO;
import utils.PasswordUtil;
import views.PerfilView;
import javax.swing.JOptionPane;

/**
 * Permite que el usuario que inició sesión cambie su nombre y su teléfono.
 * Como la red familiar lee los datos desde la tabla usuarios, sus familiares
 * ven el cambio automáticamente.
 *
 * @author 
 */
public class PerfilController {
    private final PerfilView vista;
    private final UsuarioDAO usuarioDAO;
    private final Usuario usuarioActual;
    private final char caracterOculto; // el símbolo con que los campos esconden la contraseña (•)

    public PerfilController(PerfilView vista) {
        this.vista = vista;
        this.usuarioDAO = new UsuarioDAO();
        this.usuarioActual = Sesion.getUsuarioActual();

        // Llenar el formulario con los datos actuales
        vista.getTxtDocumento().setText(usuarioActual.getDocumento());
        vista.getTxtNombre().setText(usuarioActual.getNombreCompleto());
        vista.getTxtTelefono().setText(usuarioActual.getTelefono() == null ? "" : usuarioActual.getTelefono());

        vista.getBtnGuardar().addActionListener(e -> guardar());
        vista.getBtnCancelar().addActionListener(e -> vista.dispose());

        this.caracterOculto = vista.getTxtPasswordActual().getEchoChar();
        vista.getChkMostrarPassword().addActionListener(e -> alternarPassword());
        vista.getBtnCambiarPassword().addActionListener(e -> cambiarPassword());
    }

    // Muestra u oculta los tres campos de contraseña con una sola casilla
    private void alternarPassword() {
        char eco = vista.getChkMostrarPassword().isSelected() ? (char) 0 : caracterOculto;
        vista.getTxtPasswordActual().setEchoChar(eco);
        vista.getTxtPasswordNueva().setEchoChar(eco);
        vista.getTxtPasswordConfirmar().setEchoChar(eco);
    }

    // Cambia la contraseña: primero valida lo escrito, luego comprueba la actual contra la base de datos
    private void cambiarPassword() {
        String actual = new String(vista.getTxtPasswordActual().getPassword());
        String nueva = new String(vista.getTxtPasswordNueva().getPassword());
        String confirmar = new String(vista.getTxtPasswordConfirmar().getPassword());

        String error = validarPassword(actual, nueva, confirmar);
        if (error != null) {
            JOptionPane.showMessageDialog(vista, error, "Datos inválidos", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // Se confirma que quien cambia la contraseña conoce la actual (por si dejaron la sesión abierta)
        if (usuarioDAO.autenticar(usuarioActual.getDocumento(), actual) == null) {
            JOptionPane.showMessageDialog(vista, "La contraseña actual no es correcta.", "Contraseña incorrecta", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (usuarioDAO.actualizarPassword(usuarioActual.getId(), nueva)) {
            JOptionPane.showMessageDialog(vista, "Contraseña cambiada correctamente.\nUse la nueva la próxima vez que inicie sesión.");
            vista.getTxtPasswordActual().setText("");
            vista.getTxtPasswordNueva().setText("");
            vista.getTxtPasswordConfirmar().setText("");
        } else {
            JOptionPane.showMessageDialog(vista, "Error al guardar en la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Devuelve el mensaje de error, o null si todo es válido. */
    static String validarPassword(String actual, String nueva, String confirmar) {
        if (actual.isEmpty()) {
            return "Escriba su contraseña actual.";
        }
        String errorFormato = PasswordUtil.validarFormato(nueva);
        if (errorFormato != null) {
            return errorFormato;
        }
        if (!nueva.equals(confirmar)) {
            return "Las contraseñas no coinciden.";
        }
        if (nueva.equals(actual)) {
            return "La nueva contraseña debe ser diferente a la actual.";
        }
        return null;
    }

    private void guardar() {
        String nombre = vista.getTxtNombre().getText().trim();
        String telefono = vista.getTxtTelefono().getText().trim();

        String error = validar(nombre, telefono);
        if (error != null) {
            JOptionPane.showMessageDialog(vista, error, "Datos inválidos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String telefonoFinal = telefono.isEmpty() ? null : telefono;
        if (usuarioDAO.actualizarPerfil(usuarioActual.getId(), nombre, telefonoFinal)) {
            // Se actualiza también el usuario de la sesión para que el resto de la app vea el cambio
            usuarioActual.setNombreCompleto(nombre);
            usuarioActual.setTelefono(telefonoFinal);
            JOptionPane.showMessageDialog(vista, "Perfil actualizado correctamente.");
            vista.dispose();
        } else {
            JOptionPane.showMessageDialog(vista, "Error al guardar en la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Devuelve el mensaje de error, o null si todo es válido (mismas reglas que el registro). */
    static String validar(String nombre, String telefono) {
        if (nombre.length() < 3) {
            return "Escriba el nombre completo.";
        }
        if (nombre.length() > 100) {
            return "El nombre no puede tener más de 100 caracteres.";
        }
        if (!telefono.isEmpty() && !telefono.matches("\\+?[0-9 ]{7,20}")) {
            return "El teléfono solo puede tener números (mínimo 7).";
        }
        return null;
    }

    /** Muestra la ventana. */
    public void iniciar() {
        vista.setVisible(true);
    }
}
