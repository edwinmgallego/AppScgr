// PROVISIONAL: implementación mínima de referencia para poder ejecutar las pruebas del Miembro 4.
// Será reemplazada por la versión real del Miembro 3. Respeta el contrato acordado.
package rutas.persistencia;

import rutas.algoritmos.RutaResultado;
import rutas.modelo.Grafo;
import rutas.modelo.Nodo;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Lee nodos.csv, tramos.csv y cierres.csv (UTF-8, con encabezado).
 * Se ignoran líneas en blanco y las que empiezan con '#'.
 */
public class LectorCSV implements RepositorioGrafo {

    @Override
    public Grafo cargar(String rutaNodos, String rutaTramos) throws IOException {
        Grafo g = new Grafo();
        String archivo = nombre(rutaNodos);
        try (BufferedReader r = Files.newBufferedReader(Paths.get(rutaNodos), StandardCharsets.UTF_8)) {
            int n = 0;
            String linea;
            while ((linea = r.readLine()) != null) {
                n++;
                if (n == 1 || vacia(linea)) continue;
                String[] c = ValidadorDatos.campos(linea, 4, archivo, n);
                int id = ValidadorDatos.entero(c[0], archivo, n);
                double lat = ValidadorDatos.decimal(c[2], archivo, n);
                double lon = ValidadorDatos.decimal(c[3], archivo, n);
                try {
                    g.agregarNodo(new Nodo(id, c[1], lat, lon));
                } catch (IllegalArgumentException e) {
                    throw new DatosInvalidosException(archivo, n, e.getMessage());
                }
            }
        }
        archivo = nombre(rutaTramos);
        try (BufferedReader r = Files.newBufferedReader(Paths.get(rutaTramos), StandardCharsets.UTF_8)) {
            int n = 0;
            String linea;
            while ((linea = r.readLine()) != null) {
                n++;
                if (n == 1 || vacia(linea)) continue;
                String[] c = ValidadorDatos.campos(linea, 4, archivo, n);
                int o = ValidadorDatos.entero(c[0], archivo, n);
                int d = ValidadorDatos.entero(c[1], archivo, n);
                double dist = ValidadorDatos.decimal(c[2], archivo, n);
                boolean bidi = ValidadorDatos.booleano(c[3], archivo, n);
                try {
                    g.agregarArista(o, d, dist, !bidi);
                } catch (IllegalArgumentException e) {
                    throw new DatosInvalidosException(archivo, n, e.getMessage());
                }
            }
        }
        return g;
    }

    @Override
    public void aplicarCierres(Grafo g, String rutaCierres) throws IOException {
        String archivo = nombre(rutaCierres);
        try (BufferedReader r = Files.newBufferedReader(Paths.get(rutaCierres), StandardCharsets.UTF_8)) {
            int n = 0;
            String linea;
            while ((linea = r.readLine()) != null) {
                n++;
                if (n == 1 || vacia(linea)) continue;
                String[] c = ValidadorDatos.campos(linea, 3, archivo, n);
                int o = ValidadorDatos.entero(c[0], archivo, n);
                int d = ValidadorDatos.entero(c[1], archivo, n);
                try {
                    g.cerrarArista(o, d);
                } catch (IllegalArgumentException e) {
                    throw new DatosInvalidosException(archivo, n, e.getMessage());
                }
            }
        }
    }

    @Override
    public void exportarRuta(RutaResultado r, String rutaSalida) throws IOException {
        new EscritorCSV().exportar(r, rutaSalida);
    }

    private static boolean vacia(String linea) {
        String t = linea.trim();
        return t.isEmpty() || t.startsWith("#");
    }

    private static String nombre(String ruta) {
        Path p = Paths.get(ruta).getFileName();
        return p == null ? ruta : p.toString();
    }
}
