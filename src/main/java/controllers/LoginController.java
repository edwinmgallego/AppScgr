package controllers;

import config.Sesion;
import models.Usuario;
import models.dao.UsuarioDAO;
import views.LoginView;
import views.MapaView;
import views.RegistroView;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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

    // Reemplaza la ventana de login por la de registro (no abre una ventana emergente encima)
    private void abrirRegistro() {
        RegistroController registro = new RegistroController(new RegistroView());

        // Cuando la ventana de registro se cierre (por registrarse o por cancelar) se vuelve al login.
        // Como el registro ya no es modal, el código no se queda esperando: hay que "escuchar" el cierre.
        registro.getVista().addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                volverAlLogin(registro);
            }
        });

        vista.setVisible(false); // oculta el login (no se cierra, así conserva lo escrito)
        registro.iniciar();      // muestra el registro en su lugar
    }

    // Muestra otra vez el login; si el usuario se acaba de registrar, deja su documento escrito
    private void volverAlLogin(RegistroController registro) {
        if (registro.getUsuarioRegistrado() != null) {
            vista.getTxtDocumento().setText(registro.getUsuarioRegistrado().getDocumento());
            vista.getTxtPassword().setText("");
        }
        vista.setVisible(true);
        vista.getTxtPassword().requestFocusInWindow();
    }

    public void iniciar() {
        vista.setVisible(true);
    }
}
