package controllers;

import models.Usuario;
import models.dao.UsuarioDAO;
import utils.PasswordUtil;
import views.RegistroView;
import javax.swing.JOptionPane;

/**
 * Valida el formulario de registro y guarda el usuario nuevo.
 *
 * @author Equipo 1 - Autenticación y Red Familiar
 */
public class RegistroController {
    private final RegistroView vista;
    private final UsuarioDAO usuarioDAO;
    private Usuario usuarioRegistrado;

    public RegistroController(RegistroView vista) {
        this.vista = vista;
        this.usuarioDAO = new UsuarioDAO();
        vista.getBtnRegistrar().addActionListener(e -> registrar());
        vista.getBtnCancelar().addActionListener(e -> vista.dispose());
    }

    private void registrar() {
        String documento = vista.getTxtDocumento().getText().trim();
        String nombre = vista.getTxtNombre().getText().trim();
        String telefono = vista.getTxtTelefono().getText().trim();
        String password = new String(vista.getTxtPassword().getPassword());
        String confirmar = new String(vista.getTxtConfirmar().getPassword());

        String error = validar(documento, nombre, telefono, password, confirmar);
        if (error != null) {
            JOptionPane.showMessageDialog(vista, error, "Datos inválidos", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (usuarioDAO.existeDocumento(documento)) {
            JOptionPane.showMessageDialog(vista, "Ya existe un usuario con ese documento.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Usuario nuevo = new Usuario(0, documento, nombre, telefono.isEmpty() ? null : telefono);
        if (usuarioDAO.registrarUsuario(nuevo, password)) {
            usuarioRegistrado = nuevo;
            JOptionPane.showMessageDialog(vista, "Registro exitoso. Ya puede iniciar sesión.");
            vista.dispose();
        } else {
            JOptionPane.showMessageDialog(vista, "Error al guardar en la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Devuelve el mensaje de error, o null si todo es válido. */
    static String validar(String documento, String nombre, String telefono, String password, String confirmar) {
        if (!documento.matches("\\d{5,20}")) {
            return "El documento debe tener solo números (entre 5 y 20 dígitos).";
        }
        if (nombre.length() < 3) {
            return "Escriba el nombre completo.";
        }
        if (!telefono.isEmpty() && !telefono.matches("\\+?[0-9 ]{7,20}")) {
            return "El teléfono solo puede tener números (mínimo 7).";
        }
        // La validación de la contraseña (largo, mayúscula, minúscula y carácter especial) está en PasswordUtil
        String errorPassword = PasswordUtil.validarFormato(password);
        if (errorPassword != null) {
            return errorPassword;
        }
        if (!password.equals(confirmar)) {
            return "Las contraseñas no coinciden.";
        }
        return null;
    }

    /** Muestra la ventana (ya no es modal: no espera; LoginController se entera cuando se cierra). */
    public void iniciar() {
        vista.setVisible(true);
    }

    /** La ventana de registro (LoginController la usa para saber cuándo se cierra). */
    public RegistroView getVista() {
        return vista;
    }

    /** Usuario creado, o null si se canceló. */
    public Usuario getUsuarioRegistrado() {
        return usuarioRegistrado;
    }
}
