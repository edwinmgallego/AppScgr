package views;

import javax.swing.*;
import java.awt.*;

/**
 * Ventana para que el usuario edite sus datos (nombre y teléfono).
 * El documento se muestra, pero no se puede cambiar porque es con lo que inicia sesión.
 *
 * @author (escribe aquí tu nombre)
 */
public class PerfilView extends JDialog {
    private JTextField txtDocumento;
    private JTextField txtNombre;
    private JTextField txtTelefono;
    private JButton btnGuardar;
    private JButton btnCancelar;

    public PerfilView(Frame padre) {
        super(padre, "Editar mi perfil", true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 5, 4, 5);

        txtDocumento = new JTextField(20);
        txtDocumento.setEditable(false); // El documento no se puede cambiar
        txtNombre = new JTextField(20);
        txtTelefono = new JTextField(20);

        agregarFila(panel, gbc, 0, "Documento:", txtDocumento);
        agregarFila(panel, gbc, 1, "Nombre completo *:", txtNombre);
        agregarFila(panel, gbc, 2, "Teléfono:", txtTelefono);

        JPanel panelBotones = new JPanel(new GridLayout(1, 2, 5, 5));
        btnGuardar = new JButton("Guardar");
        btnCancelar = new JButton("Cancelar");
        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 5, 4, 5);
        panel.add(panelBotones, gbc);

        add(panel);
        getRootPane().setDefaultButton(btnGuardar);
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
    public JButton getBtnGuardar() { return btnGuardar; }
    public JButton getBtnCancelar() { return btnCancelar; }
}
