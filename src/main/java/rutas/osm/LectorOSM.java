/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rutas.osm;

import rutas.modelo.Grafo;
import rutas.modelo.Nodo;
import rutas.util.Geo;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Convierte un archivo OSM XML (el que devuelve Overpass con "out body") en una {@link RedVialOSM}.
 *
 * Usa StAX (incluido en Java), así no hace falta agregar dependencias al pom. Respeta:
 * - Solo vías transitables en vehículo ({@link VelocidadesVia#esTransitable}).
 * - Sentido único (oneway=yes/-1, glorietas y autopistas).
 * - Vías con acceso prohibido (access=no/private), áreas y pasillos de parqueadero se descartan.
 *
 * @author santi
 */
public class LectorOSM {

    /** Datos mínimos de una vía mientras se lee el XML. */
    private static final class Via {
        final List<Long> refs = new ArrayList<>();
        final Map<String, String> tags = new HashMap<>();
    }

    public RedVialOSM leer(Path archivo, LimitesGeo limites) throws IOException {
        try (InputStream in = new BufferedInputStream(Files.newInputStream(archivo))) {
            return leer(in, limites);
        }
    }

    public RedVialOSM leer(InputStream in, LimitesGeo limites) throws IOException {
        Map<Long, double[]> coordenadas = new HashMap<>();
        List<Via> vias = new ArrayList<>();
        try {
            XMLInputFactory fabrica = XMLInputFactory.newFactory();
            fabrica.setProperty(XMLInputFactory.SUPPORT_DTD, false);
            fabrica.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
            XMLStreamReader r = fabrica.createXMLStreamReader(in, "UTF-8");
            Via actual = null;
            String mensajeError = null;
            try {
                while (r.hasNext()) {
                    int evento = r.next();
                    if (evento == XMLStreamConstants.START_ELEMENT) {
                        switch (r.getLocalName()) {
                            case "node" -> {
                                String id = r.getAttributeValue(null, "id");
                                String lat = r.getAttributeValue(null, "lat");
                                String lon = r.getAttributeValue(null, "lon");
                                if (id != null && lat != null && lon != null) {
                                    coordenadas.put(Long.parseLong(id),
                                            new double[]{Double.parseDouble(lat), Double.parseDouble(lon)});
                                }
                            }
                            case "way" -> actual = new Via();
                            case "nd" -> {
                                String ref = r.getAttributeValue(null, "ref");
                                if (actual != null && ref != null) actual.refs.add(Long.parseLong(ref));
                            }
                            case "tag" -> {
                                if (actual != null) {
                                    actual.tags.put(r.getAttributeValue(null, "k"), r.getAttributeValue(null, "v"));
                                }
                            }
                            case "remark" -> mensajeError = r.getElementText();
                            default -> { }
                        }
                    } else if (evento == XMLStreamConstants.END_ELEMENT && "way".equals(r.getLocalName())) {
                        if (actual != null) vias.add(actual);
                        actual = null;
                    }
                }
            } finally {
                r.close();
            }
            if (vias.isEmpty() && mensajeError != null && !mensajeError.isBlank()) {
                throw new IOException("OpenStreetMap (Overpass) respondió: " + mensajeError.trim());
            }
        } catch (XMLStreamException | NumberFormatException e) {
            throw new IOException("El archivo de OpenStreetMap no es válido: " + e.getMessage(), e);
        }
        return construir(coordenadas, vias, limites);
    }

    private RedVialOSM construir(Map<Long, double[]> coordenadas, List<Via> vias, LimitesGeo limites) {
        Grafo grafo = new Grafo();
        RedVialOSM red = new RedVialOSM(grafo, limites);
        Map<Long, Integer> idInterno = new HashMap<>();
        Map<Long, Integer> viasPorNodo = new HashMap<>();
        int usadas = 0;

        for (Via via : vias) {
            if (!esUtil(via)) continue;
            String tipo = via.tags.get("highway");
            String nombre = nombreDe(via);
            double velocidad = VelocidadesVia.velocidadKmh(tipo, via.tags.get("maxspeed"));
            int sentido = sentido(via);
            RedVialOSM.InfoTramo info = new RedVialOSM.InfoTramo(nombre, tipo, velocidad);
            boolean agregoAlgo = false;
            Long anterior = null;
            Set<Long> nodosDeEstaVia = new HashSet<>();

            for (Long ref : via.refs) {
                double[] c = coordenadas.get(ref);
                if (c == null || !Geo.coordenadasValidas(c[0], c[1])) { anterior = null; continue; }
                if (anterior != null && !anterior.equals(ref)) {
                    int a = obtenerNodo(grafo, red, idInterno, anterior, coordenadas.get(anterior), nombre);
                    int b = obtenerNodo(grafo, red, idInterno, ref, c, nombre);
                    double[] ca = coordenadas.get(anterior);
                    double metros = Geo.distanciaMetros(ca[0], ca[1], c[0], c[1]);
                    if (sentido > 0) {
                        grafo.agregarArista(a, b, metros, true);
                        red.registrarTramo(a, b, info);
                    } else if (sentido < 0) {
                        grafo.agregarArista(b, a, metros, true);
                        red.registrarTramo(b, a, info);
                    } else {
                        grafo.agregarArista(a, b, metros, false);
                        red.registrarTramo(a, b, info);
                        red.registrarTramo(b, a, info);
                    }
                    nodosDeEstaVia.add(anterior);
                    nodosDeEstaVia.add(ref);
                    agregoAlgo = true;
                }
                anterior = ref;
            }
            if (agregoAlgo) {
                usadas++;
                for (Long ref : nodosDeEstaVia) viasPorNodo.merge(ref, 1, Integer::sum);
            }
        }
        for (Map.Entry<Long, Integer> e : viasPorNodo.entrySet()) {
            if (e.getValue() >= 2) red.marcarInterseccion(idInterno.get(e.getKey()));
        }
        red.setNumVias(usadas);
        return red;
    }

    private static int obtenerNodo(Grafo g, RedVialOSM red, Map<Long, Integer> ids, Long idOsm,
                                   double[] c, String nombre) {
        Integer id = ids.get(idOsm);
        if (id == null) {
            id = ids.size() + 1;
            ids.put(idOsm, id);
            g.agregarNodo(new Nodo(id, nombre == null ? "Nodo OSM " + idOsm : nombre, c[0], c[1]));
            red.registrarIdOsm(id, idOsm);
        }
        return id;
    }

    /** ¿La vía sirve para circular en vehículo? */
    private static boolean esUtil(Via via) {
        if (via.refs.size() < 2) return false;
        if (!VelocidadesVia.esTransitable(via.tags.get("highway"))) return false;
        if ("yes".equalsIgnoreCase(via.tags.get("area"))) return false;
        String acceso = minus(via.tags.get("access"));
        String vehiculo = minus(via.tags.get("motor_vehicle"));
        if ((acceso.equals("no") || acceso.equals("private")) && !vehiculo.equals("yes")) return false;
        if (vehiculo.equals("no") || vehiculo.equals("private")) return false;
        String servicio = minus(via.tags.get("service"));
        return !servicio.equals("parking_aisle") && !servicio.equals("drive-through");
    }

    /** +1 solo en el sentido de dibujo, -1 solo en sentido contrario, 0 doble sentido. */
    private static int sentido(Via via) {
        String oneway = minus(via.tags.get("oneway"));
        switch (oneway) {
            case "yes", "true", "1" -> { return 1; }
            case "-1", "reverse" -> { return -1; }
            case "no", "false", "0", "reversible", "alternating" -> { return 0; }
            default -> { }
        }
        String junction = minus(via.tags.get("junction"));
        String tipo = minus(via.tags.get("highway"));
        if (junction.equals("roundabout") || junction.equals("circular")
                || tipo.equals("motorway") || tipo.equals("motorway_link")) {
            return 1;
        }
        return 0;
    }

    private static String nombreDe(Via via) {
        String nombre = via.tags.get("name");
        if (nombre == null || nombre.isBlank()) nombre = via.tags.get("ref");
        return nombre == null || nombre.isBlank() ? null : nombre.trim();
    }

    private static String minus(String s) {
        return s == null ? "" : s.trim().toLowerCase(Locale.ROOT);
    }
}
