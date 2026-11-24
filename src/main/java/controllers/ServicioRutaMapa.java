package controllers;
import models.Incidente;
import rutas.algoritmos.Dijkstra;
import rutas.algoritmos.Localizador;
import rutas.algoritmos.ReglasCierre;
import rutas.algoritmos.ResumenCierres;
import rutas.algoritmos.RutaResultado;
import rutas.modelo.Nodo;
import rutas.osm.FuenteRedVialOSM;
import rutas.osm.LimitesGeo;
import rutas.osm.ProveedorRedVial;
import rutas.osm.RedVialOSM;
import rutas.persistencia.LectorCSV;
import rutas.util.Geo;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
/**
 * Caso de uso "ruta entre dos puntos del mapa" sobre la red vial real de OpenStreetMap.
 *
 * No duplica lógica de rutas: obtiene la red de {@code rutas.osm}, se la entrega al {@link RutasController}
 * del Equipo 4 ({@link RutasController#cargarGrafo}), aplica los cierres con {@link ReglasCierre}
 * (vía {@link RutasController#aplicarIncidentes}), asocia cada clic al nodo vial más cercano con
 * {@link Localizador} y calcula el camino con {@link Dijkstra}.
 *
 * No usa Swing: se puede probar sin interfaz y se ejecuta en segundo plano desde el controlador de la vista.
 *
 * @author santi
 */
