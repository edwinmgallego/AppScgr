package rutas.osm;

import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Qué vías de OpenStreetMap sirven para circular en vehículo y a qué velocidad
 * de referencia. Las velocidades por defecto son valores urbanos típicos (Cali)
 * cuando la vía no trae "maxspeed".
 *
 * @author Equipo 4 - Rutas alternas (RF-04)
 */
public final class VelocidadesVia {

    public static final double POR_DEFECTO_KMH = 30.0;

    /**
     * Valor del tag highway → velocidad de referencia (km/h). Solo estas vías
     * entran al grafo.
     */
    private static final Map<String, Double> VELOCIDAD_POR_TIPO = Map.ofEntries(
            Map.entry("motorway", 80.0), Map.entry("motorway_link", 50.0),
            Map.entry("trunk", 60.0), Map.entry("trunk_link", 40.0),
            Map.entry("primary", 50.0), Map.entry("primary_link", 35.0),
            Map.entry("secondary", 45.0), Map.entry("secondary_link", 30.0),
            Map.entry("tertiary", 40.0), Map.entry("tertiary_link", 30.0),
            Map.entry("unclassified", 30.0), Map.entry("residential", 30.0),
            Map.entry("living_street", 10.0), Map.entry("service", 15.0),
            Map.entry("road", 30.0));

    /**
     * Expresión regular con los tipos anteriores, para la consulta Overpass.
     */
    public static final String REGEX_TIPOS_TRANSITABLES
            = "^(motorway|motorway_link|trunk|trunk_link|primary|primary_link|secondary|secondary_link"
            + "|tertiary|tertiary_link|unclassified|residential|living_street|service|road)$";

    private static final Pattern NUMERO = Pattern.compile("(\\d+(?:\\.\\d+)?)\\s*(mph)?");

    private VelocidadesVia() {
    }

    public static boolean esTransitable(String tipoVia) {
        return tipoVia != null && VELOCIDAD_POR_TIPO.containsKey(tipoVia.trim().toLowerCase(Locale.ROOT));
    }

    /**
     * Velocidad para una vía: usa maxspeed si es un número válido ("50", "50
     * km/h", "30 mph"); si no, la velocidad típica del tipo de vía.
     */
    public static double velocidadKmh(String tipoVia, String maxspeed) {
        if (maxspeed != null) {
            Matcher m = NUMERO.matcher(maxspeed.trim().toLowerCase(Locale.ROOT));
            if (m.lookingAt()) {
                double v = Double.parseDouble(m.group(1));
                if (m.group(2) != null) {
                    v *= 1.609344;
                }
                if (v >= 5 && v <= 130) {
                    return v;
                }
            }
        }
        if (tipoVia == null) {
            return POR_DEFECTO_KMH;
        }
        return VELOCIDAD_POR_TIPO.getOrDefault(tipoVia.trim().toLowerCase(Locale.ROOT), POR_DEFECTO_KMH);
    }
}