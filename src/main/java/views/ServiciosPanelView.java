package views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/*
 * PROPOSITO GENERAL: Vista principal del Equipo 5 (RF-06 y RF-08) construida como JPanel
 * para integrarse dentro del Dashboard lateral del Equipo 9 sin abrir ventanas externas.
 *
 * ELEMENTOS CLAVE PARA SUSTENTACION:
 * - Arquitectura MVC estricta: La vista solo captura entradas del usuario y expone Getters
 *   y metodos de limpieza para que ServiciosController gestione los eventos y llame al DAO.
 */
public class ServiciosPanelView extends JPanel {

    private JTabbedPane pestanasModulo;
    private JPanel panelServiciosBarrio;
    private JPanel panelCoberturaMovil;

    // Componentes Pestaña 1 (RF-06)
    private JTextField txtBarrioServicio;
    private JComboBox<String> cmbTipoServicio;
    private JComboBox<String> cmbEstadoServicio;
    private JTextField txtObservacionesServicio;
    private JButton btnGuardarServicio;
    private JTextField txtBuscarBarrio;
    private JButton btnBuscarBarrio;
    private JTable tablaServiciosBarrio;
    private DefaultTableModel modeloTablaServicios;

    // Componentes Pestaña 2 (RF-08)
    private JTextField txtBarrioMovil;
    private JComboBox<String> cmbOperadorMovil;
    private JComboBox<String> cmbNivelFallaMovil;
    private JTextField txtLatitudMovil;
    private JTextField txtLongitudMovil;
    private JTextField txtObservacionesMovil;
    private JButton btnGuardarMovil;
    private JTable tablaCoberturaMovil;
    private DefaultTableModel modeloTablaMovil;

