package controllers;

import config.Sesion;
import models.Usuario;
import models.dao.UsuarioDAO;
import views.PerfilView;
import javax.swing.JOptionPane;

/**
 * Permite que el usuario que inició sesión cambie su nombre y su teléfono.
 * Como la red familiar lee los datos desde la tabla usuarios, sus familiares
 * ven el cambio automáticamente.
 *
 * @author (escribe aquí tu nombre)
 */
public class PerfilController {
    private final PerfilView vista;
    private final UsuarioDAO usuarioDAO;
    private final Usuario usuarioActual;

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

    /** Muestra la ventana (modal: espera hasta que se cierre). */
    public void iniciar() {
        vista.setVisible(true);
    }
}
