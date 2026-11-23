package views;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import models.EstadoReporte;
import models.ReporteExtraviado;
import models.TipoReporte;

public class ExtraviadosView extends JPanel {

    // Formulario
    private JComboBox<TipoReporte> cmbTipo;
    private JTextField txtNombre;
    private JComboBox<EstadoReporte> cmbEstado;
    private JTextField txtLatitud;
    private JTextField txtLongitud;
    private JTextArea txtCaracteristicas;

    // Filtros
    private JComboBox<Object> cmbFiltroTipo;
    private JComboBox<Object> cmbFiltroEstado;
    private JTextField txtFiltroTexto;
    private JButton btnFiltrar;
    private JButton btnVerTodos;

    // Tabla
    private JTable tablaReportes;
    private DefaultTableModel modeloTabla;

    // 5 Botones principales
    private JButton btnGuardar;
    private JButton btnActualizar;
    private JButton btnEliminar;
    private JButton btnLimpiar;
    private JButton btnBuscarCoincidencias;

    // ID del registro seleccionado en la tabla
    private Integer idSeleccionado = null;

    public ExtraviadosView() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(crearPanelFormulario(), BorderLayout.NORTH);
        add(crearPanelCentro(), BorderLayout.CENTER);
        add(crearPanelBotones(), BorderLayout.SOUTH);

