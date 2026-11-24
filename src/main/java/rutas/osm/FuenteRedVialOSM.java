/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rutas.osm;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Punto de entrada para obtener la red vial real de una zona:
 * descarga (o toma de la caché) el OSM XML con {@link ClienteOverpass} y lo convierte en grafo con {@link LectorOSM}.
 *
 * @author santi
 */
public class FuenteRedVialOSM implements ProveedorRedVial {

    private final ClienteOverpass cliente;
    private final LectorOSM lector;

    public FuenteRedVialOSM() {
        this(new ClienteOverpass(), new LectorOSM());
    }

    public FuenteRedVialOSM(ClienteOverpass cliente, LectorOSM lector) {
        if (cliente == null || lector == null) throw new IllegalArgumentException("Cliente y lector son obligatorios");
        this.cliente = cliente;
        this.lector = lector;
    }

    /** @throws IOException si no hay conexión ni caché, si el archivo está dañado o si la zona no tiene vías. */
    @Override
    public RedVialOSM cargar(LimitesGeo limites) throws IOException {
        Path archivo = cliente.obtenerArchivo(limites);
        RedVialOSM red;
        try {
            red = lector.leer(archivo, limites);
        } catch (IOException e) {
            cliente.invalidar(limites);   // respuesta dañada o de error: no dejarla en caché
            throw e;
        }
        if (red.getGrafo().numNodos() == 0) {
            cliente.invalidar(limites);
            throw new IOException("OpenStreetMap no tiene vías transitables en la zona seleccionada.");
        }
        return red;
    }

    @Override
    public boolean hayCacheVigente(LimitesGeo limites) {
        return cliente.hayCacheVigente(limites);
    }
}
