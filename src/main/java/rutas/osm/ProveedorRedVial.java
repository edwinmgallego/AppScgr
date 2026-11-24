package rutas.osm;

import java.io.IOException;

/**
 * Origen de la red vial de una zona. La implementación real es {@link FuenteRedVialOSM} (OpenStreetMap);
 * las pruebas usan una red armada en memoria.
 *
 * @author santi
 */
public interface ProveedorRedVial {

    RedVialOSM cargar(LimitesGeo limites) throws IOException;

    /** ¿La zona ya está disponible sin descargar? (solo sirve para mostrar el mensaje adecuado) */
    default boolean hayCacheVigente(LimitesGeo limites) { return false; }
}