        configurarSeleccionTabla();
    }

    private JPanel crearPanelFormulario() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Reporte de personas y mascotas extraviadas"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        cmbTipo = new JComboBox<>(TipoReporte.values());
        txtNombre = new JTextField(16);
        cmbEstado = new JComboBox<>(EstadoReporte.values());
        txtLatitud = new JTextField(10);
        txtLongitud = new JTextField(10);
        txtCaracteristicas = new JTextArea(3, 30);
        txtCaracteristicas.setLineWrap(true);
        txtCaracteristicas.setWrapStyleWord(true);
        JScrollPane scrollCarac = new JScrollPane(txtCaracteristicas);

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        panel.add(new JLabel("Tipo:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        panel.add(cmbTipo, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        panel.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 3; gbc.weightx = 1;
        panel.add(txtNombre, gbc);

        gbc.gridx = 4; gbc.weightx = 0;
        panel.add(new JLabel("Estado:"), gbc);
        gbc.gridx = 5; gbc.weightx = 1;
        panel.add(cmbEstado, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        panel.add(new JLabel("Latitud (opcional):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        panel.add(txtLatitud, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        panel.add(new JLabel("Longitud (opcional):"), gbc);
        gbc.gridx = 3; gbc.weightx = 1;
        panel.add(txtLongitud, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        panel.add(new JLabel("Características:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 5; gbc.weightx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(scrollCarac, gbc);
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        return panel;
    }

    private JPanel crearPanelCentro() {
        JPanel centro = new JPanel(new BorderLayout(8, 8));

        JPanel panelFiltros = new JPanel();
        panelFiltros.setBorder(BorderFactory.createTitledBorder("Filtros"));

        cmbFiltroTipo = new JComboBox<>();
        cmbFiltroTipo.addItem("Todos");
        for (TipoReporte t : TipoReporte.values()) {
            cmbFiltroTipo.addItem(t);
        }

        cmbFiltroEstado = new JComboBox<>();
        cmbFiltroEstado.addItem("Todos");
        for (EstadoReporte e : EstadoReporte.values()) {
            cmbFiltroEstado.addItem(e);
        }

        txtFiltroTexto = new JTextField(16);
        btnFiltrar = new JButton("Filtrar");
        btnVerTodos = new JButton("Ver todos");

        panelFiltros.add(new JLabel("Tipo:"));
        panelFiltros.add(cmbFiltroTipo);
        panelFiltros.add(new JLabel("Estado:"));
        panelFiltros.add(cmbFiltroEstado);
        panelFiltros.add(new JLabel("Buscar:"));
        panelFiltros.add(txtFiltroTexto);
        panelFiltros.add(btnFiltrar);
        panelFiltros.add(btnVerTodos);

        String[] columnas = {"ID", "Tipo", "Nombre", "Estado", "Características", "Latitud", "Longitud", "Fecha"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaReportes = new JTable(modeloTabla);
        JScrollPane scrollTabla = new JScrollPane(tablaReportes);
        scrollTabla.setBorder(BorderFactory.createTitledBorder("Listado de reportes"));

        centro.add(panelFiltros, BorderLayout.NORTH);
        centro.add(scrollTabla, BorderLayout.CENTER);
        return centro;
    }

    private JPanel crearPanelBotones() {
        JPanel panelBotones = new JPanel();
        btnGuardar = new JButton("Guardar");
        btnActualizar = new JButton("Actualizar");
        btnEliminar = new JButton("Eliminar");
        btnLimpiar = new JButton("Limpiar");
        btnBuscarCoincidencias = new JButton("Buscar coincidencias");

        panelBotones.add(btnGuardar);
        panelBotones.add(btnActualizar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnLimpiar);
        panelBotones.add(btnBuscarCoincidencias);

        return panelBotones;
    }

    private void configurarSeleccionTabla() {
        tablaReportes.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int fila = tablaReportes.getSelectedRow();
                if (fila >= 0) {
                    Object idObj = modeloTabla.getValueAt(fila, 0);
                    idSeleccionado = (idObj instanceof Integer) ? (Integer) idObj : Integer.valueOf(idObj.toString());

                    Object tipoObj = modeloTabla.getValueAt(fila, 1);
                    if (tipoObj instanceof TipoReporte) {
                        cmbTipo.setSelectedItem(tipoObj);
                    } else if (tipoObj != null) {
                        cmbTipo.setSelectedItem(TipoReporte.valueOf(tipoObj.toString()));
                    }

                    Object nombreObj = modeloTabla.getValueAt(fila, 2);
                    txtNombre.setText(nombreObj != null ? nombreObj.toString() : "");

                    Object estadoObj = modeloTabla.getValueAt(fila, 3);
                    if (estadoObj instanceof EstadoReporte) {
                        cmbEstado.setSelectedItem(estadoObj);
                    } else if (estadoObj != null) {
                        cmbEstado.setSelectedItem(EstadoReporte.valueOf(estadoObj.toString()));
                    }

                    Object caracObj = modeloTabla.getValueAt(fila, 4);
                    txtCaracteristicas.setText(caracObj != null ? caracObj.toString() : "");

                    Object latObj = modeloTabla.getValueAt(fila, 5);
                    txtLatitud.setText(latObj != null ? latObj.toString() : "");

                    Object lonObj = modeloTabla.getValueAt(fila, 6);
                    txtLongitud.setText(lonObj != null ? lonObj.toString() : "");
                }
            }
        });
    }

    // --- Métodos públicos para el controlador ---

    public ReporteExtraviado construirReporteDesdeFormulario() {
        String nombre = txtNombre.getText().trim();
        String caracteristicas = txtCaracteristicas.getText().trim();

        if (nombre.isEmpty() || caracteristicas.isEmpty()) {
            mostrarError("Debes ingresar nombre y características.");
            return null;
        }

        Double latitud = parsearCoordenada(txtLatitud.getText().trim(), "latitud");
        if (latitud == null && !txtLatitud.getText().trim().isEmpty()) {
            return null;
        }

        Double longitud = parsearCoordenada(txtLongitud.getText().trim(), "longitud");
        if (longitud == null && !txtLongitud.getText().trim().isEmpty()) {
            return null;
        }

        TipoReporte tipo = (TipoReporte) cmbTipo.getSelectedItem();
        EstadoReporte estado = (EstadoReporte) cmbEstado.getSelectedItem();

        ReporteExtraviado reporte = new ReporteExtraviado(tipo, nombre, caracteristicas, estado, latitud, longitud);
        if (idSeleccionado != null) {
            reporte.setId(idSeleccionado);
        }
        return reporte;
    }

    private Double parsearCoordenada(String texto, String campo) {
        if (texto.isEmpty()) {
            return null;
        }
        try {
            return Double.valueOf(texto.replace(',', '.'));
        } catch (NumberFormatException ex) {
            mostrarError("La " + campo + " debe ser un número válido o dejarse vacía.");
            return null;
        }
    }

    public void setReportes(List<ReporteExtraviado> reportes) {
        modeloTabla.setRowCount(0);
        if (reportes == null) {
            return;
        }
        for (ReporteExtraviado r : reportes) {
            modeloTabla.addRow(new Object[]{
                r.getId(),
                r.getTipo(),
                r.getNombre(),
                r.getEstado(),
                r.getCaracteristicas(),
                r.getLatitudVisto(),
                r.getLongitudVisto(),
                r.getFechaReporte()
            });
        }
    }

    public void limpiarFormulario() {
        idSeleccionado = null;
        tablaReportes.clearSelection();
        cmbTipo.setSelectedIndex(0);
        txtNombre.setText("");
        cmbEstado.setSelectedIndex(0);
        txtLatitud.setText("");
        txtLongitud.setText("");
        txtCaracteristicas.setText("");
    }

    public void limpiarFiltros() {
        cmbFiltroTipo.setSelectedIndex(0);
        cmbFiltroEstado.setSelectedIndex(0);
        txtFiltroTexto.setText("");
    }

    public void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Información", JOptionPane.INFORMATION_MESSAGE);
    }

    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.WARNING_MESSAGE);
    }

    public boolean confirmarEliminacion(String nombreReporte) {
        String detalle = (nombreReporte == null || nombreReporte.isBlank())
                ? "este reporte"
                : "el reporte de \"" + nombreReporte + "\"";
        int opcion = JOptionPane.showConfirmDialog(
                this,
                "¿Seguro que deseas eliminar " + detalle + "?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
        return opcion == JOptionPane.YES_OPTION;
    }

    public Integer getIdSeleccionado() {
        return idSeleccionado;
    }

    public TipoReporte getFiltroTipo() {
        Object sel = cmbFiltroTipo.getSelectedItem();
        return (sel instanceof TipoReporte) ? (TipoReporte) sel : null;
    }

    public EstadoReporte getFiltroEstado() {
        Object sel = cmbFiltroEstado.getSelectedItem();
        return (sel instanceof EstadoReporte) ? (EstadoReporte) sel : null;
    }

    public String getFiltroTexto() {
        String texto = txtFiltroTexto.getText().trim();
        return texto.isEmpty() ? null : texto;
    }

    public void addGuardarListener(ActionListener listener) {
        btnGuardar.addActionListener(listener);
    }

    public void addActualizarListener(ActionListener listener) {
        btnActualizar.addActionListener(listener);
    }

    public void addEliminarListener(ActionListener listener) {
        btnEliminar.addActionListener(listener);
    }

    public void addLimpiarListener(ActionListener listener) {
        btnLimpiar.addActionListener(listener);
    }

    public void addBuscarCoincidenciasListener(ActionListener listener) {
        btnBuscarCoincidencias.addActionListener(listener);
    }

    public void addFiltrarListener(ActionListener listener) {
        btnFiltrar.addActionListener(listener);
    }

    public void addVerTodosListener(ActionListener listener) {
        btnVerTodos.addActionListener(listener);
    }
}