    public ServiciosPanelView() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lblTitulo = new JLabel("Monitoreo de Servicios Públicos y Señal Móvil (Equipo 5)");
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 16));
        add(lblTitulo, BorderLayout.NORTH);

        pestanasModulo = new JTabbedPane();
        inicializarPestanaServicios();
        inicializarPestanaCoberturaMovil();

        pestanasModulo.addTab("Servicios por Barrio (RF-06)", panelServiciosBarrio);
        pestanasModulo.addTab("Cobertura Móvil GPS (RF-08)", panelCoberturaMovil);

        add(pestanasModulo, BorderLayout.CENTER);
    }

    private void inicializarPestanaServicios() {
        panelServiciosBarrio = new JPanel(new BorderLayout(10, 10));
        panelServiciosBarrio.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel panelFormulario = new JPanel(new GridLayout(5, 2, 8, 8));
        panelFormulario.setBorder(BorderFactory.createTitledBorder("Reportar Estado de Servicio por Zona"));

        txtBarrioServicio = new JTextField();
        cmbTipoServicio = new JComboBox<>(new String[]{"AGUA", "ENERGIA", "TELECOMUNICACIONES"});
        cmbEstadoServicio = new JComboBox<>(new String[]{"OPERANDO", "CON_FALLAS", "CAIDO"});
        txtObservacionesServicio = new JTextField();
        btnGuardarServicio = new JButton("Registrar Reporte de Servicio");

        panelFormulario.add(new JLabel("Barrio / Zona:"));
        panelFormulario.add(txtBarrioServicio);
        panelFormulario.add(new JLabel("Servicio Público:"));
        panelFormulario.add(cmbTipoServicio);
        panelFormulario.add(new JLabel("Estado Actual:"));
        panelFormulario.add(cmbEstadoServicio);
        panelFormulario.add(new JLabel("Observaciones:"));
        panelFormulario.add(txtObservacionesServicio);
        panelFormulario.add(new JLabel(""));
        panelFormulario.add(btnGuardarServicio);

        JPanel panelCentro = new JPanel(new BorderLayout(5, 5));
        JPanel panelFiltro = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txtBuscarBarrio = new JTextField(20);
        btnBuscarBarrio = new JButton("Filtrar por Barrio");
        panelFiltro.add(new JLabel("Consultar Barrio:"));
        panelFiltro.add(txtBuscarBarrio);
        panelFiltro.add(btnBuscarBarrio);

        String[] columnas = {"ID", "Barrio / Zona", "Servicio", "Estado", "Observaciones", "Votos", "Actualización"};
        modeloTablaServicios = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaServiciosBarrio = new JTable(modeloTablaServicios);
        aplicarSemaforoEstado(tablaServiciosBarrio);

        panelCentro.add(panelFiltro, BorderLayout.NORTH);
        panelCentro.add(new JScrollPane(tablaServiciosBarrio), BorderLayout.CENTER);

        panelServiciosBarrio.add(panelFormulario, BorderLayout.NORTH);
        panelServiciosBarrio.add(panelCentro, BorderLayout.CENTER);
    }

    private void aplicarSemaforoEstado(JTable tabla) {
        tabla.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String estado = value != null ? value.toString() : "";
                if ("CAIDO".equalsIgnoreCase(estado)) {
                    c.setBackground(new Color(255, 204, 204));
                    c.setForeground(new Color(153, 0, 0));
                } else if ("CON_FALLAS".equalsIgnoreCase(estado)) {
                    c.setBackground(new Color(255, 243, 205));
                    c.setForeground(new Color(133, 100, 4));
                } else {
                    c.setBackground(new Color(212, 237, 218));
                    c.setForeground(new Color(21, 87, 36));
                }
                return c;
            }
        });
    }

    private void inicializarPestanaCoberturaMovil() {
        panelCoberturaMovil = new JPanel(new BorderLayout(10, 10));
        panelCoberturaMovil.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel panelFormulario = new JPanel(new GridLayout(7, 2, 8, 8));
        panelFormulario.setBorder(BorderFactory.createTitledBorder("Reportar Falta de Cobertura Móvil en Coordenadas"));

        txtBarrioMovil = new JTextField();
        cmbOperadorMovil = new JComboBox<>(new String[]{"Claro", "Movistar", "Tigo", "WOM"});
        cmbNivelFallaMovil = new JComboBox<>(new String[]{"SIN_SENAL", "INTERMITENTE"});
        txtLatitudMovil = new JTextField("3.4516");
        txtLongitudMovil = new JTextField("-76.5320");
        txtObservacionesMovil = new JTextField();
        btnGuardarMovil = new JButton("Reportar Falla de Señal");

        panelFormulario.add(new JLabel("Barrio / Sector:"));
        panelFormulario.add(txtBarrioMovil);
        panelFormulario.add(new JLabel("Operador Móvil:"));
        panelFormulario.add(cmbOperadorMovil);
        panelFormulario.add(new JLabel("Nivel de Falla:"));
        panelFormulario.add(cmbNivelFallaMovil);
        panelFormulario.add(new JLabel("Latitud GPS:"));
        panelFormulario.add(txtLatitudMovil);
        panelFormulario.add(new JLabel("Longitud GPS:"));
        panelFormulario.add(txtLongitudMovil);
        panelFormulario.add(new JLabel("Observaciones:"));
        panelFormulario.add(txtObservacionesMovil);
        panelFormulario.add(new JLabel(""));
        panelFormulario.add(btnGuardarMovil);

        String[] columnas = {"ID", "Descripción / Operador", "Latitud", "Longitud", "Votos", "Fecha"};
        modeloTablaMovil = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaCoberturaMovil = new JTable(modeloTablaMovil);

        panelCoberturaMovil.add(panelFormulario, BorderLayout.NORTH);
        panelCoberturaMovil.add(new JScrollPane(tablaCoberturaMovil), BorderLayout.CENTER);
    }

    public void limpiarFormularioServicios() {
        txtBarrioServicio.setText("");
        txtObservacionesServicio.setText("");
        cmbTipoServicio.setSelectedIndex(0);
        cmbEstadoServicio.setSelectedIndex(0);
    }

    public void limpiarFormularioMovil() {
        txtBarrioMovil.setText("");
        txtObservacionesMovil.setText("");
        cmbOperadorMovil.setSelectedIndex(0);
        cmbNivelFallaMovil.setSelectedIndex(0);
    }

    // --- GETTERS PARA EL CONTROLADOR (MVC) ---
    public JTabbedPane getPestanasModulo() { return pestanasModulo; }
    public JTextField getTxtBarrioServicio() { return txtBarrioServicio; }
    public JComboBox<String> getCmbTipoServicio() { return cmbTipoServicio; }
    public JComboBox<String> getCmbEstadoServicio() { return cmbEstadoServicio; }
    public JTextField getTxtObservacionesServicio() { return txtObservacionesServicio; }
    public JButton getBtnGuardarServicio() { return btnGuardarServicio; }
    public JTextField getTxtBuscarBarrio() { return txtBuscarBarrio; }
    public JButton getBtnBuscarBarrio() { return btnBuscarBarrio; }
    public DefaultTableModel getModeloTablaServicios() { return modeloTablaServicios; }

    public JTextField getTxtBarrioMovil() { return txtBarrioMovil; }
    public JComboBox<String> getCmbOperadorMovil() { return cmbOperadorMovil; }
    public JComboBox<String> getCmbNivelFallaMovil() { return cmbNivelFallaMovil; }
    public JTextField getTxtLatitudMovil() { return txtLatitudMovil; }
    public JTextField getTxtLongitudMovil() { return txtLongitudMovil; }
    public JTextField getTxtObservacionesMovil() { return txtObservacionesMovil; }
    public JButton getBtnGuardarMovil() { return btnGuardarMovil; }
    public DefaultTableModel getModeloTablaMovil() { return modeloTablaMovil; }
}
