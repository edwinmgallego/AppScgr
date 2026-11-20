package rutas.algoritmos;

import rutas.modelo.Grafo;
import rutas.modelo.Nodo;
import rutas.util.Geo;

/** Asocia un punto del mapa (por ejemplo la ubicación del usuario) con el nodo más cercano del grafo. */
public final class Localizador {
    private Localizador() { }

    /**
     * @return el nodo más cercano, o null si el grafo no tiene nodos.
     * @throws IllegalArgumentException si el grafo es null o las coordenadas están fuera de rango.
     */
    public static Nodo nodoMasCercano(Grafo g, double latitud, double longitud) {
        if (g == null) throw new IllegalArgumentException("El grafo es obligatorio");
        if (!Geo.coordenadasValidas(latitud, longitud)) {
            throw new IllegalArgumentException("Coordenadas fuera de rango: " + latitud + ", " + longitud);
        }
        Nodo mejor = null;
        double mejorDist = Double.POSITIVE_INFINITY;
        for (Nodo n : g.getNodos()) {
            double d = Geo.distanciaMetros(latitud, longitud, n.getLatitud(), n.getLongitud());
            if (d < mejorDist) { mejorDist = d; mejor = n; }
        }
        return mejor;
    }
}
