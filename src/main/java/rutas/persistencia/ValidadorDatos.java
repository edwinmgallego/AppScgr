// PROVISIONAL: implementación mínima de referencia para poder ejecutar las pruebas del Miembro 4.
// Será reemplazada por la versión real del Miembro 3. Respeta el contrato acordado.
package rutas.persistencia;

/** Validaciones de campos de CSV con reporte de línea. */
public final class ValidadorDatos {
    private ValidadorDatos() { }

    public static String[] campos(String linea, int esperados, String archivo, int nLinea)
            throws DatosInvalidosException {
        String[] c = linea.split(",", esperados);
        if (c.length < esperados) {
            throw new DatosInvalidosException(archivo, nLinea,
                    "se esperaban " + esperados + " columnas y hay " + c.length);
        }
        for (int i = 0; i < c.length; i++) c[i] = c[i].trim();
        return c;
    }

    public static int entero(String valor, String archivo, int nLinea) throws DatosInvalidosException {
        try {
            return Integer.parseInt(valor);
        } catch (NumberFormatException e) {
            throw new DatosInvalidosException(archivo, nLinea, "entero inválido '" + valor + "'");
        }
    }

    public static double decimal(String valor, String archivo, int nLinea) throws DatosInvalidosException {
        try {
            return Double.parseDouble(valor);
        } catch (NumberFormatException e) {
            throw new DatosInvalidosException(archivo, nLinea, "número inválido '" + valor + "'");
        }
    }

    public static boolean booleano(String valor, String archivo, int nLinea) throws DatosInvalidosException {
        if (valor.equalsIgnoreCase("true")) return true;
        if (valor.equalsIgnoreCase("false")) return false;
        throw new DatosInvalidosException(archivo, nLinea, "se esperaba true/false y llegó '" + valor + "'");
    }
}
