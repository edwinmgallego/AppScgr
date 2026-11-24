package controllers;
import models.Incidente;
import models.MarcadorMapa;
import models.dao.IncidenteDAO;
import rutas.algoritmos.ReglasCierre;
import rutas.modelo.Arista;
import rutas.modelo.Grafo;
import rutas.modelo.Nodo;
import rutas.osm.RedVialOSM;
import views.MarcadorWaypoint;
import views.RutasView;
import org.jxmapviewer.viewer.GeoPosition;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;
/**
 * Controlador del módulo de rutas alternas (RF-04) abierto desde el Dashboard.
 *
 * Flujo: el usuario hace clic en el mapa para el inicio (A) y otro clic para el destino (B);
 * el cálculo arranca solo. {@link ServicioRutaMapa} obtiene la red vial real de OpenStreetMap,
 * aplica los cierres viales (incidentes VIA_CERRADA / DERRUMBE de MySQL) y ejecuta Dijkstra;
 * este controlador solo traduce el resultado a la vista. El trabajo pesado corre en segundo plano
 * (SwingWorker) para no congelar la ventana mientras se descargan las vías.
 *
 * @author santi
 */
public class RutasModuloController extends BaseController<RutasView> {
 private static final int MAX_VIAS_LISTADAS = 12;
 private static final Locale ES = Locale.forLanguageTag("es-CO");
 private final ServicioRutaMapa servicio;
 private final Supplier<List<Incidente>> fuenteIncidentes;
 private final ReglasCierre reglas = new ReglasCierre();
 private GeoPosition origen;
 private GeoPosition destino;
 private SwingWorker<?, ?> trabajo;
 public RutasModuloController(RutasView vista) {
 this(vista, () -> new IncidenteDAO().obtenerTodosLosIncidentes());
 }
 public RutasModuloController(RutasView vista, Supplier<List<Incidente>> fuenteIncidentes) {
 this(vista, new ServicioRutaMapa(fuenteIncidentes), fuenteIncidentes);
 }
 RutasModuloController(RutasView vista, ServicioRutaMapa servicio, Supplier<List<Incidente>> fuenteIncidentes) {
 super(vista);
 this.servicio = servicio;
 this.fuenteIncidentes = fuenteIncidentes;
 inicializarEventos();
 mostrarPaso();
 }
 @Override
 protected void inicializarEventos() {
 vista.getMapViewer().addMouseListener(new MouseAdapter() {
 @Override
 public void mouseClicked(MouseEvent e) {
 if (e.getButton() != MouseEvent.BUTTON1) return;
 GeoPosition p = vista.getMapViewer().convertPointToGeoPosition(e.getPoint());
 alSeleccionar(new GeoPosition(p.getLatitude(), MarcadorMapa.normalizarLongitud(p.getLongitude())));
 }
 });
 vista.getBtnCalcular().addActionListener(e -> calcularRuta());
 vista.getBtnLimpiar().addActionListener(e -> limpiar());
 vista.addWindowListener(new WindowAdapter() {
 @Override
 public void windowClosed(WindowEvent e) {
 if (trabajo != null) trabajo.cancel(true);
 }
 });
 }
 @Override
 public void iniciar() {
 super.iniciar();
 cargarCierresEnMapa();
 }
 // ---------------------------------------------------------------- selección
 /** Clic en el mapa: primero el inicio, luego el destino (y se calcula); un tercer clic empieza otra ruta. */
 void alSeleccionar(GeoPosition punto) {
 if (ocupado()) {
 vista.setEstado("Espere a que termine el cálculo actual…");
 return;
 }
 if (origen == null || destino != null) {
 if (destino != null) limpiarPuntos();
 origen = punto;
 vista.mostrarInicio(origen);
 vista.setResultado("");
 vista.setEstado("Inicio seleccionado.");
 mostrarPaso();
 } else {
 destino = punto;
 vista.mostrarDestino(destino);
 mostrarPaso();
 calcularRuta();
 }
 }
 private void limpiar() {
 if (ocupado()) return;
 limpiarPuntos();
 vista.setResultado("");
 vista.setEstado("Selección borrada. Haga clic en el mapa para elegir un nuevo inicio.");
 mostrarPaso();
 }
 private void limpiarPuntos() {
 origen = null;
 destino = null;
 vista.limpiarSeleccion();
 }
 private void mostrarPaso() {
 if (origen == null) {
 vista.setInstruccion("<b>Paso 1:</b> haga clic en el mapa para marcar el <font color='#16a34a'>INICIO (A)</font>.");
 } else if (destino == null) {
 vista.setInstruccion("<b>Paso 2:</b> haga clic en el mapa para marcar el <font color='#dc2626'>DESTINO (B)</font>.");
 } else {
 vista.setInstruccion("Ruta de A a B. Un nuevo clic en el mapa empieza otra ruta, "
 + "o use <i>Limpiar selección</i>.");
 }
 }
 // ---------------------------------------------------------------- cálculo
 private void calcularRuta() {
 if (ocupado()) return;
 if (origen == null || destino == null) {
 JOptionPane.showMessageDialog(vista, "Primero haga clic en el mapa para marcar el inicio y el destino.",
 "Rutas", JOptionPane.INFORMATION_MESSAGE);
 return;
 }
 if (!ServicioRutaMapa.zonaPermitida(origen.getLatitude(), origen.getLongitude(),
 destino.getLatitude(), destino.getLongitude())) {
 JOptionPane.showMessageDialog(vista, "Los puntos están demasiado lejos entre sí. "
 + "El módulo calcula rutas urbanas; elija puntos más cercanos.", "Rutas", JOptionPane.WARNING_MESSAGE);
 return;
 }
 final double latO = origen.getLatitude();
 final double lonO = origen.getLongitude();
 final double latD = destino.getLatitude();
 final double lonD = destino.getLongitude();
 vista.limpiarRuta();
 vista.setResultado("Calculando…");
 vista.setOcupado(true);
 SwingWorker<ServicioRutaMapa.Resultado, String> w = new SwingWorker<>() {
 @Override
 protected ServicioRutaMapa.Resultado doInBackground() throws Exception {
 return servicio.calcular(latO, lonO, latD, lonD, this::publish);
 }
 @Override
 protected void process(List<String> mensajes) {
 vista.setEstado(mensajes.get(mensajes.size() - 1));
 }
 @Override
 protected void done() {
 trabajo = null;
 vista.setOcupado(false);
 if (isCancelled()) return;
 try {
 mostrarResultado(get());
 } catch (InterruptedException e) {
 Thread.currentThread().interrupt();
 } catch (ExecutionException e) {
 Throwable causa = e.getCause() == null ? e : e.getCause();
 vista.limpiarRuta();
 vista.setResultado("No se pudo calcular la ruta.\n\n" + causa.getMessage());
 vista.setEstado("Error al obtener la red vial.");
 JOptionPane.showMessageDialog(vista, causa.getMessage(), "Rutas - red vial no disponible",
 JOptionPane.ERROR_MESSAGE);
 }
 }
 };
 trabajo = w;
 w.execute();
 }
 private void mostrarResultado(ServicioRutaMapa.Resultado r) {
 RedVialOSM red = r.red();
 if (red != null) {
 vista.mostrarTramosCerrados(tramosCerrados(red.getGrafo()));
 }
 vista.mostrarIncidentes(marcadores(r.incidentesBloqueantes()));
 if (r.problema() != null) {
 vista.limpiarRuta();
 vista.setResultado("NO SE CALCULÓ LA RUTA\n\n" + r.problema() + "\n\n" + textoCierres(r) + textoRed(red));
 vista.setEstado(r.problema());
 JOptionPane.showMessageDialog(vista, r.problema(), "Rutas", JOptionPane.WARNING_MESSAGE);
 return;
 }
 if (!r.rutaEncontrada()) {
 // No se dibuja nada: una línea "aproximada" sería engañosa en una emergencia.
 vista.limpiarRuta();
 String msg = "No existe una ruta posible entre el inicio y el destino por la red vial disponible"
 + (r.cierres() != null && !r.cierres().getTramosCerrados().isEmpty()
 ? ", teniendo en cuenta los cierres viales registrados." : ".");
 vista.setResultado("RUTA NO ENCONTRADA\n\n" + msg + "\n\n" + textoCierres(r) + textoRed(red));
 vista.setEstado("No existe ruta posible.");
 JOptionPane.showMessageDialog(vista, msg, "Rutas - sin ruta", JOptionPane.WARNING_MESSAGE);
 return;
 }
 List<Nodo> camino = r.ruta().getCamino();
 List<GeoPosition> puntos = new ArrayList<>(camino.size());
 for (Nodo n : camino) puntos.add(new GeoPosition(n.getLatitud(), n.getLongitud()));
 vista.mostrarRuta(puntos, puntos.get(0), puntos.get(puntos.size() - 1));
 Set<GeoPosition> encuadre = new HashSet<>(puntos);
 encuadre.add(origen);
 encuadre.add(destino);
 vista.encuadrar(encuadre);
 double metros = r.ruta().getCosto();
 double segundos = red.tiempoSegundos(camino);
 StringBuilder t = new StringBuilder();
 t.append("RUTA ENCONTRADA\n(Dijkstra sobre la red vial de OpenStreetMap)\n\n");
 t.append("Distancia aproximada: ").append(distancia(metros)).append('\n');
 t.append("Tiempo estimado: ").append(tiempo(segundos)).append(" en vehículo\n");
 t.append(" (velocidades de referencia de cada vía, sin tráfico)\n");
 t.append("Tramo hasta la vía: inicio ").append(distancia(r.accesoOrigenMetros()))
 .append(" · destino ").append(distancia(r.accesoDestinoMetros())).append("\n\n");
 List<String> vias = red.nombresVias(camino);
 if (!vias.isEmpty()) {
 t.append("Recorrido por:\n");
 for (int i = 0; i < vias.size() && i < MAX_VIAS_LISTADAS; i++) {
 t.append(" ").append(i + 1).append(". ").append(vias.get(i)).append('\n');
 }
 if (vias.size() > MAX_VIAS_LISTADAS) t.append(" … y ").append(vias.size() - MAX_VIAS_LISTADAS).append(" más\n");
 t.append('\n');
 }
 t.append(textoCierres(r)).append(textoRed(red));
 vista.setResultado(t.toString());
 vista.setEstado("Ruta calculada: " + distancia(metros) + ", " + tiempo(segundos)
 + " aprox. Clic en el mapa para una nueva ruta.");
 }
 // ---------------------------------------------------------------- cierres en el mapa
 /** Al abrir el módulo se muestran los cierres registrados, antes de elegir puntos. */
 private void cargarCierresEnMapa() {
 new SwingWorker<List<Incidente>, Void>() {
 @Override
 protected List<Incidente> doInBackground() {
 try {
 List<Incidente> lista = fuenteIncidentes.get();
 return lista == null ? List.of() : lista;
 } catch (RuntimeException e) {
 return null;
 }
 }
 @Override
 protected void done() {
 try {
 List<Incidente> lista = get();
 if (lista == null) {
 vista.setEstado("Sin conexión a la base de datos: las rutas se calcularán sin cierres viales.");
 return;
 }
 List<Incidente> bloqueantes = new ArrayList<>();
 for (Incidente i : lista) if (reglas.bloquea(i)) bloqueantes.add(i);
 vista.mostrarIncidentes(marcadores(bloqueantes));
 vista.setEstado(bloqueantes.size() + " cierre(s) vial(es) registrado(s). "
 + "Haga clic en el mapa para marcar el inicio.");
 } catch (InterruptedException e) {
 Thread.currentThread().interrupt();
 } catch (ExecutionException e) {
 vista.setEstado("No se pudieron leer los cierres viales.");
 }
 }
 }.execute();
 }
 private static Set<MarcadorWaypoint> marcadores(List<Incidente> incidentes) {
 Set<MarcadorWaypoint> set = new HashSet<>();
 if (incidentes == null) return set;
 for (Incidente i : incidentes) {
 try {
 set.add(new MarcadorWaypoint(MarcadorMapa.desdeIncidente(i)));
 } catch (IllegalArgumentException ex) {
 // coordenadas inválidas: no se dibuja ese incidente
 }
 }
 return set;
 }
 private static List<GeoPosition[]> tramosCerrados(Grafo g) {
 List<GeoPosition[]> tramos = new ArrayList<>();
 Set<String> vistos = new HashSet<>();
 for (Arista a : g.getTodasLasAristas()) {
 if (!a.isBloqueada()) continue;
 String clave = Math.min(a.getOrigen(), a.getDestino()) + "-" + Math.max(a.getOrigen(), a.getDestino());
 if (!vistos.add(clave)) continue;
 Nodo o = g.getNodo(a.getOrigen());
 Nodo d = g.getNodo(a.getDestino());
 tramos.add(new GeoPosition[]{new GeoPosition(o.getLatitud(), o.getLongitud()),
 new GeoPosition(d.getLatitud(), d.getLongitud())});
 }
 return tramos;
 }
 // ---------------------------------------------------------------- textos
 private static String textoCierres(ServicioRutaMapa.Resultado r) {
 StringBuilder t = new StringBuilder();
 if (!r.incidentesDisponibles()) {
 t.append("? Sin conexión a la base de datos: la ruta NO tiene en cuenta los cierres viales.\n\n");
 return t.toString();
 }
 int bloqueantes = r.incidentesBloqueantes().size();
 int cerrados = r.cierres() == null ? 0 : r.cierres().getTramosCerrados().size();
 int lejanos = r.cierres() == null ? 0 : r.cierres().getSinTramoCercano().size();
 t.append("Cierres viales: ").append(bloqueantes).append(" registrado(s) (vía cerrada / derrumbe), ")
 .append(cerrados).append(" tramo(s) bloqueado(s) en la zona, excluidos del cálculo.\n");
 if (lejanos > 0) {
 t.append(" ").append(lejanos).append(" cierre(s) fuera de la zona o lejos de una vía.\n");
 }
 return t.append('\n').toString();
 }
 private static String textoRed(RedVialOSM red) {
 if (red == null) return "";
 return String.format(ES, "Red vial cargada: %,d vías, %,d intersecciones, %,d nodos.%n",
 red.getNumVias(), red.getNumIntersecciones(), red.getGrafo().numNodos());
 }
 static String distancia(double metros) {
 if (metros < 1000) return Math.round(metros) + " m";
 return String.format(ES, "%.2f km", metros / 1000.0);
 }
 static String tiempo(double segundos) {
 long min = Math.max(1, Math.round(segundos / 60.0));
 if (min < 60) return min + " min";
 return (min / 60) + " h " + (min % 60) + " min";
 }
 private boolean ocupado() { return trabajo != null; }
}