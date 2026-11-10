package views;

import models.Familiar;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Ventana de la red familiar: arriba se buscan usuarios registrados para vincularlos,
 * abajo se ven los familiares ya vinculados.
 *
 * @author Equipo 1 - Autenticación y Red Familiar
 */
public class FamiliaresView extends JDialog {
    private JTextField txtBuscar;
    private JButton btnBuscar;
    private JTable tblResultados;
    private DefaultTableModel modeloResultados;
    private JButton btnVincular;

    private JTable tblFamiliares;
    private DefaultTableModel modeloFamiliares;
    private JComboBox<String> cbxEstado;
    private JButton btnCambiarEstado;
    private JButton btnDesvincular;
    private JButton btnPrincipal;

    public FamiliaresView(Frame padre) {
        super(padre, "Mi red familiar", true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(900, 520);
        setLayout(new GridLayout(2, 1, 0, 8));

        // --- 1. BUSCAR USUARIOS ---
        JPanel panelBuscar = new JPanel(new BorderLayout(5, 5));
        panelBuscar.setBorder(BorderFactory.createTitledBorder("Buscar familiar (documento o nombre)"));

        JPanel barraBusqueda = new JPanel(new BorderLayout(5, 5));
        txtBuscar = new JTextField();
        btnBuscar = new JButton("Buscar");
        barraBusqueda.add(txtBuscar, BorderLayout.CENTER);
        barraBusqueda.add(btnBuscar, BorderLayout.EAST);
        panelBuscar.add(barraBusqueda, BorderLayout.NORTH);

        modeloResultados = crearModelo("ID", "Documento", "Nombre", "Teléfono");
        tblResultados = crearTabla(modeloResultados);
        panelBuscar.add(new JScrollPane(tblResultados), BorderLayout.CENTER);

        btnVincular = new JButton("Vincular como familiar");
        JPanel surBuscar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        surBuscar.add(btnVincular);
        panelBuscar.add(surBuscar, BorderLayout.SOUTH);

        // --- 2. FAMILIARES VINCULADOS ---
        JPanel panelFamiliares = new JPanel(new BorderLayout(5, 5));
        panelFamiliares.setBorder(BorderFactory.createTitledBorder("Mis familiares"));

        modeloFamiliares = crearModelo("ID", "Documento", "Nombre", "Teléfono", "Estado");
        tblFamiliares = crearTabla(modeloFamiliares);
        panelFamiliares.add(new JScrollPane(tblFamiliares), BorderLayout.CENTER);

        JPanel surFamiliares = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        cbxEstado = new JComboBox<>(Familiar.ESTADOS);
        btnCambiarEstado = new JButton("Cambiar estado");
        btnDesvincular = new JButton("Desvincular");
        btnPrincipal = new JButton("Contacto principal");
        surFamiliares.add(new JLabel("Estado:"));
        surFamiliares.add(cbxEstado);
        surFamiliares.add(btnCambiarEstado);
        surFamiliares.add(btnPrincipal);
        surFamiliares.add(btnDesvincular);
        panelFamiliares.add(surFamiliares, BorderLayout.SOUTH);

        add(panelBuscar);
        add(panelFamiliares);
        getRootPane().setDefaultButton(btnBuscar);
        setLocationRelativeTo(padre);
    }

    private DefaultTableModel crearModelo(String... columnas) {
        return new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false; // Las tablas son solo de lectura
            }
        };
    }

    private JTable crearTabla(DefaultTableModel modelo) {
        JTable tabla = new JTable(modelo);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // Ocultar la columna ID (se usa internamente, no se muestra)
        tabla.removeColumn(tabla.getColumnModel().getColumn(0));
        return tabla;
    }

    public JTextField getTxtBuscar() { return txtBuscar; }
    public JButton getBtnBuscar() { return btnBuscar; }
    public JTable getTblResultados() { return tblResultados; }
    public DefaultTableModel getModeloResultados() { return modeloResultados; }
    public JButton getBtnVincular() { return btnVincular; }
    public JTable getTblFamiliares() { return tblFamiliares; }
    public DefaultTableModel getModeloFamiliares() { return modeloFamiliares; }
    public JComboBox<String> getCbxEstado() { return cbxEstado; }
    public JButton getBtnCambiarEstado() { return btnCambiarEstado; }
    public JButton getBtnDesvincular() { return btnDesvincular; }
    public JButton getBtnPrincipal() { return btnPrincipal; }
}