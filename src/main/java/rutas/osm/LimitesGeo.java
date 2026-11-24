package rutas.osm;

import rutas.util.Geo;
import java.util.Locale;

/**
 * Rectángulo geográfico (bounding box) en grados WGS84: sur, oeste, norte y
 * este. Se usa para pedir a OpenStreetMap solo las vías de la zona donde está
 * la ruta.
 *
 * @author santi
 */
public final class LimitesGeo {

    private final double sur;
    private final double oeste;
    private final double norte;
    private final double este;

    public LimitesGeo(double sur, double oeste, double norte, double este) {
        if (!Geo.coordenadasValidas(sur, oeste) || !Geo.coordenadasValidas(norte, este)) {
            throw new IllegalArgumentException("Límites fuera de rango");
        }
        if (sur >= norte || oeste >= este) {
            throw new IllegalArgumentException("Límites invertidos o vacíos: " + sur + "," + oeste + " / " + norte + "," + este);
        }
        this.sur = sur;
        this.oeste = oeste;
        this.norte = norte;
        this.este = este;
    }

    /**
     * Rectángulo que contiene los dos puntos más un margen. El margen es fijo
     * (margenMinimoGrados) más una fracción del tamaño del recorrido, para
     * dejar espacio a desvíos por cierres viales. Los bordes se redondean hacia
     * afuera a una cuadrícula, así consultas cercanas reutilizan la caché.
     */
    public static LimitesGeo alrededorDe(double lat1, double lon1, double lat2, double lon2,
            double margenMinimoGrados, double fraccionExtra, double pasoCuadricula) {
        if (!Geo.coordenadasValidas(lat1, lon1) || !Geo.coordenadasValidas(lat2, lon2)) {
            throw new IllegalArgumentException("Coordenadas fuera de rango");
        }
        double alto = Math.abs(lat1 - lat2);
        double ancho = Math.abs(lon1 - lon2);
        double margen = margenMinimoGrados + fraccionExtra * Math.max(alto, ancho);
        double s = Math.min(lat1, lat2) - margen;
        double n = Math.max(lat1, lat2) + margen;
        double o = Math.min(lon1, lon2) - margen;
        double e = Math.max(lon1, lon2) + margen;
        if (pasoCuadricula > 0) {
            s = Math.floor(s / pasoCuadricula) * pasoCuadricula;
            o = Math.floor(o / pasoCuadricula) * pasoCuadricula;
            n = Math.ceil(n / pasoCuadricula) * pasoCuadricula;
            e = Math.ceil(e / pasoCuadricula) * pasoCuadricula;
        }
        return new LimitesGeo(Math.max(-90, s), Math.max(-180, o), Math.min(90, n), Math.min(180, e));
    }

    public double getSur() {
        return sur;
    }

    public double getOeste() {
        return oeste;
    }

    public double getNorte() {
        return norte;
    }

    public double getEste() {
        return este;
    }

    public boolean contiene(double lat, double lon) {
        return lat >= sur && lat <= norte && lon >= oeste && lon <= este;
    }

    /**
     * ¿El punto está dentro y al menos a 'margen' grados de cada borde?
     */
    public boolean contieneConHolgura(double lat, double lon, double margen) {
        return lat >= sur + margen && lat <= norte - margen && lon >= oeste + margen && lon <= este - margen;
    }

    /**
     * Lado mayor del rectángulo en grados.
     */
    public double ladoMayorGrados() {
        return Math.max(norte - sur, este - oeste);
    }

    /**
     * Formato que espera Overpass: (sur,oeste,norte,este).
     */
    public String comoOverpass() {
        return String.format(Locale.ROOT, "%.5f,%.5f,%.5f,%.5f", sur, oeste, norte, este);
    }

    /**
     * Nombre de archivo estable para la caché local.
     */
    public String clave() {
        return String.format(Locale.ROOT, "%.4f_%.4f_%.4f_%.4f", sur, oeste, norte, este).replace('-', 'm');
    }

    @Override
    public String toString() {
        return "[" + comoOverpass() + "]";
    }
}
