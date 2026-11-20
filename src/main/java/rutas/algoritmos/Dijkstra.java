// PROVISIONAL: implementación mínima de referencia para poder ejecutar las pruebas del Miembro 4.
// Será reemplazada por la versión real del Miembro 2. Respeta el contrato acordado.
package rutas.algoritmos;

import rutas.modelo.Arista;
import rutas.modelo.Grafo;
import rutas.modelo.Nodo;
import java.util.*;

/** Camino de menor costo. Complejidad O((V+E) log V) con cola de prioridad. */
public class Dijkstra implements AlgoritmoRuta {

    @Override
    public RutaResultado calcular(Grafo g, int idOrigen, int idDestino) {
        if (g == null || g.getNodo(idOrigen) == null || g.getNodo(idDestino) == null) {
            throw new IllegalArgumentException("Grafo nulo o nodo inexistente");
        }
        Map<Integer, Double> dist = new HashMap<>();
        Map<Integer, Integer> previo = new HashMap<>();
        Set<Integer> visitados = new HashSet<>();
        PriorityQueue<double[]> cola = new PriorityQueue<>(Comparator.comparingDouble(a -> a[0]));

        dist.put(idOrigen, 0.0);
        cola.add(new double[]{0.0, idOrigen});

        while (!cola.isEmpty()) {
            int u = (int) cola.poll()[1];
            if (!visitados.add(u)) continue;
            if (u == idDestino) break;
            for (Arista a : g.getAdyacentes(u)) {
                int v = a.getDestino();
                double nuevo = dist.get(u) + a.getPeso();
                if (nuevo < dist.getOrDefault(v, Double.POSITIVE_INFINITY)) {
                    dist.put(v, nuevo);
                    previo.put(v, u);
                    cola.add(new double[]{nuevo, v});
                }
            }
        }
        if (!visitados.contains(idDestino)) {
            return RutaResultado.noEncontrada("No existe ruta de " + idOrigen + " a " + idDestino);
        }
        LinkedList<Nodo> camino = new LinkedList<>();
        for (Integer actual = idDestino; actual != null; actual = previo.get(actual)) {
            camino.addFirst(g.getNodo(actual));
        }
        return RutaResultado.encontrada(camino, dist.get(idDestino));
    }
}
