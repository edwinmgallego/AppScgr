// PROVISIONAL: implementación mínima de referencia para poder ejecutar las pruebas del Miembro 4.
// Será reemplazada por la versión real del Miembro 1. Respeta el contrato acordado.
package rutas.modelo;

import java.util.Objects;

/** Vértice del grafo vial (intersección o punto de interés). */
public class Nodo {
    private final int id;
    private final String nombre;
    private final double latitud;
    private final double longitud;

    public Nodo(int id, String nombre, double latitud, double longitud) {
        this.id = id;
        this.nombre = nombre;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public double getLatitud() { return latitud; }
    public double getLongitud() { return longitud; }

    @Override
    public boolean equals(Object o) {
        return o instanceof Nodo && ((Nodo) o).id == id;
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() { return id + ":" + nombre; }
}
