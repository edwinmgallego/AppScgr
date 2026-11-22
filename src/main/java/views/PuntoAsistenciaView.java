
package views;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class PuntoAsistenciaView extends JDialog {

    private static final String[] TIPOS = {
        "ALBERGUE", "CENTRO_ACOPIO", "SUPERMERCADO",
        "MECANICO", "HOSPITAL"
    };

    private final JTextField txtNombre = new JTextField();
    private final JTextField txtLatitud = new JTextField();
    private final JTextField txtLongitud = new JTextField();
    private final JTextField txtServicios = new JTextField();

    private final JComboBox<String> cbxTipo = new JComboBox<>(TIPOS);
    private final JComboBox<String> cbxEstado =
            new JComboBox<>(new String[]{"ABIERTO", "CERRADO"});

    private final JSpinner spnVoluntarios =
            new JSpinner(new SpinnerNumberModel(0, 0, 999, 1));

    private final JComboBox<String> cbxFiltroTipo;

    private final DefaultTableModel modeloPuntos;
    private final JTable tblPuntos;

    private final JButton btnNuevo = new JButton("Nuevo");
    private final JButton btnGuardar = new JButton("Guardar");
    private final JButton btnCambiarEstado =
            new JButton("Abrir / cerrar");
    private final JButton btnEliminar = new JButton("Eliminar");

    public PuntoAsistenciaView(Frame padre) {
        super(padre, "Puntos de asistencia", true);

        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(950, 560);
        setLayout(new BorderLayout(8, 8));

        JPanel panelForm = new JPanel(new GridLayout(0, 4, 8, 6));
        panelForm.setBorder(BorderFactory.createTitledBorder("Datos del punto"));

        panelForm.add(new JLabel("Nombre:"));
        panelForm.add(txtNombre);

        panelForm.add(new JLabel("Tipo:"));
        panelForm.add(cbxTipo);

        panelForm.add(new JLabel("Latitud:"));
        panelForm.add(txtLatitud);

        panelForm.add(new JLabel("Longitud:"));
        panelForm.add(txtLongitud);

        panelForm.add(new JLabel("Estado:"));
        panelForm.add(cbxEstado);

        panelForm.add(new JLabel("Voluntarios necesarios:"));
        panelForm.add(spnVoluntarios);

        panelForm.add(new JLabel("Servicios (separados por coma):"));
        panelForm.add(txtServicios);

        panelForm.add(new JLabel(""));
        panelForm.add(new JLabel(""));

        add(panelForm, BorderLayout.NORTH);

        modeloPuntos = new DefaultTableModel(
                new String[]{
                    "ID", "Tipo", "Nombre", "Latitud",
                    "Longitud", "Estado", "Servicios", "Voluntarios"
                }, 0) {

            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        tblPuntos = new JTable(modeloPuntos);
        tblPuntos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblPuntos.removeColumn(tblPuntos.getColumnModel().getColumn(0));

        JPanel panelLista = new JPanel(new BorderLayout(5, 5));

        String[] filtros = new String[TIPOS.length + 1];
        filtros[0] = "TODOS";

        System.arraycopy(TIPOS, 0, filtros, 1, TIPOS.length);

        cbxFiltroTipo = new JComboBox<>(filtros);

        JPanel barraFiltro = new JPanel(new FlowLayout(FlowLayout.LEFT));
        barraFiltro.add(new JLabel("Filtrar por tipo:"));
        barraFiltro.add(cbxFiltroTipo);

        panelLista.add(barraFiltro, BorderLayout.NORTH);
        panelLista.add(new JScrollPane(tblPuntos), BorderLayout.CENTER);

        add(panelLista, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        panelBotones.add(btnNuevo);
        panelBotones.add(btnGuardar);
        panelBotones.add(btnCambiarEstado);
        panelBotones.add(btnEliminar);

        add(panelBotones, BorderLayout.SOUTH);

        setLocationRelativeTo(padre);
    }

    public JTable getTblPuntos() {
        return tblPuntos;
    }

    public DefaultTableModel getModeloPuntos() {
        return modeloPuntos;
    }

    public JTextField getTxtNombre() {
        return txtNombre;
    }

    public JTextField getTxtLatitud() {
        return txtLatitud;
    }

    public JTextField getTxtLongitud() {
        return txtLongitud;
    }

    public JComboBox<String> getCbxTipo() {
        return cbxTipo;
    }

    public JComboBox<String> getCbxEstado() {
        return cbxEstado;
    }

    public JTextField getTxtServicios() {
        return txtServicios;
    }

    public JSpinner getSpnVoluntarios() {
        return spnVoluntarios;
    }

    public JComboBox<String> getCbxFiltroTipo() {
        return cbxFiltroTipo;
    }

    public JButton getBtnNuevo() {
        return btnNuevo;
    }

    public JButton getBtnGuardar() {
        return btnGuardar;
    }

    public JButton getBtnCambiarEstado() {
        return btnCambiarEstado;
    }

    public JButton getBtnEliminar() {
        return btnEliminar;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PuntoAsistenciaView ventana = new PuntoAsistenciaView(null);
            ventana.setVisible(true);
        });
    }
}
