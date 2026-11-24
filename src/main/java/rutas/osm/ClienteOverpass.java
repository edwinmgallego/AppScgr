package rutas.osm;

import config.ConfiguracionMapa;

import java.io.IOException;
import java.io.InputStream;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileTime;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Descarga la geometría de las vías de OpenStreetMap usando la API pública Overpass.
 *
 * Las teselas del mapa (imágenes) no traen la red vial; por eso las vías se piden aparte a Overpass,
 * en formato OSM XML: cada vía con su lista ordenada de nodos y cada nodo con su latitud/longitud.
 *
 * Las respuestas se guardan en una caché en disco (carpeta {@code cache/osm}) para no repetir descargas
 * de la misma zona y para poder seguir calculando rutas sin internet si la zona ya se descargó.
 *
 * @author santi
 */
public class ClienteOverpass {

    public static final List<String> SERVIDORES_POR_DEFECTO = List.of(
            "https://overpass-api.de/api/interpreter",
            "https://overpass.kumi.systems/api/interpreter",
            "https://overpass.private.coffee/api/interpreter");

    public static final Path CACHE_POR_DEFECTO = Paths.get("cache", "osm");
    public static final Duration VIGENCIA_CACHE = Duration.ofDays(7);

    private final List<String> servidores;
    private final Path carpetaCache;
    private final HttpClient http;

    public ClienteOverpass() {
        this(SERVIDORES_POR_DEFECTO, CACHE_POR_DEFECTO);
    }

    public ClienteOverpass(List<String> servidores, Path carpetaCache) {
        if (servidores == null || servidores.isEmpty()) throw new IllegalArgumentException("Se requiere al menos un servidor");
        if (carpetaCache == null) throw new IllegalArgumentException("La carpeta de caché es obligatoria");
        this.servidores = List.copyOf(servidores);
        this.carpetaCache = carpetaCache;
        this.http = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(20))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .proxy(ProxySelector.getDefault())
                .build();
    }

    /** Consulta Overpass QL: vías transitables dentro del rectángulo y todos sus nodos. */
    public static String construirConsulta(LimitesGeo limites) {
        return "[out:xml][timeout:120];\n"
                + "way[\"highway\"~\"" + VelocidadesVia.REGEX_TIPOS_TRANSITABLES + "\"][\"area\"!=\"yes\"]("
                + limites.comoOverpass() + ");\n"
                + "(._;>;);\n"
                + "out body;\n";
    }

    public Path archivoCache(LimitesGeo limites) {
        return carpetaCache.resolve("vias_" + limites.clave() + ".osm");
    }

    /** ¿Hay una copia en caché todavía vigente para esta zona? */
    public boolean hayCacheVigente(LimitesGeo limites) {
        Path f = archivoCache(limites);
        try {
            return Files.isRegularFile(f) && Files.size(f) > 0
                    && Files.getLastModifiedTime(f).toInstant().isAfter(Instant.now().minus(VIGENCIA_CACHE));
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Devuelve el archivo OSM XML de la zona: de la caché si está vigente; si no, lo descarga.
     * Si la descarga falla pero hay una copia vieja en caché, se usa la copia vieja.
     *
     * @throws IOException si no se pudo descargar y no hay ninguna copia local.
     */
    public Path obtenerArchivo(LimitesGeo limites) throws IOException {
        Path destino = archivoCache(limites);
        if (hayCacheVigente(limites)) return destino;
        try {
            descargar(limites, destino);
            return destino;
        } catch (IOException e) {
            if (Files.isRegularFile(destino) && Files.size(destino) > 0) return destino;
            throw e;
        }
    }

    /** Borra la copia en caché de una zona (por ejemplo si resultó dañada). */
    public void invalidar(LimitesGeo limites) {
        try {
            Files.deleteIfExists(archivoCache(limites));
        } catch (IOException ignorada) {
            // si no se puede borrar, se sobrescribirá en la próxima descarga
        }
    }

    private void descargar(LimitesGeo limites, Path destino) throws IOException {
        Files.createDirectories(destino.toAbsolutePath().getParent());
        String cuerpo = "data=" + URLEncoder.encode(construirConsulta(limites), StandardCharsets.UTF_8);
        List<String> errores = new ArrayList<>();

        for (String servidor : servidores) {
            Path temporal = Files.createTempFile(destino.toAbsolutePath().getParent(), "descarga", ".tmp");
            try {
                HttpRequest peticion = HttpRequest.newBuilder(URI.create(servidor))
                        .timeout(Duration.ofSeconds(150))
                        .header("User-Agent", ConfiguracionMapa.USER_AGENT)
                        .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                        .POST(HttpRequest.BodyPublishers.ofString(cuerpo))
                        .build();
                HttpResponse<InputStream> respuesta = http.send(peticion, HttpResponse.BodyHandlers.ofInputStream());
                try (InputStream in = respuesta.body()) {
                    if (respuesta.statusCode() != 200) {
                        errores.add(servidor + " → HTTP " + respuesta.statusCode());
                        continue;
                    }
                    Files.copy(in, temporal, StandardCopyOption.REPLACE_EXISTING);
                }
                if (Files.size(temporal) == 0) {
                    errores.add(servidor + " → respuesta vacía");
                    continue;
                }
                Files.move(temporal, destino, StandardCopyOption.REPLACE_EXISTING);
                Files.setLastModifiedTime(destino, FileTime.from(Instant.now()));
                return;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IOException("Descarga interrumpida", e);
            } catch (IOException | RuntimeException e) {
                errores.add(servidor + " → " + e.getClass().getSimpleName()
                        + (e.getMessage() == null ? "" : ": " + e.getMessage()));
            } finally {
                Files.deleteIfExists(temporal);
            }
        }
        throw new IOException("No se pudo descargar la red vial de OpenStreetMap. Verifique la conexión a internet.\n"
                + String.join("\n", errores));
    }
}
