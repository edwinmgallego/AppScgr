// PROVISIONAL: implementación mínima de referencia para poder ejecutar las pruebas del Miembro 4.
// Será reemplazada por la versión real del Miembro 2. Respeta el contrato acordado.
package rutas.algoritmos;

import rutas.modelo.Grafo;
import rutas.modelo.Nodo;
import java.util.*;

/**
 * Rutas alternas: calcula la mejor ruta y, para cada tramo de ella, la mejor ruta
 * que evita ese tramo. Devuelve hasta k rutas distintas ordenadas por costo.
 * El grafo queda exactamente como estaba al terminar.
 */
public class RutasAlternas {
    private final AlgoritmoRuta algoritmo;

    public RutasAlternas(AlgoritmoRuta algoritmo) { this.algoritmo = algoritmo; }

    /** @return lista vacía si no existe ninguna ruta. */
    public List<RutaResultado> calcular(Grafo g, int idOrigen, int idDestino, int k) {
        if (k < 1) throw new IllegalArgumentException("k debe ser >= 1");
        List<RutaResultado> resultado = new ArrayList<>();
        RutaResultado mejor = algoritmo.calcular(g, idOrigen, idDestino);
        if (!mejor.isRutaEncontrada()) return resultado;
        resultado.add(mejor);

        Set<List<Integer>> vistas = new HashSet<>();
        vistas.add(ids(mejor));
        List<RutaResultado> candidatas = new ArrayList<>();
        List<Nodo> camino = mejor.getCamino();

        for (int i = 0; i < camino.size() - 1; i++) {
            int a = camino.get(i).getId();
            int b = camino.get(i + 1).getId();
            g.cerrarArista(a, b);
            try {
                RutaResultado alt = algoritmo.calcular(g, idOrigen, idDestino);
                if (alt.isRutaEncontrada() && vistas.add(ids(alt))) candidatas.add(alt);
            } finally {
                g.abrirArista(a, b);
            }
        }
        candidatas.sort(Comparator.comparingDouble(RutaResultado::getCosto));
        for (RutaResultado c : candidatas) {
            if (resultado.size() >= k) break;
            resultado.add(c);
        }
        return resultado;
    }

    private static List<Integer> ids(RutaResultado r) {
        List<Integer> ids = new ArrayList<>();
        for (Nodo n : r.getCamino()) ids.add(n.getId());
        return ids;
    }
}
