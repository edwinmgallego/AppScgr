package rutas.util;

/** Cálculos geográficos sobre coordenadas WGS84 en grados decimales (latitud, longitud). */
public final class Geo {
    public static final double RADIO_TIERRA_METROS = 6_371_000.0;
    private static final double METROS_POR_GRADO = RADIO_TIERRA_METROS * Math.PI / 180.0;

    private Geo() { }

    /** Latitud en [-90, 90] y longitud en [-180, 180] (mismo rango que valida MarcadorMapa). */
    public static boolean coordenadasValidas(double latitud, double longitud) {
        return !Double.isNaN(latitud) && !Double.isNaN(longitud)
                && latitud >= -90 && latitud <= 90 && longitud >= -180 && longitud <= 180;
    }

    /** Distancia entre dos puntos sobre la esfera (fórmula de Haversine), en metros. */
    public static double distanciaMetros(double lat1, double lon1, double lat2, double lon2) {
        double f1 = Math.toRadians(lat1);
        double f2 = Math.toRadians(lat2);
        double df = Math.toRadians(lat2 - lat1);
        double dl = Math.toRadians(lon2 - lon1);
        double a = Math.sin(df / 2) * Math.sin(df / 2)
                + Math.cos(f1) * Math.cos(f2) * Math.sin(dl / 2) * Math.sin(dl / 2);
        return 2 * RADIO_TIERRA_METROS * Math.asin(Math.min(1.0, Math.sqrt(a)));
    }

    /**
     * Distancia en metros del punto P al segmento AB. Usa una proyección plana local centrada en P,
     * que es muy precisa para distancias urbanas (cientos de metros a pocos kilómetros).
     */
    public static double distanciaPuntoSegmentoMetros(double lat, double lon,
                                                      double latA, double lonA,
                                                      double latB, double lonB) {
        double kLon = METROS_POR_GRADO * Math.cos(Math.toRadians(lat));
        double ax = (lonA - lon) * kLon;
        double ay = (latA - lat) * METROS_POR_GRADO;
        double bx = (lonB - lon) * kLon;
        double by = (latB - lat) * METROS_POR_GRADO;
        double dx = bx - ax;
        double dy = by - ay;
        double largo2 = dx * dx + dy * dy;
        double t = largo2 == 0 ? 0 : -(ax * dx + ay * dy) / largo2;   // P está en el origen
        t = Math.max(0, Math.min(1, t));
        double cx = ax + t * dx;
        double cy = ay + t * dy;
        return Math.sqrt(cx * cx + cy * cy);
    }
}
