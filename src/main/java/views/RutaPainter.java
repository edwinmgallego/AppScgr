package views;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.GeoPosition;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/**
 * Dibuja sobre el mapa de OpenStreetMap la ruta calculada siguiendo punto a punto la geometría
 * de las vías, los marcadores de inicio y destino y los tramos cerrados.
 *
 * @author santi
 */
public class RutaPainter implements Painter<JXMapViewer> {
 /** Azul eléctrico de la ruta, con un borde oscuro para que resalte sobre cualquier fondo. */
 public static final Color COLOR_RUTA = new Color(0, 98, 255);
 public static final Color COLOR_BORDE_RUTA = new Color(10, 20, 80);
 public static final Color COLOR_INICIO = new Color(22, 163, 74);
 public static final Color COLOR_DESTINO = new Color(220, 38, 38);
 public static final Color COLOR_CIERRE = new Color(200, 0, 0);
 private static final Font FUENTE_LETRA = new Font(Font.SANS_SERIF, Font.BOLD, 13);
 private static final Font FUENTE_ETIQUETA = new Font(Font.SANS_SERIF, Font.BOLD, 11);
 private static final Stroke TRAZO_ACCESO = new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
 10f, new float[]{6f, 6f}, 0f);
 private List<GeoPosition> ruta = Collections.emptyList();
 private List<GeoPosition[]> tramosCerrados = Collections.emptyList();
 private GeoPosition inicio;
 private GeoPosition destino;
 private GeoPosition inicioEnVia;
 private GeoPosition destinoEnVia;
 public void setRuta(List<GeoPosition> puntos) {
 this.ruta = puntos == null ? Collections.emptyList() : new ArrayList<>(puntos);
 }
 public List<GeoPosition> getRuta() { return Collections.unmodifiableList(ruta); }
 public void setTramosCerrados(List<GeoPosition[]> tramos) {
 this.tramosCerrados = tramos == null ? Collections.emptyList() : new ArrayList<>(tramos);
 }
 public void setInicio(GeoPosition inicio) { this.inicio = inicio; }
 public void setDestino(GeoPosition destino) { this.destino = destino; }
 public GeoPosition getInicio() { return inicio; }
 public GeoPosition getDestino() { return destino; }
 /** Puntos de la vía a los que se "engancharon" el inicio y el destino (para la línea punteada de acceso). */
 public void setAccesos(GeoPosition inicioEnVia, GeoPosition destinoEnVia) {
 this.inicioEnVia = inicioEnVia;
 this.destinoEnVia = destinoEnVia;
 }
 public void limpiarRuta() {
 ruta = Collections.emptyList();
 inicioEnVia = null;
 destinoEnVia = null;
 }
 public void limpiarTodo() {
 limpiarRuta();
 tramosCerrados = Collections.emptyList();
 inicio = null;
 destino = null;
 }
 @Override
 public void paint(Graphics2D g0, JXMapViewer mapa, int ancho, int alto) {
 Graphics2D g = (Graphics2D) g0.create();
 try {
 Rectangle vista = mapa.getViewportBounds();
 g.translate(-vista.x, -vista.y);
 g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
 g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
 dibujarCierres(g, mapa);
 dibujarRuta(g, mapa);
 dibujarAcceso(g, mapa, inicio, inicioEnVia);
 dibujarAcceso(g, mapa, destino, destinoEnVia);
 if (inicio != null) dibujarInicio(g, pixel(mapa, inicio));
 if (destino != null) dibujarDestino(g, pixel(mapa, destino));
 } finally {
 g.dispose();
 }
 }
 private void dibujarRuta(Graphics2D g, JXMapViewer mapa) {
 if (ruta.size() < 2) return;
 Path2D.Double linea = new Path2D.Double();
 Point2D p0 = pixel(mapa, ruta.get(0));
 linea.moveTo(p0.getX(), p0.getY());
 for (int i = 1; i < ruta.size(); i++) {
 Point2D p = pixel(mapa, ruta.get(i));
 linea.lineTo(p.getX(), p.getY());
 }
 g.setColor(COLOR_BORDE_RUTA);
 g.setStroke(new BasicStroke(11f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
 g.draw(linea);
 g.setColor(COLOR_RUTA);
 g.setStroke(new BasicStroke(7f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
 g.draw(linea);
 g.setColor(new Color(150, 200, 255, 170));
 g.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
 g.draw(linea);
 }
 private void dibujarCierres(Graphics2D g, JXMapViewer mapa) {
 for (GeoPosition[] tramo : tramosCerrados) {
 Point2D a = pixel(mapa, tramo[0]);
 Point2D b = pixel(mapa, tramo[1]);
 g.setColor(COLOR_CIERRE);
 g.setStroke(new BasicStroke(9f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
 g.drawLine((int) a.getX(), (int) a.getY(), (int) b.getX(), (int) b.getY());
 int mx = (int) ((a.getX() + b.getX()) / 2);
 int my = (int) ((a.getY() + b.getY()) / 2);
 g.fillOval(mx - 9, my - 9, 18, 18);
 g.setColor(Color.WHITE);
 g.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
 g.drawLine(mx - 4, my - 4, mx + 4, my + 4);
 g.drawLine(mx - 4, my + 4, mx + 4, my - 4);
 }
 }
 private void dibujarAcceso(Graphics2D g, JXMapViewer mapa, GeoPosition punto, GeoPosition enVia) {
 if (punto == null || enVia == null || ruta.isEmpty()) return;
 Point2D a = pixel(mapa, punto);
 Point2D b = pixel(mapa, enVia);
 g.setColor(new Color(60, 60, 60, 200));
 g.setStroke(TRAZO_ACCESO);
 g.drawLine((int) a.getX(), (int) a.getY(), (int) b.getX(), (int) b.getY());
 }
 /** Inicio: círculo verde con la letra A. */
 private void dibujarInicio(Graphics2D g, Point2D p) {
 int x = (int) p.getX();
 int y = (int) p.getY();
 int r = 12;
 g.setColor(new Color(0, 0, 0, 70));
 g.fillOval(x - r + 2, y - r + 3, r * 2, r * 2);
 g.setColor(COLOR_INICIO);
 g.fillOval(x - r, y - r, r * 2, r * 2);
 g.setColor(Color.WHITE);
 g.setStroke(new BasicStroke(3f));
 g.drawOval(x - r, y - r, r * 2, r * 2);
 letraCentrada(g, "A", x, y);
 etiqueta(g, "Inicio", x, y + r + 14, COLOR_INICIO);
 }
 /** Destino: pin (gota) rojo con la letra B; la punta marca el punto exacto. */
 private void dibujarDestino(Graphics2D g, Point2D p) {
 int x = (int) p.getX();
 int y = (int) p.getY();
 int r = 13;
 int cy = y - 30;
 GeneralPath pin = new GeneralPath();
 pin.moveTo(x, y);
 pin.curveTo(x - 6, y - 12, x - r, cy + 8, x - r, cy);
 pin.curveTo(x - r, cy - 18, x + r, cy - 18, x + r, cy);
 pin.curveTo(x + r, cy + 8, x + 6, y - 12, x, y);
 pin.closePath();
 g.setColor(new Color(0, 0, 0, 70));
 g.translate(2, 2);
 g.fill(pin);
 g.translate(-2, -2);
 g.setColor(COLOR_DESTINO);
 g.fill(pin);
 g.setColor(Color.WHITE);
 g.setStroke(new BasicStroke(2.5f));
 g.draw(pin);
 letraCentrada(g, "B", x, cy - 2);
 etiqueta(g, "Destino", x, y + 15, COLOR_DESTINO);
 }
 private static void letraCentrada(Graphics2D g, String letra, int x, int y) {
 g.setFont(FUENTE_LETRA);
 g.setColor(Color.WHITE);
 FontMetrics fm = g.getFontMetrics();
 g.drawString(letra, x - fm.stringWidth(letra) / 2, y + (fm.getAscent() - fm.getDescent()) / 2);
 }
 private static void etiqueta(Graphics2D g, String texto, int x, int y, Color color) {
 g.setFont(FUENTE_ETIQUETA);
 FontMetrics fm = g.getFontMetrics();
 int w = fm.stringWidth(texto) + 10;
 int h = fm.getHeight() + 2;
 g.setColor(new Color(255, 255, 255, 230));
 g.fillRoundRect(x - w / 2, y - h + 3, w, h, 8, 8);
 g.setColor(color);
 g.setStroke(new BasicStroke(1.2f));
 g.drawRoundRect(x - w / 2, y - h + 3, w, h, 8, 8);
 g.drawString(texto, x - fm.stringWidth(texto) / 2, y - 1);
 }
 private static Point2D pixel(JXMapViewer mapa, GeoPosition pos) {
 return mapa.getTileFactory().geoToPixel(pos, mapa.getZoom());
 }
}