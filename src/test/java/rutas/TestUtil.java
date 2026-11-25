package rutas;

import rutas.algoritmos.RutaResultado;
import rutas.modelo.Grafo;
import rutas.modelo.Nodo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Utilidades compartidas por las pruebas. */
public final class TestUtil {
    private TestUtil() { }

    /** Ids del camino de un resultado, en orden. */
    public static List<Integer> ids(RutaResultado r) {
        List<Integer> ids = new ArrayList<>();
        for (Nodo n : r.getCamino()) ids.add(n.getId());
        return ids;
    }

    public static List<Integer> lista(Integer... ids) {
        return Arrays.asList(ids);
    }

    /** Grafo con los nodos 1..n (sin aristas). */
    public static Grafo grafoConNodos(int n) {
        Grafo g = new Grafo();
        for (int i = 1; i <= n; i++) g.agregarNodo(new Nodo(i, "N" + i, 0, 0));
        return g;
    }

    /** Punto medio del tramo 5-6 (coordenadas reales del grafo de ejemplo). */
    public static final double LAT_MEDIO_5_6 = 3.4265;
    public static final double LON_MEDIO_5_6 = -76.5385;
    /** Punto medio del tramo 3-4. */
    public static final double LAT_MEDIO_3_4 = 3.4620;
    public static final double LON_MEDIO_3_4 = -76.5310;
    /** Punto medio del tramo 4-7. */
    public static final double LAT_MEDIO_4_7 = 3.4725;
    public static final double LON_MEDIO_4_7 = -76.5265;

    /**
     * Mismo grafo que data/*.csv (sin cierres, con coordenadas). 1->8 óptimo: 1-2-5-6-8 = 4200;
     * alterna: 1-3-4-7-8 = 5000. El tramo 7->8 es de un solo sentido.
     */
    public static Grafo grafoEjemplo() {
        Grafo g = new Grafo();
        g.agregarNodo(new Nodo(1, "Centro", 3.4516, -76.5320));
        g.agregarNodo(new Nodo(2, "San Antonio", 3.4480, -76.5410));
        g.agregarNodo(new Nodo(3, "Granada", 3.4590, -76.5290));
        g.agregarNodo(new Nodo(4, "Versalles", 3.4650, -76.5330));
        g.agregarNodo(new Nodo(5, "Zona Sur A", 3.4330, -76.5370));
        g.agregarNodo(new Nodo(6, "Zona Sur B", 3.4200, -76.5400));
        g.agregarNodo(new Nodo(7, "Zona Norte", 3.4800, -76.5200));
        g.agregarNodo(new Nodo(8, "Punto de Encuentro", 3.4100, -76.5300));
        g.agregarArista(1, 2, 800, false);
        g.agregarArista(1, 3, 1200, false);
        g.agregarArista(2, 5, 900, false);
        g.agregarArista(3, 4, 1000, false);
        g.agregarArista(4, 7, 1500, false);
        g.agregarArista(5, 6, 1400, false);
        g.agregarArista(6, 8, 1100, false);
        g.agregarArista(7, 8, 1300, true);
        g.agregarArista(3, 5, 2000, false);
        return g;
    }
}
