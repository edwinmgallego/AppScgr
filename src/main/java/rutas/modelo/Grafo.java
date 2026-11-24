// PROVISIONAL: implementación mínima de referencia para poder ejecutar las pruebas del Miembro 4.
// Será reemplazada por la versión real del Miembro 1. Respeta el contrato acordado.
package rutas.modelo;

import java.util.*;

/**
 * Grafo ponderado con lista de adyacencia.
 * Convenciones del contrato:
 * - getNodo devuelve null si el id no existe.
 * - getAdyacentes devuelve SOLO aristas no bloqueadas.
 * - cerrarArista/abrirArista afectan el tramo en ambos sentidos.
 * - getTodasLasAristas devuelve TODAS las aristas, también las bloqueadas (la usan las reglas de cierre).
 * - Los errores de uso (duplicados, pesos NaN/infinitos/negativos, coordenadas fuera de rango,
 *   nodos inexistentes) lanzan IllegalArgumentException.
 */
public class Grafo {
    private final Map<Integer, Nodo> nodos = new LinkedHashMap<>();
    private final Map<Integer, List<Arista>> adyacencia = new LinkedHashMap<>();

    public void agregarNodo(Nodo n) {
        if (n == null) throw new IllegalArgumentException("El nodo no puede ser null");
        if (Double.isNaN(n.getLatitud()) || Double.isNaN(n.getLongitud())
                || n.getLatitud() < -90 || n.getLatitud() > 90
                || n.getLongitud() < -180 || n.getLongitud() > 180) {
            throw new IllegalArgumentException("Coordenadas fuera de rango en el nodo " + n.getId()
                    + ": " + n.getLatitud() + ", " + n.getLongitud());
        }
        if (nodos.containsKey(n.getId())) {
            throw new IllegalArgumentException("Nodo duplicado: " + n.getId());
        }
        nodos.put(n.getId(), n);
        adyacencia.put(n.getId(), new ArrayList<>());
    }

    public void agregarArista(int idOrigen, int idDestino, double peso, boolean dirigida) {
        if (Double.isNaN(peso) || Double.isInfinite(peso) || peso < 0) {
            throw new IllegalArgumentException("Peso inválido (debe ser un número finito >= 0): " + peso);
        }
        exigirNodo(idOrigen);
        exigirNodo(idDestino);
        adyacencia.get(idOrigen).add(new Arista(idOrigen, idDestino, peso));
        if (!dirigida) {
            adyacencia.get(idDestino).add(new Arista(idDestino, idOrigen, peso));
        }
    }

    public Nodo getNodo(int id) { return nodos.get(id); }

    public List<Arista> getAdyacentes(int idNodo) {
        exigirNodo(idNodo);
        List<Arista> libres = new ArrayList<>();
        for (Arista a : adyacencia.get(idNodo)) {
            if (!a.isBloqueada()) libres.add(a);
        }
        return libres;
    }

    public void cerrarArista(int idOrigen, int idDestino) { cambiarBloqueo(idOrigen, idDestino, true); }

    public void abrirArista(int idOrigen, int idDestino) { cambiarBloqueo(idOrigen, idDestino, false); }

    /** Todas las aristas (arcos dirigidos), abiertas y bloqueadas. Un tramo bidireccional aparece dos veces. */
    public List<Arista> getTodasLasAristas() {
        List<Arista> todas = new ArrayList<>();
        for (List<Arista> lista : adyacencia.values()) todas.addAll(lista);
        return todas;
    }

    public Collection<Nodo> getNodos() { return Collections.unmodifiableCollection(nodos.values()); }

    public int numNodos() { return nodos.size(); }

    private void cambiarBloqueo(int o, int d, boolean bloquear) {
        exigirNodo(o);
        exigirNodo(d);
        boolean existe = false;
        for (Arista a : adyacencia.get(o)) {
            if (a.getDestino() == d) { a.setBloqueada(bloquear); existe = true; }
        }
        for (Arista a : adyacencia.get(d)) {
            if (a.getDestino() == o) { a.setBloqueada(bloquear); existe = true; }
        }
        if (!existe) throw new IllegalArgumentException("No existe tramo entre " + o + " y " + d);
    }

    private void exigirNodo(int id) {
        if (!nodos.containsKey(id)) throw new IllegalArgumentException("Nodo inexistente: " + id);
    }
}
