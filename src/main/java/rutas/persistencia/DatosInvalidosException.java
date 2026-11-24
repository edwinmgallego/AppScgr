// PROVISIONAL: implementación mínima de referencia para poder ejecutar las pruebas del Miembro 4.
// Será reemplazada por la versión real del Miembro 3. Respeta el contrato acordado.
package rutas.persistencia;

import java.io.IOException;

/** Error de formato en un archivo de datos; el mensaje incluye archivo y número de línea. */
public class DatosInvalidosException extends IOException {
    private static final long serialVersionUID = 1L;

    public DatosInvalidosException(String archivo, int linea, String detalle) {
        super(archivo + ", línea " + linea + ": " + detalle);
    }
}