public class ServicioRutaMapa {
 /** Margen mínimo alrededor de los dos puntos al descargar vías (~1,3 km). */
 public static final double MARGEN_GRADOS = 0.012;
 /** Fracción extra del tamaño del recorrido que se agrega como margen (espacio para desvíos). */
 public static final double FRACCION_MARGEN = 0.25;
 /** Los bordes de la zona se redondean a esta cuadrícula (~1,1 km) para reutilizar la caché. */
 public static final double CUADRICULA_GRADOS = 0.01;
 /** Zona máxima que se descarga (lado en grados, ~55 km). Evita consultas enormes a OpenStreetMap. */
 public static final double LADO_MAXIMO_GRADOS = 0.5;
 /** Si el clic queda más lejos que esto de cualquier vía, no se calcula (clic en un río, un cerro...). */
 public static final double ACCESO_MAXIMO_METROS = 800.0;
 /** Resultado completo para que la vista lo dibuje. */
 public record Resultado(RutaResultado ruta, RedVialOSM red, ResumenCierres cierres,
 List<Incidente> incidentesBloqueantes, Nodo nodoOrigen, Nodo nodoDestino,
 double accesoOrigenMetros, double accesoDestinoMetros,
 boolean incidentesDisponibles, String problema) {
 public boolean rutaEncontrada() {
 return problema == null && ruta != null && ruta.isRutaEncontrada();
 }
 }
 private final RutasController rutasController;
 private final ProveedorRedVial proveedor;
 private final Supplier<List<Incidente>> fuenteIncidentes;
 private final ReglasCierre reglas = new ReglasCierre();
 private RedVialOSM red;
 /** Configuración real: OpenStreetMap + incidentes de MySQL. */
 public ServicioRutaMapa(Supplier<List<Incidente>> fuenteIncidentes) {
 this(new RutasController(new LectorCSV(), new Dijkstra()), new FuenteRedVialOSM(), fuenteIncidentes);
 }
 public ServicioRutaMapa(RutasController rutasController, ProveedorRedVial proveedor,
 Supplier<List<Incidente>> fuenteIncidentes) {
 if (rutasController == null || proveedor == null || fuenteIncidentes == null) {
 throw new IllegalArgumentException("Controlador, proveedor e incidentes son obligatorios");
 }
 this.rutasController = rutasController;
 this.proveedor = proveedor;
 this.fuenteIncidentes = fuenteIncidentes;
 }
 /** Zona que se descargaría para estos dos puntos. */
 public static LimitesGeo zonaPara(double latO, double lonO, double latD, double lonD, double factorMargen) {
 return LimitesGeo.alrededorDe(latO, lonO, latD, lonD,
 MARGEN_GRADOS * factorMargen, FRACCION_MARGEN * factorMargen, CUADRICULA_GRADOS);
 }
 /** ¿Los puntos están lo bastante cerca como para descargar su zona? */
 public static boolean zonaPermitida(double latO, double lonO, double latD, double lonD) {
 return zonaPara(latO, lonO, latD, lonD, 1).ladoMayorGrados() <= LADO_MAXIMO_GRADOS;
 }
 public RedVialOSM getRed() { return red; }
 /**
 * Calcula la mejor ruta (Dijkstra) entre dos puntos del mapa, siguiendo las vías y evitando los cierres.
 * Si no hay ruta dentro de la zona descargada, reintenta una vez con una zona más grande
 * (el desvío alrededor de un cierre puede salirse del rectángulo inicial).
 *
 * @param progreso recibe mensajes de avance para la barra de estado (puede ser null)
 * @throws IOException si no se pudo obtener la red vial (sin internet y sin caché)
 */
 public Resultado calcular(double latO, double lonO, double latD, double lonD,
 Consumer<String> progreso) throws IOException {
 if (!Geo.coordenadasValidas(latO, lonO) || !Geo.coordenadasValidas(latD, lonD)) {
 throw new IllegalArgumentException("Coordenadas fuera de rango");
 }
 Consumer<String> avisar = progreso == null ? m -> { } : progreso;
 if (!zonaPermitida(latO, lonO, latD, lonD)) {
 return sinRuta(null, null, Collections.emptyList(), true,
 "Los puntos están demasiado lejos entre sí para el cálculo urbano (máx. ~"
 + Math.round(LADO_MAXIMO_GRADOS * 111) + " km de zona).");
 }
 Resultado ultimo = null;
 for (int intento = 1; intento <= 2; intento++) {
 LimitesGeo zona = zonaPara(latO, lonO, latD, lonD, intento == 1 ? 1 : 3);
 if (intento == 2) {
 if (zona.ladoMayorGrados() > LADO_MAXIMO_GRADOS) break;
 avisar.accept("No hubo ruta en la zona inicial; ampliando la zona de búsqueda…");
 }
 RedVialOSM r = obtenerRed(zona, latO, lonO, latD, lonD, intento == 1, avisar);
 ultimo = calcularSobre(r, latO, lonO, latD, lonD, avisar);
 if (ultimo.rutaEncontrada() || ultimo.problema() != null) return ultimo;
 }
 return ultimo;
 }
 private RedVialOSM obtenerRed(LimitesGeo zona, double latO, double lonO, double latD, double lonD,
 boolean reutilizar, Consumer<String> avisar) throws IOException {
 double holgura = MARGEN_GRADOS / 2;
 if (reutilizar && red != null && red.getLimites() != null
 && red.getLimites().contieneConHolgura(latO, lonO, holgura)
 && red.getLimites().contieneConHolgura(latD, lonD, holgura)) {
 avisar.accept("Usando la red vial ya cargada…");
 return red;
 }
 avisar.accept(proveedor.hayCacheVigente(zona)
 ? "Leyendo la red vial de OpenStreetMap guardada en caché…"
 : "Descargando la red vial de OpenStreetMap (puede tardar unos segundos)…");
 red = proveedor.cargar(zona);
 return red;
 }
 private Resultado calcularSobre(RedVialOSM r, double latO, double lonO, double latD, double lonD,
 Consumer<String> avisar) {
 rutasController.cargarGrafo(r.getGrafo());
 r.reabrirTramos(); // los cierres se recalculan siempre con la lista actual de incidentes
 avisar.accept("Aplicando cierres viales registrados…");
 boolean bdDisponible = true;
 List<Incidente> incidentes;
 try {
 incidentes = fuenteIncidentes.get();
 if (incidentes == null) incidentes = Collections.emptyList();
 } catch (RuntimeException e) { // sin MySQL: se calcula igual, sin cierres
 incidentes = Collections.emptyList();
 bdDisponible = false;
 }
 ResumenCierres cierres = rutasController.aplicarIncidentes(incidentes);
 List<Incidente> bloqueantes = new ArrayList<>();
 for (Incidente i : incidentes) {
 if (reglas.bloquea(i) && i != null && Geo.coordenadasValidas(i.getLatitud(), i.getLongitud())) {
 bloqueantes.add(i);
 }
 }
 avisar.accept("Buscando los nodos viales más cercanos…");
 Nodo nodoO = Localizador.nodoMasCercano(r.getGrafo(), latO, lonO, r::esNodoPrincipal);
 Nodo nodoD = Localizador.nodoMasCercano(r.getGrafo(), latD, lonD, r::esNodoPrincipal);
 if (nodoO == null || nodoD == null) {
 return sinRuta(r, cierres, bloqueantes, bdDisponible, "La zona no tiene vías conectadas.");
 }
 double accO = Geo.distanciaMetros(latO, lonO, nodoO.getLatitud(), nodoO.getLongitud());
 double accD = Geo.distanciaMetros(latD, lonD, nodoD.getLatitud(), nodoD.getLongitud());
 if (accO > ACCESO_MAXIMO_METROS || accD > ACCESO_MAXIMO_METROS) {
 String cual = accO > ACCESO_MAXIMO_METROS ? "inicio" : "destino";
 double d = Math.max(accO, accD);
 return new Resultado(null, r, cierres, bloqueantes, nodoO, nodoD, accO, accD, bdDisponible,
 "El punto de " + cual + " está a " + Math.round(d) + " m de la vía más cercana. "
 + "Seleccione un punto sobre una calle o cerca de ella.");
 }
 if (nodoO.getId() == nodoD.getId()) {
 return new Resultado(null, r, cierres, bloqueantes, nodoO, nodoD, accO, accD, bdDisponible,
 "El inicio y el destino corresponden al mismo punto de la red vial. Elija puntos más separados.");
 }
 avisar.accept("Calculando la mejor ruta con Dijkstra…");
 RutaResultado ruta = rutasController.calcularRuta(nodoO.getId(), nodoD.getId());
 return new Resultado(ruta, r, cierres, bloqueantes, nodoO, nodoD, accO, accD, bdDisponible, null);
 }
 private static Resultado sinRuta(RedVialOSM r, ResumenCierres cierres, List<Incidente> bloqueantes,
 boolean bd, String problema) {
 return new Resultado(null, r, cierres, bloqueantes, null, null, 0, 0, bd, problema);
 }
}