package rutas.osm;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Red vial de prueba en formato OSM XML (el mismo que devuelve Overpass), ubicada en Cali.
 *
 * <pre>
 *  (lat 3.4540)  N1 ---- N2 ---- N3 ---- N4 ---- N5     "Calle 2"   (sentido configurable)
 *                 |                               |
 *            "Carrera A"                     "Carrera B"
 *                 |                               |
 *  (lat 3.4500)  S1 ---- S2 ---- S3 ---- S4 ---- S5     "Calle 1"   (S3 es el centro: ahí se pone el cierre)
 *              lon -76.540                    lon -76.520
 * </pre>
 * Además: un sendero peatonal (footway) que sería un atajo y no debe usarse, una vía privada,
 * un pedazo de vía aislado lejos de todo y una curva ("Avenida Curva") con muchos puntos de forma.
 */
public final class RedPrueba {

    public static final double LAT_SUR = 3.4500;
    public static final double LAT_NORTE = 3.4540;
    public static final double LON_OESTE = -76.540;
    public static final double LON_ESTE = -76.520;

    private final StringBuilder nodos = new StringBuilder();
    private final StringBuilder vias = new StringBuilder();
    private long siguienteVia = 900;

    private RedPrueba() { }

    /** @param calle2 valor del tag oneway de "Calle 2" (null = doble sentido; "-1" = solo de este a oeste) */
    public static String xml(String calle2) {
        RedPrueba r = new RedPrueba();
        double[] lons = {-76.540, -76.535, -76.530, -76.525, -76.520};
        for (int i = 0; i < 5; i++) {
            r.nodo(10 + i, LAT_SUR, lons[i]);              // S1..S5 -> 10..14
            r.nodo(20 + i, LAT_NORTE, lons[i]);            // N1..N5 -> 20..24
        }
        r.via("Calle 1", "secondary", null, null, 10, 11, 12, 13, 14);
        r.via("Calle 2", "residential", calle2, "30", 20, 21, 22, 23, 24);
        r.via("Carrera A", "tertiary", null, null, 10, 20);
        r.via("Carrera B", "tertiary", null, null, 14, 24);
        // atajo peatonal y vía privada: no deben entrar al grafo
        r.nodo(30, 3.4520, -76.530);
        r.via("Sendero", "footway", null, null, 12, 30, 22);
        r.via("Privada", "residential", null, null, 11, 21, "access", "private");
        // vía aislada (pedazo suelto) muy cerca del punto (3.4560, -76.5300)
        r.nodo(40, 3.4560, -76.5302);
        r.nodo(41, 3.4560, -76.5298);
        r.via("Parqueadero suelto", "service", null, null, 40, 41);
        // curva con 9 puntos de forma al sur, unida a S1 y S5 (más larga que Calle 1)
        List<Long> curva = new ArrayList<>();
        curva.add(10L);
        for (int k = 1; k <= 9; k++) {
            double t = k / 10.0;
            r.nodo(50 + k, LAT_SUR - 0.004 * Math.sin(Math.PI * t), -76.540 + 0.020 * t);
            curva.add(50L + k);
        }
        curva.add(14L);
        r.via("Avenida Curva", "primary", null, null, curva.stream().mapToLong(Long::longValue).toArray());
        return r.cerrar();
    }

    public static RedVialOSM red(String calle2) throws IOException {
        return new LectorOSM().leer(new ByteArrayInputStream(xml(calle2).getBytes(StandardCharsets.UTF_8)),
                new LimitesGeo(3.44, -76.55, 3.47, -76.51));
    }

    private void nodo(long id, double lat, double lon) {
        nodos.append(String.format(Locale.ROOT, "  <node id=\"%d\" lat=\"%.7f\" lon=\"%.7f\"/>%n", id, lat, lon));
    }

    private void via(String nombre, String tipo, String oneway, String maxspeed, long... refs) {
        via(nombre, tipo, oneway, maxspeed, refs, new String[0]);
    }

    private void via(String nombre, String tipo, String oneway, String maxspeed, long a, long b, String k, String v) {
        via(nombre, tipo, oneway, maxspeed, new long[]{a, b}, new String[]{k, v});
    }

    private void via(String nombre, String tipo, String oneway, String maxspeed, long[] refs, String[] extra) {
        vias.append("  <way id=\"").append(siguienteVia++).append("\">\n");
        for (long r : refs) vias.append("    <nd ref=\"").append(r).append("\"/>\n");
        tag("highway", tipo);
        tag("name", nombre);
        if (oneway != null) tag("oneway", oneway);
        if (maxspeed != null) tag("maxspeed", maxspeed);
        for (int i = 0; i + 1 < extra.length; i += 2) tag(extra[i], extra[i + 1]);
        vias.append("  </way>\n");
    }

    private void tag(String k, String v) {
        vias.append("    <tag k=\"").append(k).append("\" v=\"").append(v).append("\"/>\n");
    }

    private String cerrar() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<osm version=\"0.6\" generator=\"prueba\">\n"
                + nodos + vias + "</osm>\n";
    }
}
