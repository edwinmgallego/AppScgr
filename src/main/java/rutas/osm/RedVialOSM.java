package rutas.osm;

import rutas.modelo.Arista;
import rutas.modelo.Grafo;
import rutas.modelo.Nodo;
import rutas.util.Geo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Red vial real construida con datos de OpenStreetMap sobre el {@link Grafo} del proyecto.
 *
 * Cada punto (nodo OSM) de cada vía es un {@link Nodo} del grafo y cada par de puntos consecutivos
 * es una {@link Arista} con peso = distancia en metros. Por eso el camino que devuelve Dijkstra ya
 * sigue la geometría exacta de las calles (curvas y giros incluidos). Las intersecciones son los
 * nodos que comparten dos o más vías: ahí el grafo se ramifica.
 *
 * Además del grafo guarda, por cada arco dirigido, el nombre de la vía y la velocidad de referencia,
 * para estimar el tiempo del recorrido.
 *
 * @author santi
 */
public class RedVialOSM {

    /** Datos de un arco dirigido que el grafo no guarda (el grafo solo conoce el peso). */
    public record InfoTramo(String nombreVia, String tipoVia, double velocidadKmh) { }

    private final Grafo grafo;
    private final LimitesGeo limites;
    private final Map<Long, InfoTramo> infoTramos = new HashMap<>();
    private final Map<Integer, Long> idOsmPorNodo = new HashMap<>();
    private final Set<Integer> intersecciones = new HashSet<>();
    private Set<Integer> componentePrincipal;
    private int numVias;

    RedVialOSM(Grafo grafo, LimitesGeo limites) {
        if (grafo == null) throw new IllegalArgumentException("El grafo es obligatorio");
        this.grafo = grafo;
        this.limites = limites;
    }

    // ---- usado por LectorOSM al construir la red ----

    void registrarTramo(int origen, int destino, InfoTramo info) {
        infoTramos.put(clave(origen, destino), info);
    }

    void registrarIdOsm(int idNodo, long idOsm) { idOsmPorNodo.put(idNodo, idOsm); }

    void marcarInterseccion(int idNodo) { intersecciones.add(idNodo); }

    void setNumVias(int numVias) { this.numVias = numVias; }

    // ---- consultas ----

    public Grafo getGrafo() { return grafo; }

    /** Zona descargada (puede ser null si la red se construyó sin límites). */
    public LimitesGeo getLimites() { return limites; }

    public int getNumVias() { return numVias; }

    public int getNumIntersecciones() { return intersecciones.size(); }

    public boolean esInterseccion(int idNodo) { return intersecciones.contains(idNodo); }

    public Long getIdOsm(int idNodo) { return idOsmPorNodo.get(idNodo); }

    public InfoTramo getInfoTramo(int origen, int destino) { return infoTramos.get(clave(origen, destino)); }

    /** Número de tramos (pares de nodos conectados, sin contar el sentido). */
    public int getNumTramos() {
        Set<Long> pares = new HashSet<>();
        for (Arista a : grafo.getTodasLasAristas()) {
            pares.add(clave(Math.min(a.getOrigen(), a.getDestino()), Math.max(a.getOrigen(), a.getDestino())));
        }
        return pares.size();
    }

    /**
     * ¿El nodo pertenece a la parte conectada más grande de la red? Al recortar el mapa por un rectángulo
     * quedan pedazos sueltos (una calle cortada en el borde, un parqueadero); si el clic se asociara a uno
     * de ellos nunca habría ruta. Se calcula sin tener en cuenta los cierres, que los evalúa Dijkstra.
     */
    public boolean esNodoPrincipal(Nodo n) {
        if (n == null) return false;
        if (componentePrincipal == null) componentePrincipal = calcularComponentePrincipal();
        return componentePrincipal.contains(n.getId());
    }

