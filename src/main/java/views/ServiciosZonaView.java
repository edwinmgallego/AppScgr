package views;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
/**
 * Vista del módulo Servicios por zona (RF-06).
 *
 * @author Grupo 9 - Validación Comunitaria y Dashboard
 */
public class ServiciosZonaView extends JFrame {
 private final JTextField txtZona;
 private final JComboBox<String> cbxServicio;
 private final JComboBox<String> cbxEstado;
 private final JTextArea txtObservaciones;
 private final JTextField txtVerificaciones;
 private final JButton btnGuardar;
 private final JButton btnActualizar;
 private final JButton btnEliminar;
 private final JButton btnLimpiar;
 private final JTable tabla;
 private final DefaultTableModel modeloTabla;
 private final JLabel lblEstado;
 public ServiciosZonaView() {
 setTitle("Servicios por zona - Grupo 9");
 setSize(900, 560);
 setLocationRelativeTo(null);
 setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
 setLayout(new BorderLayout(10, 10));
 JLabel titulo = new JLabel("Servicios por zona", SwingConstants.CENTER);
 titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 18f));
 titulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
 add(titulo, BorderLayout.NORTH);
 JPanel formulario = new JPanel(new GridLayout(0, 2, 8, 8));
 formulario.setBorder(BorderFactory.createTitledBorder("Datos del reporte"));
 txtZona = new JTextField();
 cbxServicio = new JComboBox<>(new String[]{"AGUA", "ENERGIA", "TELECOMUNICACIONES"});
 cbxEstado = new JComboBox<>(new String[]{"OPERANDO", "CON_FALLAS", "CAIDO"});
 txtObservaciones = new JTextArea(3, 20);
 txtObservaciones.setLineWrap(true);
 txtVerificaciones = new JTextField("0");
 formulario.add(new JLabel("Zona / barrio:"));
 formulario.add(txtZona);
 formulario.add(new JLabel("Servicio:"));
 formulario.add(cbxServicio);
 formulario.add(new JLabel("Estado:"));
 formulario.add(cbxEstado);
 formulario.add(new JLabel("Observaciones:"));
 formulario.add(new JScrollPane(txtObservaciones));
 formulario.add(new JLabel("Verificaciones:"));
 formulario.add(txtVerificaciones);
 JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 8));
 btnGuardar = new JButton("Guardar");
 btnActualizar = new JButton("Actualizar");
 btnEliminar = new JButton("Eliminar");
 btnLimpiar = new JButton("Limpiar");
 botones.add(btnGuardar);
 botones.add(btnActualizar);
 botones.add(btnEliminar);
 botones.add(btnLimpiar);
 JPanel panelIzquierdo = new JPanel(new BorderLayout());
 panelIzquierdo.add(formulario, BorderLayout.CENTER);
 panelIzquierdo.add(botones, BorderLayout.SOUTH);
 panelIzquierdo.setPreferredSize(new Dimension(360, 0));
 add(panelIzquierdo, BorderLayout.WEST);
 String[] columnas = {"ID", "Zona", "Servicio", "Estado", "Verificaciones", "Fecha"};
 modeloTabla = new DefaultTableModel(columnas, 0) {
 @Override
 public boolean isCellEditable(int fila, int columna) {
 return false;
 }
 };
 tabla = new JTable(modeloTabla);
 tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
 add(new JScrollPane(tabla), BorderLayout.CENTER);
 lblEstado = new JLabel(" ");
 lblEstado.setBorder(BorderFactory.createEmptyBorder(4, 10, 8, 10));
 add(lblEstado, BorderLayout.SOUTH);
 }
 public JTextField getTxtZona() { return txtZona; }
 public JComboBox<String> getCbxServicio() { return cbxServicio; }
 public JComboBox<String> getCbxEstado() { return cbxEstado; }
 public JTextArea getTxtObservaciones() { return txtObservaciones; }
 public JTextField getTxtVerificaciones() { return txtVerificaciones; }
 public JButton getBtnGuardar() { return btnGuardar; }
 public JButton getBtnActualizar() { return btnActualizar; }
 public JButton getBtnEliminar() { return btnEliminar; }
 public JButton getBtnLimpiar() { return btnLimpiar; }
 public JTable getTabla() { return tabla; }
 public DefaultTableModel getModeloTabla() { return modeloTabla; }
 public void setEstado(String texto) { lblEstado.setText(texto); }
}