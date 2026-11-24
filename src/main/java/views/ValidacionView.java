package views;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FlowLayout;

/**
 * Vista del módulo de validación comunitaria (RF-13).
 * Muestra los reportes, sus votos y una etiqueta visual que diferencia
 * información oficial/verificada de los reportes ciudadanos.
 *
 * @author Grupo 9 - Validación Comunitaria y Dashboard
 */
public class ValidacionView extends JFrame {

    private final DefaultTableModel modeloTabla;
    private final JTable tabla;
    private final JButton btnConfirmar;
    private final JButton btnCuestionar;
    private final JButton btnActualizar;
    private final JButton btnCerrar;
    private final JLabel lblEstado;
    private final JLabel lblLeyenda;

    public ValidacionView() {
        setTitle("Validación comunitaria de reportes - Grupo 9");
        setSize(1000, 560);
        setMinimumSize(new Dimension(850, 480));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel titulo = new JLabel("Validación comunitaria de reportes", SwingConstants.CENTER);
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 18f));
        titulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(titulo, BorderLayout.NORTH);

        String[] columnas = {
            "ID", "Tipo", "Descripción", "Latitud", "Longitud",
            "Origen / Estado", "Confirmaciones", "Cuestionamientos"
        };
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        tabla = new JTable(modeloTabla);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setRowHeight(24);
        tabla.getColumnModel().getColumn(0).setPreferredWidth(45);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(120);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(300);
        tabla.getColumnModel().getColumn(5).setPreferredWidth(170);
        tabla.getColumnModel().getColumn(6).setPreferredWidth(100);
        tabla.getColumnModel().getColumn(7).setPreferredWidth(110);
        tabla.getColumnModel().getColumn(5).setCellRenderer(new EstadoRenderer());
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        JPanel panelSur = new JPanel(new BorderLayout());
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 8));
        btnConfirmar = new JButton("Confirmar reporte");
        btnCuestionar = new JButton("Cuestionar reporte");
        btnActualizar = new JButton("Actualizar lista");
        btnCerrar = new JButton("Cerrar");
        panelBotones.add(btnConfirmar);
        panelBotones.add(btnCuestionar);
        panelBotones.add(btnActualizar);
        panelBotones.add(btnCerrar);
        panelSur.add(panelBotones, BorderLayout.NORTH);

        lblLeyenda = new JLabel("<html><b>Leyenda:</b> "
                + "<span style='color:#1565C0'><b>OFICIAL VERIFICADO</b></span> | "
                + "<span style='color:#2E7D32'><b>VERIFICADO POR COMUNIDAD</b></span> | "
                + "<span style='color:#C62828'><b>CUESTIONADO</b></span> | "
                + "<span style='color:#424242'>REPORTE CIUDADANO</span></html>");
        lblLeyenda.setBorder(BorderFactory.createEmptyBorder(0, 10, 4, 10));
        panelSur.add(lblLeyenda, BorderLayout.CENTER);

        lblEstado = new JLabel(" ");
        lblEstado.setBorder(BorderFactory.createEmptyBorder(4, 10, 8, 10));
        panelSur.add(lblEstado, BorderLayout.SOUTH);
        add(panelSur, BorderLayout.SOUTH);
    }

    public void setEstado(String texto) {
        lblEstado.setText(texto);
    }

    public JTable getTabla() {
        return tabla;
    }

    public DefaultTableModel getModeloTabla() {
        return modeloTabla;
    }

    public JButton getBtnConfirmar() {
        return btnConfirmar;
    }

    public JButton getBtnCuestionar() {
        return btnCuestionar;
    }

    public JButton getBtnActualizar() {
        return btnActualizar;
    }

    public JButton getBtnCerrar() {
        return btnCerrar;
    }

    /** Colorea la columna "Origen / Estado" para identificar visualmente el reporte. */
    private static class EstadoRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            String texto = value == null ? "" : value.toString();
            if (!isSelected) {
                if (texto.startsWith("OFICIAL")) {
                    c.setForeground(new Color(0x1565C0));
                } else if (texto.startsWith("VERIFICADO")) {
                    c.setForeground(new Color(0x2E7D32));
                } else if (texto.startsWith("CUESTIONADO")) {
                    c.setForeground(new Color(0xC62828));
                } else {
                    c.setForeground(new Color(0x424242));
                }
            }
            setFont(getFont().deriveFont(Font.BOLD));
            return c;
        }
    }
}