package views;

import javax.swing.*;
import java.awt.*;

/**
 * Ventana de inicio de sesión. Solo muestra los campos y expone getters;
 * la lógica está en LoginController.
 *
 * @author Equipo 1 - Autenticación y Red Familiar
 */
public class LoginView extends JFrame {
    private JTextField txtDocumento;
    private JPasswordField txtPassword;
    private JCheckBox chkMostrarPassword;
    private JButton btnIngresar;
    private JButton btnRegistrarse;

    public LoginView() {
        setTitle("Iniciar sesión - Sistema de Asistencia en Emergencias");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0;

        JLabel titulo = new JLabel("Bienvenido", SwingConstants.CENTER);
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 20f));
        gbc.gridy = 0; panel.add(titulo, gbc);

        gbc.gridy = 1; panel.add(new JLabel("Documento:"), gbc);
        txtDocumento = new JTextField(20);
        gbc.gridy = 2; panel.add(txtDocumento, gbc);

        gbc.gridy = 3; panel.add(new JLabel("Contraseña:"), gbc);
        txtPassword = new JPasswordField(20);
        gbc.gridy = 4; panel.add(txtPassword, gbc);

        // Casilla para ver u ocultar la contraseña (la lógica está en LoginController)
        chkMostrarPassword = new JCheckBox("Mostrar contraseña");
        gbc.gridy = 5; panel.add(chkMostrarPassword, gbc);

        JPanel panelBotones = new JPanel(new GridLayout(1, 2, 5, 5));
        btnIngresar = new JButton("Ingresar");
        btnRegistrarse = new JButton("Registrarse");
        panelBotones.add(btnIngresar);
        panelBotones.add(btnRegistrarse);
        gbc.gridy = 6; gbc.insets = new Insets(15, 5, 5, 5);
        panel.add(panelBotones, gbc);

        add(panel);
        getRootPane().setDefaultButton(btnIngresar); // Enter = Ingresar
        pack();
        setLocationRelativeTo(null);
    }

    public JTextField getTxtDocumento() { return txtDocumento; }
    public JPasswordField getTxtPassword() { return txtPassword; }
    public JCheckBox getChkMostrarPassword() { return chkMostrarPassword; }
    public JButton getBtnIngresar() { return btnIngresar; }
    public JButton getBtnRegistrarse() { return btnRegistrarse; }
}