    /** Reabre todos los tramos cerrados, para volver a aplicar la lista actual de cierres. */
    public int reabrirTramos() {
        Set<Long> reabiertos = new HashSet<>();
        for (Arista a : grafo.getTodasLasAristas()) {
            if (a.isBloqueada()) {
                long par = clave(Math.min(a.getOrigen(), a.getDestino()), Math.max(a.getOrigen(), a.getDestino()));
                if (reabiertos.add(par)) grafo.abrirArista(a.getOrigen(), a.getDestino());
            }
        }
        return reabiertos.size();
    }

    /** Longitud en metros de un camino recorriendo sus nodos en orden. */
    public static double longitudMetros(List<Nodo> camino) {
        double total = 0;
        for (int i = 0; i + 1 < camino.size(); i++) {
            Nodo a = camino.get(i);
            Nodo b = camino.get(i + 1);
            total += Geo.distanciaMetros(a.getLatitud(), a.getLongitud(), b.getLatitud(), b.getLongitud());
        }
        return total;
    }

    /**
     * Tiempo estimado (segundos) de recorrer el camino a la velocidad de referencia de cada vía
     * (límite "maxspeed" de OSM o un valor típico urbano según el tipo de vía). No considera tráfico.
     */
    public double tiempoSegundos(List<Nodo> camino) {
        double total = 0;
        for (int i = 0; i + 1 < camino.size(); i++) {
            Nodo a = camino.get(i);
            Nodo b = camino.get(i + 1);
            double metros = Geo.distanciaMetros(a.getLatitud(), a.getLongitud(), b.getLatitud(), b.getLongitud());
            InfoTramo info = getInfoTramo(a.getId(), b.getId());
            double kmh = info == null || info.velocidadKmh() <= 0 ? VelocidadesVia.POR_DEFECTO_KMH : info.velocidadKmh();
            total += metros / (kmh / 3.6);
        }
        return total;
    }

    /** Nombres de las vías del camino, en orden y sin repetir tramos consecutivos de la misma vía. */
    public List<String> nombresVias(List<Nodo> camino) {
        List<String> nombres = new ArrayList<>();
        for (int i = 0; i + 1 < camino.size(); i++) {
            InfoTramo info = getInfoTramo(camino.get(i).getId(), camino.get(i + 1).getId());
            String nombre = info == null || info.nombreVia() == null || info.nombreVia().isBlank()
                    ? null : info.nombreVia();
            if (nombre != null && (nombres.isEmpty() || !nombres.get(nombres.size() - 1).equals(nombre))) {
                nombres.add(nombre);
            }
        }
        return Collections.unmodifiableList(nombres);
    }

    private Set<Integer> calcularComponentePrincipal() {
        // Unión-búsqueda sobre las aristas sin sentido: rápido para cientos de miles de nodos.
        Map<Integer, Integer> padre = new HashMap<>();
        for (Nodo n : grafo.getNodos()) padre.put(n.getId(), n.getId());
        for (Arista a : grafo.getTodasLasAristas()) unir(padre, a.getOrigen(), a.getDestino());
        Map<Integer, Integer> tamanos = new HashMap<>();
        for (Integer id : padre.keySet()) tamanos.merge(raiz(padre, id), 1, Integer::sum);
        int mejorRaiz = -1;
        int mejorTamano = -1;
        for (Map.Entry<Integer, Integer> e : tamanos.entrySet()) {
            if (e.getValue() > mejorTamano) { mejorTamano = e.getValue(); mejorRaiz = e.getKey(); }
        }
        Set<Integer> resultado = new HashSet<>();
        for (Integer id : padre.keySet()) {
            if (raiz(padre, id) == mejorRaiz) resultado.add(id);
        }
        return resultado;
    }

    private static int raiz(Map<Integer, Integer> padre, int x) {
        int r = x;
        while (padre.get(r) != r) r = padre.get(r);
        while (padre.get(x) != r) { int siguiente = padre.get(x); padre.put(x, r); x = siguiente; }
        return r;
    }

    private static void unir(Map<Integer, Integer> padre, int a, int b) {
        int ra = raiz(padre, a);
        int rb = raiz(padre, b);
        if (ra != rb) padre.put(ra, rb);
    }

    static long clave(int origen, int destino) {
        return ((long) origen << 32) | (destino & 0xffffffffL);
    }
}
