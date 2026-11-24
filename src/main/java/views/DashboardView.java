package views;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
/**
 * Dashboard principal (RF-14). Menú lateral para acceder a todos los módulos
 * desarrollados por los grupos anteriores y al módulo de validación comunitaria.
 *
 * @author Grupo 9 - Validación Comunitaria y Dashboard
 */
public class DashboardView extends JFrame {
 private final DefaultListModel<String> modeloMenu;
 private final JList<String> menuLista;
 private final JLabel lblBienvenida;
 private final JLabel lblRol;
 private final JLabel lblResumen;
 private final JPanel panelIndicadores;
 public DashboardView() {
 setTitle("Dashboard - Sistema Integrado de Asistencia Urbana ante Emergencias");
 setSize(980, 620);
 setMinimumSize(new Dimension(820, 520));
 setLocationRelativeTo(null);
 setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 setLayout(new BorderLayout());
 // ---------- Encabezado ----------
 JPanel encabezado = new JPanel(new BorderLayout());
 encabezado.setBackground(new Color(0x0D47A1));
 encabezado.setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18));
 JLabel titulo = new JLabel("Sistema Integrado de Asistencia Urbana ante Emergencias");
 titulo.setForeground(Color.WHITE);
 titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 18f));
 encabezado.add(titulo, BorderLayout.WEST);
 lblBienvenida = new JLabel("Bienvenido");
 lblBienvenida.setForeground(Color.WHITE);
 lblBienvenida.setHorizontalAlignment(SwingConstants.RIGHT);
 encabezado.add(lblBienvenida, BorderLayout.EAST);
 add(encabezado, BorderLayout.NORTH);
 // ---------- Menú lateral ----------
 modeloMenu = new DefaultListModel<>();
 String[] items = {
 "Resumen",
 "Incidentes (Mapa)",
 "Red familiar",
 "Estado de supervivencia",
 "Mi perfil",
 "Daños estructurales",
 "Rutas alternas",
 "Servicios por zona",
 "Cobertura móvil",
 "Validación comunitaria",
 "Cerrar sesión"
 };
 for (String item : items) {
 modeloMenu.addElement(item);
 }
 menuLista = new JList<>(modeloMenu);
 menuLista.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
 menuLista.setFixedCellWidth(220);
 menuLista.setFixedCellHeight(34);
 menuLista.setFont(menuLista.getFont().deriveFont(14f));
 menuLista.setBorder(BorderFactory.createEmptyBorder(8, 4, 8, 4));
 JPanel panelMenu = new JPanel(new BorderLayout());
 panelMenu.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(0xBDBDBD)));
 JLabel lblMenu = new JLabel(" Módulos del sistema");
 lblMenu.setFont(lblMenu.getFont().deriveFont(Font.BOLD, 13f));
 lblMenu.setBorder(BorderFactory.createEmptyBorder(10, 8, 8, 8));
 panelMenu.add(lblMenu, BorderLayout.NORTH);
 panelMenu.add(new JScrollPane(menuLista), BorderLayout.CENTER);
 panelMenu.setPreferredSize(new Dimension(230, 0));
 add(panelMenu, BorderLayout.WEST);
 // ---------- Contenido central ----------
 JPanel contenido = new JPanel(new BorderLayout());
 contenido.setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));
 JLabel tituloContenido = new JLabel("Resumen general");
 tituloContenido.setFont(tituloContenido.getFont().deriveFont(Font.BOLD, 20f));
 contenido.add(tituloContenido, BorderLayout.NORTH);
 panelIndicadores = new JPanel(new GridLayout(1, 3, 12, 12));
 contenido.add(panelIndicadores, BorderLayout.CENTER);
 lblResumen = new JLabel("<html><div style='text-align:center'>"
 + "Seleccione un módulo del menú lateral para comenzar.</div></html>",
 SwingConstants.CENTER);
 contenido.add(lblResumen, BorderLayout.SOUTH);
 add(contenido, BorderLayout.CENTER);
 lblRol = new JLabel(" ");
 lblRol.setBorder(BorderFactory.createEmptyBorder(4, 18, 4, 18));
 lblRol.setForeground(new Color(0x616161));
 lblRol.setHorizontalAlignment(SwingConstants.RIGHT);
 add(lblRol, BorderLayout.SOUTH);
 }
 public JList<String> getMenuLista() {
 return menuLista;
 }
 public JLabel getLblBienvenida() {
 return lblBienvenida;
 }
 public JLabel getLblResumen() {
 return lblResumen;
 }
 public void setRol(String texto) {
 lblRol.setText(texto);
 }
 public JPanel getPanelIndicadores() {
 return panelIndicadores;
 }
 /** Muestra tres tarjetas simples con indicadores del sistema. */
 public void mostrarIndicadores(int reportes, int verificados, int votos) {
 panelIndicadores.removeAll();
 panelIndicadores.add(tarjeta("Reportes registrados", String.valueOf(reportes), new Color(0x1565C0)));
 panelIndicadores.add(tarjeta("Reportes verificados", String.valueOf(verificados), new Color(0x2E7D32)));
 panelIndicadores.add(tarjeta("Votos comunitarios", String.valueOf(votos), new Color(0x6A1B9A)));
 panelIndicadores.revalidate();
 panelIndicadores.repaint();
 }
 private JPanel tarjeta(String titulo, String valor, Color color) {
 JPanel tarjeta = new JPanel(new BorderLayout());
 tarjeta.setBorder(BorderFactory.createCompoundBorder(
 BorderFactory.createLineBorder(color, 2),
 BorderFactory.createEmptyBorder(14, 14, 14, 14)));
 tarjeta.setBackground(Color.WHITE);
 JLabel lblTitulo = new JLabel(titulo);
 lblTitulo.setForeground(color);
 lblTitulo.setFont(lblTitulo.getFont().deriveFont(Font.BOLD, 13f));
 JLabel lblValor = new JLabel(valor, SwingConstants.CENTER);
 lblValor.setFont(lblValor.getFont().deriveFont(Font.BOLD, 30f));
 lblValor.setForeground(color);
 tarjeta.add(lblTitulo, BorderLayout.NORTH);
 tarjeta.add(lblValor, BorderLayout.CENTER);
 return tarjeta;
 }
}