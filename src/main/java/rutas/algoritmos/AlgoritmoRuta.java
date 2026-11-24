// PROVISIONAL: implementación mínima de referencia para poder ejecutar las pruebas del Miembro 4.
// Será reemplazada por la versión real del Miembro 2. Respeta el contrato acordado.
package rutas.algoritmos;

import rutas.modelo.Grafo;

/** Contrato común de los algoritmos de ruta (patrón Strategy). */
public interface AlgoritmoRuta {
    /**
     * @throws IllegalArgumentException si el grafo es null o algún nodo no existe.
     */
    RutaResultado calcular(Grafo g, int idOrigen, int idDestino);
}
