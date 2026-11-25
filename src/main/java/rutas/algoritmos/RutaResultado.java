// PROVISIONAL: implementación mínima de referencia para poder ejecutar las pruebas del Miembro 4.
// Será reemplazada por la versión real del Miembro 2. Respeta el contrato acordado.
package rutas.algoritmos;

import rutas.modelo.Nodo;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Resultado de un cálculo de ruta: camino (lista ordenada de nodos), costo y estado. */
public class RutaResultado {
    private final List<Nodo> camino;
    private final double costo;
    private final boolean rutaEncontrada;
    private final String mensaje;

    private RutaResultado(List<Nodo> camino, double costo, boolean encontrada, String mensaje) {
        this.camino = Collections.unmodifiableList(new ArrayList<>(camino));
        this.costo = costo;
        this.rutaEncontrada = encontrada;
        this.mensaje = mensaje;
    }

    public static RutaResultado encontrada(List<Nodo> camino, double costo) {
        return new RutaResultado(camino, costo, true, "Ruta encontrada");
    }

    public static RutaResultado noEncontrada(String mensaje) {
        return new RutaResultado(new ArrayList<>(), Double.POSITIVE_INFINITY, false, mensaje);
    }

    public List<Nodo> getCamino() { return camino; }
    public double getCosto() { return costo; }
    public boolean isRutaEncontrada() { return rutaEncontrada; }
    public String getMensaje() { return mensaje; }
}
