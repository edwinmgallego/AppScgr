package controllers;

import config.Sesion;
import models.Usuario;
import models.dao.UsuarioDAO;
import views.LoginView;
import views.MapaView;
import views.RegistroView;
import javax.swing.JOptionPane;

/**
 * Controla el inicio de sesión. Si el login es correcto guarda el usuario
 * en Sesion y abre la ventana principal del mapa.
 *
 * @author Equipo 1 - Autenticación y Red Familiar
 */
public class LoginController {
    private final LoginView vista;
    private final UsuarioDAO usuarioDAO;

    public LoginController(LoginView vista) {
        this.vista = vista;
        this.usuarioDAO = new UsuarioDAO();
        vista.getBtnIngresar().addActionListener(e -> ingresar());
        vista.getBtnRegistrarse().addActionListener(e -> abrirRegistro());
    }

    private void ingresar() {
        String documento = vista.getTxtDocumento().getText().trim();
        String password = new String(vista.getTxtPassword().getPassword());

        if (documento.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Escriba su documento y contraseña.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Usuario usuario = usuarioDAO.autenticar(documento, password);
        if (usuario == null) {
            JOptionPane.showMessageDialog(vista, "Documento o contraseña incorrectos.", "Error", JOptionPane.ERROR_MESSAGE);
            vista.getTxtPassword().setText("");
            return;
        }

        Sesion.iniciar(usuario);
        vista.dispose();
        new MapaController(new MapaView()).iniciar();
    }

    private void abrirRegistro() {
        RegistroController registro = new RegistroController(new RegistroView(vista));
        registro.iniciar();
        if (registro.getUsuarioRegistrado() != null) {
            vista.getTxtDocumento().setText(registro.getUsuarioRegistrado().getDocumento());
            vista.getTxtPassword().setText("");
            vista.getTxtPassword().requestFocusInWindow();
        }
    }

    public void iniciar() {
        vista.setVisible(true);
    }
}
