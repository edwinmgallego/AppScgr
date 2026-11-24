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
 * Vista del módulo Cobertura móvil (RF-08).
 *
 * @author santi
 */
public class CoberturaMovilView extends JFrame {
 private final JTextField txtZona;
 private final JTextField txtOperador;
 private final JComboBox<String> cbxNivelFalla;
 private final JTextField txtLatitud;
 private final JTextField txtLongitud;
 private final JTextArea txtObservaciones;
 private final JButton btnGuardar;
 private final JButton btnActualizar;
 private final JButton btnEliminar;
 private final JButton btnLimpiar;
 private final JTable tabla;
 private final DefaultTableModel modeloTabla;
 private final JLabel lblEstado;
 public CoberturaMovilView() {
 setTitle("Cobertura móvil - Grupo 9");
 setSize(920, 580);
 setLocationRelativeTo(null);
 setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
 setLayout(new BorderLayout(10, 10));
 JLabel titulo = new JLabel("Cobertura móvil", SwingConstants.CENTER);
 titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 18f));
 titulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
 add(titulo, BorderLayout.NORTH);
 JPanel formulario = new JPanel(new GridLayout(0, 2, 8, 8));
 formulario.setBorder(BorderFactory.createTitledBorder("Datos del reporte"));
 txtZona = new JTextField();
 txtOperador = new JTextField();
 cbxNivelFalla = new JComboBox<>(new String[]{"SIN_SENAL", "SEÑAL_DEBIL", "OK"});
 txtLatitud = new JTextField();
 txtLongitud = new JTextField();
 txtObservaciones = new JTextArea(3, 20);
 txtObservaciones.setLineWrap(true);
 formulario.add(new JLabel("Zona / barrio:"));
 formulario.add(txtZona);
 formulario.add(new JLabel("Operador:"));
 formulario.add(txtOperador);
 formulario.add(new JLabel("Nivel de falla:"));
 formulario.add(cbxNivelFalla);
 formulario.add(new JLabel("Latitud:"));
 formulario.add(txtLatitud);
 formulario.add(new JLabel("Longitud:"));
 formulario.add(txtLongitud);
 formulario.add(new JLabel("Observaciones:"));
 formulario.add(new JScrollPane(txtObservaciones));
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
 String[] columnas = {"ID", "Zona", "Operador", "Nivel", "Latitud", "Longitud", "Fecha"};
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
 public JTextField getTxtOperador() { return txtOperador; }
 public JComboBox<String> getCbxNivelFalla() { return cbxNivelFalla; }
 public JTextField getTxtLatitud() { return txtLatitud; }
 public JTextField getTxtLongitud() { return txtLongitud; }
 public JTextArea getTxtObservaciones() { return txtObservaciones; }
 public JButton getBtnGuardar() { return btnGuardar; }
 public JButton getBtnActualizar() { return btnActualizar; }
 public JButton getBtnEliminar() { return btnEliminar; }
 public JButton getBtnLimpiar() { return btnLimpiar; }
 public JTable getTabla() { return tabla; }
 public DefaultTableModel getModeloTabla() { return modeloTabla; }
 public void setEstado(String texto) { lblEstado.setText(texto); }
}