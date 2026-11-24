package views;
import config.ConfiguracionMapa;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.ZoomMouseWheelListenerCursor;
import org.jxmapviewer.painter.CompoundPainter;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.TileFactoryInfo;
import org.jxmapviewer.viewer.WaypointPainter;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
/**
 * Vista del módulo de rutas alternas (RF-04): mapa interactivo de OpenStreetMap donde el usuario
 * elige con clics el punto de inicio y el destino, y donde se dibuja la ruta calculada.
 *
 * Solo muestra datos y expone componentes; la lógica está en {@code RutasModuloController}.
 *
 * @author Equipo 4 - Rutas alternas / Grupo 9 - Dashboard
 */
public class RutasView extends JFrame {
 private final JXMapViewer mapViewer;
 private final RutaPainter rutaPainter = new RutaPainter();
 private final WaypointPainter<MarcadorWaypoint> pintorIncidentes = new WaypointPainter<>();
 private final JLabel lblInstruccion;
 private final JTextField txtOrigen;
 private final JTextField txtDestino;
 private final JButton btnCalcular;
 private final JButton btnLimpiar;
 private final JProgressBar barraProgreso;
 private final JTextArea txtResultado;
 private final JLabel lblEstado;
 public RutasView() {
 setTitle("Rutas alternas - Selección de ruta en el mapa");
 System.setProperty("http.agent", ConfiguracionMapa.USER_AGENT);
 setSize(1150, 720);
 setMinimumSize(new Dimension(900, 560));
 setLocationRelativeTo(null);
 setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
 setLayout(new BorderLayout());
 // --- Mapa (misma configuración que MapaView) ---
 mapViewer = new JXMapViewer();
 TileFactoryInfo info = new OSMTileFactoryInfo() {
 @Override
 public String getTileUrl(int x, int y, int zoom) {
 return ConfiguracionMapa.aHttps(super.getTileUrl(x, y, zoom));
 }
 };
 mapViewer.setTileFactory(new DefaultTileFactory(info));
 mapViewer.setZoom(ConfiguracionMapa.ZOOM_INICIAL);
 mapViewer.setAddressLocation(new GeoPosition(ConfiguracionMapa.CALI_LATITUD, ConfiguracionMapa.CALI_LONGITUD));
 PanMouseInputListener arrastre = new PanMouseInputListener(mapViewer);
 mapViewer.addMouseListener(arrastre);
 mapViewer.addMouseMotionListener(arrastre);
 mapViewer.addMouseWheelListener(new ZoomMouseWheelListenerCursor(mapViewer));
 mapViewer.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
 pintorIncidentes.setRenderer(new MarcadorRenderer());
 List<Painter<JXMapViewer>> capas = new ArrayList<>();
 capas.add(rutaPainter);
 capas.add(pintorIncidentes);
 mapViewer.setOverlayPainter(new CompoundPainter<>(capas));
 // --- Panel lateral ---
 JPanel lateral = new JPanel();
 lateral.setLayout(new BoxLayout(lateral, BoxLayout.Y_AXIS));
 lateral.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
 lateral.setPreferredSize(new Dimension(330, 0));
 lblInstruccion = new JLabel();
 lblInstruccion.setFont(lblInstruccion.getFont().deriveFont(Font.BOLD, 13f));
 lblInstruccion.setBorder(BorderFactory.createCompoundBorder(
 BorderFactory.createLineBorder(RutaPainter.COLOR_RUTA, 2),
 BorderFactory.createEmptyBorder(8, 8, 8, 8)));
 lblInstruccion.setOpaque(true);
 lblInstruccion.setBackground(new Color(235, 242, 255));
 agregar(lateral, lblInstruccion);
 lateral.add(Box.createVerticalStrut(10));
 JPanel puntos = new JPanel(new GridLayout(4, 1, 2, 2));
 puntos.setBorder(BorderFactory.createTitledBorder("Puntos seleccionados"));
 txtOrigen = campoSoloLectura();
 txtDestino = campoSoloLectura();
 puntos.add(etiquetaColor("A Inicio", RutaPainter.COLOR_INICIO));
 puntos.add(txtOrigen);
 puntos.add(etiquetaColor("B Destino", RutaPainter.COLOR_DESTINO));
 puntos.add(txtDestino);
 agregar(lateral, puntos);
 lateral.add(Box.createVerticalStrut(8));
 JPanel botones = new JPanel(new GridLayout(1, 2, 6, 6));
 btnCalcular = new JButton("Calcular nueva ruta");
 btnCalcular.setToolTipText("Vuelve a calcular la ruta entre los puntos marcados, con los cierres viales actualizados");
 btnLimpiar = new JButton("Limpiar selección");
 btnLimpiar.setToolTipText("Borra la ruta y los puntos para elegir otros");
 botones.add(btnCalcular);
 botones.add(btnLimpiar);
 agregar(lateral, botones);
 lateral.add(Box.createVerticalStrut(6));
 barraProgreso = new JProgressBar();
 barraProgreso.setIndeterminate(true);
 barraProgreso.setVisible(false);
 agregar(lateral, barraProgreso);
 lateral.add(Box.createVerticalStrut(6));
 txtResultado = new JTextArea();
 txtResultado.setEditable(false);
 txtResultado.setLineWrap(true);
 txtResultado.setWrapStyleWord(true);
 txtResultado.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
 JScrollPane scroll = new JScrollPane(txtResultado);
 scroll.setBorder(BorderFactory.createTitledBorder("Resultado"));
 scroll.setPreferredSize(new Dimension(300, 260));
 agregar(lateral, scroll);
 lateral.add(Box.createVerticalStrut(6));
 agregar(lateral, crearLeyenda());
 lblEstado = new JLabel(" ");
 lblEstado.setBorder(BorderFactory.createEmptyBorder(4, 10, 6, 10));
 add(mapViewer, BorderLayout.CENTER);
 add(lateral, BorderLayout.EAST);
 add(lblEstado, BorderLayout.SOUTH);
 }
 // ---------------- Componentes para el controlador ----------------
 public JXMapViewer getMapViewer() { return mapViewer; }
 public JButton getBtnCalcular() { return btnCalcular; }
 public JButton getBtnLimpiar() { return btnLimpiar; }
 public JTextArea getTxtResultado() { return txtResultado; }
 public void setEstado(String texto) { lblEstado.setText(texto == null || texto.isEmpty() ? " " : texto); }
 public void setResultado(String texto) { txtResultado.setText(texto); txtResultado.setCaretPosition(0); }
 public void setInstruccion(String texto) {
 lblInstruccion.setText("<html><body style='width:210px'>" + texto + "</body></html>");
 lblInstruccion.setMaximumSize(new Dimension(Integer.MAX_VALUE, lblInstruccion.getPreferredSize().height));
 lblInstruccion.revalidate();
 }
 /** Muestra u oculta el indicador de trabajo y bloquea los botones mientras se calcula. */
 public void setOcupado(boolean ocupado) {
 barraProgreso.setVisible(ocupado);
 btnCalcular.setEnabled(!ocupado);
 btnLimpiar.setEnabled(!ocupado);
 mapViewer.setCursor(Cursor.getPredefinedCursor(ocupado ? Cursor.WAIT_CURSOR : Cursor.CROSSHAIR_CURSOR));
 }
 // ---------------- Dibujo sobre el mapa ----------------
 public void mostrarInicio(GeoPosition p) {
 rutaPainter.setInicio(p);
 txtOrigen.setText(texto(p));
 mapViewer.repaint();
 }
 public void mostrarDestino(GeoPosition p) {
 rutaPainter.setDestino(p);
 txtDestino.setText(texto(p));
 mapViewer.repaint();
 }
 /** Dibuja la ruta (lista ordenada de puntos sobre las vías) y las líneas punteadas de acceso. */
 public void mostrarRuta(List<GeoPosition> puntos, GeoPosition inicioEnVia, GeoPosition destinoEnVia) {
 rutaPainter.setRuta(puntos);
 rutaPainter.setAccesos(inicioEnVia, destinoEnVia);
 mapViewer.repaint();
 }
 public void mostrarTramosCerrados(List<GeoPosition[]> tramos) {
 rutaPainter.setTramosCerrados(tramos);
 mapViewer.repaint();
 }
 public void mostrarIncidentes(Set<MarcadorWaypoint> marcadores) {
 pintorIncidentes.setWaypoints(marcadores);
 mapViewer.repaint();
 }
 public void limpiarRuta() {
 rutaPainter.limpiarRuta();
 mapViewer.repaint();
 }
 /** Borra los puntos y la ruta; los tramos cerrados siguen visibles. */
 public void limpiarSeleccion() {
 rutaPainter.limpiarRuta();
 rutaPainter.setInicio(null);
 rutaPainter.setDestino(null);
 txtOrigen.setText("");
 txtDestino.setText("");
 mapViewer.repaint();
 }
 /** Ajusta el zoom y el centro para que se vean todos los puntos. */
 public void encuadrar(Set<GeoPosition> puntos) {
 if (puntos == null || puntos.isEmpty()) return;
 if (puntos.size() == 1) {
 mapViewer.setAddressLocation(puntos.iterator().next());
 } else {
 mapViewer.zoomToBestFit(puntos, 0.75);
 }
 }
 // ---------------- Auxiliares ----------------
 private static String texto(GeoPosition p) {
 return p == null ? "" : String.format(Locale.ROOT, "%.6f, %.6f", p.getLatitude(), p.getLongitude());
 }
 private static JTextField campoSoloLectura() {
 JTextField t = new JTextField();
 t.setEditable(false);
 return t;
 }
 private static JLabel etiquetaColor(String texto, Color color) {
 JLabel l = new JLabel(texto);
 l.setForeground(color);
 l.setFont(l.getFont().deriveFont(Font.BOLD));
 return l;
 }
 private static void agregar(JPanel panel, JComponent c) {
 c.setAlignmentX(Component.LEFT_ALIGNMENT);
 c.setMaximumSize(new Dimension(Integer.MAX_VALUE, c.getPreferredSize().height
 + (c instanceof JScrollPane ? 2000 : 0)));
 panel.add(c);
 }
 private static JPanel crearLeyenda() {
 JPanel leyenda = new JPanel(new GridLayout(0, 1, 2, 2));
 leyenda.setBorder(BorderFactory.createTitledBorder("Leyenda"));
 leyenda.add(itemLeyenda(new MuestraLinea(RutaPainter.COLOR_RUTA, false), "Ruta por las vías"));
 leyenda.add(itemLeyenda(new MuestraLinea(RutaPainter.COLOR_CIERRE, true), "Tramo cerrado (se evita)"));
 leyenda.add(itemLeyenda(new MuestraPunto(RutaPainter.COLOR_INICIO), "Inicio (A)"));
 leyenda.add(itemLeyenda(new MuestraPunto(RutaPainter.COLOR_DESTINO), "Destino (B)"));
 leyenda.add(new JLabel("<html><small>Clic: marcar punto · Arrastrar: mover · Rueda: zoom</small></html>"));
 return leyenda;
 }
 private static JPanel itemLeyenda(JComponent muestra, String texto) {
 JPanel p = new JPanel(new BorderLayout(8, 0));
 muestra.setPreferredSize(new Dimension(34, 16));
 p.add(muestra, BorderLayout.WEST);
 p.add(new JLabel(texto), BorderLayout.CENTER);
 return p;
 }
 private static final class MuestraLinea extends JComponent {
 private final Color color;
 private final boolean cierre;
 MuestraLinea(Color color, boolean cierre) { this.color = color; this.cierre = cierre; }
 @Override protected void paintComponent(Graphics g0) {
 Graphics2D g = (Graphics2D) g0;
 g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
 int y = getHeight() / 2;
 if (!cierre) {
 g.setColor(RutaPainter.COLOR_BORDE_RUTA);
 g.setStroke(new BasicStroke(8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
 g.drawLine(5, y, getWidth() - 5, y);
 }
 g.setColor(color);
 g.setStroke(new BasicStroke(cierre ? 7f : 5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
 g.drawLine(5, y, getWidth() - 5, y);
 }
 }
 private static final class MuestraPunto extends JComponent {
 private final Color color;
 MuestraPunto(Color color) { this.color = color; }
 @Override protected void paintComponent(Graphics g0) {
 Graphics2D g = (Graphics2D) g0;
 g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
 int c = getWidth() / 2;
 g.setColor(color);
 g.fillOval(c - 7, getHeight() / 2 - 7, 14, 14);
 }
 }
}