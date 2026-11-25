package rutas.algoritmos;

import rutas.modelo.Grafo;
import rutas.modelo.Nodo;
import rutas.util.Geo;

import java.util.function.Predicate;

/** Asocia un punto del mapa (por ejemplo la ubicación del usuario) con el nodo más cercano del grafo. */
public final class Localizador {
    private Localizador() { }

    /**
     * @return el nodo más cercano, o null si el grafo no tiene nodos.
     * @throws IllegalArgumentException si el grafo es null o las coordenadas están fuera de rango.
     */
    public static Nodo nodoMasCercano(Grafo g, double latitud, double longitud) {
        return nodoMasCercano(g, latitud, longitud, n -> true);
    }

    /**
     * Igual que {@link #nodoMasCercano(Grafo, double, double)} pero solo entre los nodos que cumplen el filtro
     * (por ejemplo, los que pertenecen a la parte conectada de la red vial de OpenStreetMap).
     *
     * @return el nodo admisible más cercano, o null si ninguno cumple el filtro.
     */
    public static Nodo nodoMasCercano(Grafo g, double latitud, double longitud, Predicate<Nodo> admisible) {
        if (admisible == null) throw new IllegalArgumentException("El filtro es obligatorio");
        if (g == null) throw new IllegalArgumentException("El grafo es obligatorio");
        if (!Geo.coordenadasValidas(latitud, longitud)) {
            throw new IllegalArgumentException("Coordenadas fuera de rango: " + latitud + ", " + longitud);
        }
        Nodo mejor = null;
        double mejorDist = Double.POSITIVE_INFINITY;
        for (Nodo n : g.getNodos()) {
            if (!admisible.test(n)) continue;
            double d = Geo.distanciaMetros(latitud, longitud, n.getLatitud(), n.getLongitud());
            if (d < mejorDist) { mejorDist = d; mejor = n; }
        }
        return mejor;
    }
}
