package views;

import models.Familiar;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
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

    private JTable tblMeTienen;
    private DefaultTableModel modeloMeTienen;
    private JButton btnAceptar;
    private JButton btnRechazar;

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

        modeloFamiliares = crearModelo("ID", "Documento", "Nombre", "Teléfono", "Estado");
        tblFamiliares = crearTabla(modeloFamiliares);
        colorearEstados(tblFamiliares);
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

        // --- 3. QUIÉN ME TIENE COMO FAMILIAR (se puede aceptar o rechazar) ---
        JPanel panelMeTienen = new JPanel(new BorderLayout(5, 5));
        modeloMeTienen = crearModelo("ID", "Documento", "Nombre", "Teléfono", "Estado");
        tblMeTienen = crearTabla(modeloMeTienen);
        panelMeTienen.add(new JScrollPane(tblMeTienen), BorderLayout.CENTER);

        btnAceptar = new JButton("Aceptar");
        btnRechazar = new JButton("Rechazar");
        JPanel surMeTienen = new JPanel(new BorderLayout(5, 5));
        surMeTienen.add(new JLabel(" Estas personas lo agregaron. Acepte para que aparezcan en \"Mis familiares\"."), BorderLayout.CENTER);
        JPanel botonesMeTienen = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botonesMeTienen.add(btnAceptar);
        botonesMeTienen.add(btnRechazar);
        surMeTienen.add(botonesMeTienen, BorderLayout.EAST);
        panelMeTienen.add(surMeTienen, BorderLayout.SOUTH);

        // Las dos listas van en pestañas para no hacer la ventana más grande
        JTabbedPane pestanas = new JTabbedPane();
        pestanas.addTab("Mis familiares", panelFamiliares);
        pestanas.addTab("Me tienen como familiar", panelMeTienen);

        add(panelBuscar);
        add(pestanas);
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

    // Pinta la columna "Estado" según el valor:
    // verde = SEGURO, rojo = NECESITA AYUDA, gris = DESCONOCIDO.
    // Si la fila está seleccionada se deja el color normal de selección, para que se vea cuál se eligió.
    private void colorearEstados(JTable tabla) {
        tabla.getColumn("Estado").setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object valor, boolean seleccionada,
                    boolean foco, int fila, int columna) {
                super.getTableCellRendererComponent(t, valor, seleccionada, foco, fila, columna);
                setHorizontalAlignment(SwingConstants.CENTER);
                setFont(getFont().deriveFont(Font.BOLD));
                if (!seleccionada) {
                    String estado = String.valueOf(valor);
                    if ("SEGURO".equals(estado)) {
                        setBackground(new Color(200, 240, 200)); // verde claro
                        setForeground(new Color(0, 110, 0));
                    } else if ("NECESITA AYUDA".equals(estado)) {
                        setBackground(new Color(255, 205, 205)); // rojo claro
                        setForeground(new Color(170, 0, 0));
                    } else {
                        setBackground(new Color(230, 230, 230)); // gris
                        setForeground(Color.DARK_GRAY);
                    }
                }
                return this;
            }
        });
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
    public JTable getTblMeTienen() { return tblMeTienen; }
    public DefaultTableModel getModeloMeTienen() { return modeloMeTienen; }
    public JButton getBtnAceptar() { return btnAceptar; }
    public JButton getBtnRechazar() { return btnRechazar; }
}