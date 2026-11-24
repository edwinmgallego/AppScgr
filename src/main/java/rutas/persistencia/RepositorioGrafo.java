// PROVISIONAL: implementación mínima de referencia para poder ejecutar las pruebas del Miembro 4.
// Será reemplazada por la versión real del Miembro 3. Respeta el contrato acordado.
package rutas.persistencia;

import rutas.algoritmos.RutaResultado;
import rutas.modelo.Grafo;
import java.io.IOException;

/** Contrato de persistencia: carga de datos, cierres y exportación de resultados. */
public interface RepositorioGrafo {
    Grafo cargar(String rutaNodos, String rutaTramos) throws IOException;
    void aplicarCierres(Grafo g, String rutaCierres) throws IOException;
    void exportarRuta(RutaResultado r, String rutaSalida) throws IOException;
}
