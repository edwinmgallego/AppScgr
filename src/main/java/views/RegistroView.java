package views;

import javax.swing.*;
import java.awt.*;

/**
 * Formulario de registro de un usuario nuevo (ventana modal sobre el login).
 *
 * @author Equipo 1 - Autenticación y Red Familiar
 */
public class RegistroView extends JDialog {
    private JTextField txtDocumento;
    private JTextField txtNombre;
    private JTextField txtTelefono;
    private JPasswordField txtPassword;
    private JPasswordField txtConfirmar;
    private JButton btnRegistrar;
    private JButton btnCancelar;

    public RegistroView(Frame padre) {
        super(padre, "Registro de usuario", true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
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
        agregarFila(panel, gbc, 3, "Contraseña * (mín. 6):", txtPassword);
        agregarFila(panel, gbc, 4, "Confirmar contraseña *:", txtConfirmar);

        JPanel panelBotones = new JPanel(new GridLayout(1, 2, 5, 5));
        btnRegistrar = new JButton("Registrar");
        btnCancelar = new JButton("Cancelar");
        panelBotones.add(btnRegistrar);
        panelBotones.add(btnCancelar);
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 5, 4, 5);
        panel.add(panelBotones, gbc);

        add(panel);
        getRootPane().setDefaultButton(btnRegistrar);
        pack();
        setLocationRelativeTo(padre);
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
    public JButton getBtnRegistrar() { return btnRegistrar; }
    public JButton getBtnCancelar() { return btnCancelar; }
}
