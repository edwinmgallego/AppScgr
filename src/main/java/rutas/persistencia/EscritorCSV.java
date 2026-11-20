// PROVISIONAL: implementación mínima de referencia para poder ejecutar las pruebas del Miembro 4.
// Será reemplazada por la versión real del Miembro 3. Respeta el contrato acordado.
package rutas.persistencia;

import rutas.algoritmos.RutaResultado;
import rutas.modelo.Nodo;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/** Exporta una ruta como CSV: orden,id,nombre,latitud,longitud. */
public class EscritorCSV {

    public void exportar(RutaResultado r, String rutaSalida) throws IOException {
        if (r == null || !r.isRutaEncontrada()) {
            throw new IllegalArgumentException("No hay ruta que exportar");
        }
        Path destino = Paths.get(rutaSalida);
        if (destino.getParent() != null) Files.createDirectories(destino.getParent());
        try (BufferedWriter w = Files.newBufferedWriter(destino, StandardCharsets.UTF_8)) {
            w.write("orden,id,nombre,latitud,longitud");
            w.newLine();
            int orden = 1;
            for (Nodo n : r.getCamino()) {
                w.write(orden++ + "," + n.getId() + "," + (n.getNombre() == null ? "" : n.getNombre().replace(',', ';'))
                        + "," + n.getLatitud() + "," + n.getLongitud());
                w.newLine();
            }
        }
    }
}
