// PROVISIONAL: implementación mínima de referencia para poder ejecutar las pruebas del Miembro 4.
// Será reemplazada por la versión real del Miembro 2. Respeta el contrato acordado.
package rutas.algoritmos;

import rutas.modelo.Arista;
import rutas.modelo.Grafo;
import rutas.modelo.Nodo;
import java.util.*;

/** Ruta con el menor número de tramos (ignora pesos para elegir, pero reporta el costo real). */
public class BFS implements AlgoritmoRuta {

    @Override
    public RutaResultado calcular(Grafo g, int idOrigen, int idDestino) {
        if (g == null || g.getNodo(idOrigen) == null || g.getNodo(idDestino) == null) {
            throw new IllegalArgumentException("Grafo nulo o nodo inexistente");
        }
        Map<Integer, Arista> via = new HashMap<>();
        Set<Integer> visitados = new HashSet<>();
        Deque<Integer> cola = new ArrayDeque<>();
        visitados.add(idOrigen);
        cola.add(idOrigen);

        while (!cola.isEmpty() && !visitados.contains(idDestino)) {
            int u = cola.poll();
            for (Arista a : g.getAdyacentes(u)) {
                if (visitados.add(a.getDestino())) {
                    via.put(a.getDestino(), a);
                    cola.add(a.getDestino());
                }
            }
        }
        if (!visitados.contains(idDestino)) {
            return RutaResultado.noEncontrada("No existe ruta de " + idOrigen + " a " + idDestino);
        }
        LinkedList<Nodo> camino = new LinkedList<>();
        double costo = 0;
        int actual = idDestino;
        camino.addFirst(g.getNodo(actual));
        while (actual != idOrigen) {
            Arista a = via.get(actual);
            costo += a.getPeso();
            actual = a.getOrigen();
            camino.addFirst(g.getNodo(actual));
        }
        return RutaResultado.encontrada(camino, costo);
    }
}
