package views;

import utils.PasswordUtil;
import javax.swing.*;
import java.awt.*;

/**
 * Formulario de registro de un usuario nuevo. Es una ventana normal (JFrame), no una
 * ventana emergente: LoginController oculta el login mientras esta ventana está abierta
 * y lo vuelve a mostrar cuando se cierra.
 *
 * @author Equipo 1 - Autenticación y Red Familiar
 */
public class RegistroView extends JFrame {
    private JTextField txtDocumento;
    private JTextField txtNombre;
    private JTextField txtTelefono;
    private JPasswordField txtPassword;
    private JPasswordField txtConfirmar;
    private JCheckBox chkMostrarPassword;
    private JButton btnRegistrar;
    private JButton btnCancelar;

    public RegistroView() {
        setTitle("Registro de usuario");
        // DISPOSE_ON_CLOSE: al cerrar solo se cierra el registro (no toda la app) y el login vuelve a aparecer
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 5, 4, 5);

        txtDocumento = new JTextField(20);
        txtNombre = new JTextField(20);
        txtTelefono = new JTextField(20);
        txtPassword = new JPasswordField(20);
        txtConfirmar = new JPasswordField(20);

        agregarFila(panel, gbc, 0, "Documento *:", txtDocumento);
        agregarFila(panel, gbc, 1, "Nombre completo *:", txtNombre);
        agregarFila(panel, gbc, 2, "Teléfono:", txtTelefono);
        agregarFila(panel, gbc, 3, "Contraseña *:", txtPassword);
        agregarFila(panel, gbc, 4, "Confirmar contraseña *:", txtConfirmar);

        // Casilla para ver u ocultar las contraseñas (la lógica está en RegistroController)
        chkMostrarPassword = new JCheckBox("Mostrar contraseña");
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        panel.add(chkMostrarPassword, gbc);

        // Texto de ayuda: le muestra al usuario qué debe cumplir su contraseña
        JLabel lblAyuda = new JLabel("<html><body style='width:260px'>La contraseña debe tener mínimo "
                + PasswordUtil.MINIMO_CARACTERES + " caracteres, una mayúscula, una minúscula y un carácter especial (ej. @ # $ !).</body></html>");
        lblAyuda.setFont(lblAyuda.getFont().deriveFont(Font.ITALIC, 11f));
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        panel.add(lblAyuda, gbc);

        JPanel panelBotones = new JPanel(new GridLayout(1, 2, 5, 5));
        btnRegistrar = new JButton("Registrar");
        btnCancelar = new JButton("Cancelar");
        panelBotones.add(btnRegistrar);
        panelBotones.add(btnCancelar);
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 5, 4, 5);
        panel.add(panelBotones, gbc);

        add(panel);
        getRootPane().setDefaultButton(btnRegistrar);
        pack();
        setLocationRelativeTo(null); // centrada, en el mismo lugar donde estaba el login
    }

    private void agregarFila(JPanel panel, GridBagConstraints gbc, int fila, String etiqueta, JComponent campo) {
        gbc.gridy = fila;
        gbc.gridx = 0; gbc.weightx = 0;
        panel.add(new JLabel(etiqueta), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        panel.add(campo, gbc);
    }

    public JTextField getTxtDocumento() { return txtDocumento; }
    public JTextField getTxtNombre() { return txtNombre; }
    public JTextField getTxtTelefono() { return txtTelefono; }
    public JPasswordField getTxtPassword() { return txtPassword; }
    public JPasswordField getTxtConfirmar() { return txtConfirmar; }
    public JCheckBox getChkMostrarPassword() { return chkMostrarPassword; }
    public JButton getBtnRegistrar() { return btnRegistrar; }
    public JButton getBtnCancelar() { return btnCancelar; }
}
