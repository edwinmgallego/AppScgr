package rutas.algoritmos;

import models.Incidente;
import rutas.modelo.Arista;
import rutas.modelo.Grafo;
import rutas.modelo.Nodo;
import rutas.util.Geo;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Regla de negocio: convierte incidentes (puntos con latitud/longitud) en cierres de tramos del grafo.
 *
 * 1. Solo los tipos "bloqueantes" cierran la vía. Por defecto VIA_CERRADA y DERRUMBE; el resto
 *    (FALLA_SERVICIO, SIN_SENAL, DANO_ESTRUCTURAL) se ignora. Es una decisión de negocio: se puede
 *    cambiar pasando otro conjunto al constructor.
 * 2. El incidente se asocia al tramo MÁS CERCANO (distancia punto-segmento). Si ese tramo está a
 *    más de {@code radioMetros}, el incidente queda como "sin tramo cercano" y no se cierra nada.
 * 3. Se consideran también los tramos ya cerrados, para que un incidente sobre una vía ya cerrada
 *    no termine cerrando una vía vecina por error.
 */
public class ReglasCierre {
    public static final Set<String> TIPOS_BLOQUEANTES_POR_DEFECTO = Set.of("VIA_CERRADA", "DERRUMBE");

    private final Set<String> tiposBloqueantes;

    public ReglasCierre() {
        this(TIPOS_BLOQUEANTES_POR_DEFECTO);
    }

    public ReglasCierre(Set<String> tiposBloqueantes) {
        if (tiposBloqueantes == null) throw new IllegalArgumentException("Los tipos bloqueantes son obligatorios");
        Set<String> normalizados = new HashSet<>();
        for (String t : tiposBloqueantes) normalizados.add(normalizar(t));
        this.tiposBloqueantes = normalizados;
    }

    /** ¿Este incidente cierra la vía según su tipo? (no distingue mayúsculas ni espacios) */
    public boolean bloquea(Incidente incidente) {
        return incidente != null && incidente.getTipo() != null
                && tiposBloqueantes.contains(normalizar(incidente.getTipo()));
    }

    /**
     * Cierra en el grafo los tramos afectados por los incidentes bloqueantes.
     *
     * @throws IllegalArgumentException si el grafo o la lista son null, o el radio no es positivo.
     */
    public ResumenCierres aplicar(Grafo g, List<Incidente> incidentes, double radioMetros) {
        if (g == null || incidentes == null) throw new IllegalArgumentException("Grafo e incidentes son obligatorios");
        if (!(radioMetros > 0)) throw new IllegalArgumentException("El radio debe ser mayor que cero");

        ResumenCierres resumen = new ResumenCierres();
        List<Arista> aristas = g.getTodasLasAristas();
        Set<String> tramosYaMarcados = new HashSet<>();

        for (Incidente inc : incidentes) {
            if (inc == null || !Geo.coordenadasValidas(inc.getLatitud(), inc.getLongitud())) {
                resumen.agregarInvalido(inc);
                continue;
            }
            if (!bloquea(inc)) {
                resumen.agregarNoBloqueante(inc);
                continue;
            }
            Arista masCercana = null;
            double mejor = Double.POSITIVE_INFINITY;
            for (Arista a : aristas) {
                Nodo o = g.getNodo(a.getOrigen());
                Nodo d = g.getNodo(a.getDestino());
                double dist = Geo.distanciaPuntoSegmentoMetros(inc.getLatitud(), inc.getLongitud(),
                        o.getLatitud(), o.getLongitud(), d.getLatitud(), d.getLongitud());
                if (dist < mejor) { mejor = dist; masCercana = a; }
            }
            if (masCercana == null || mejor > radioMetros) {
                resumen.agregarSinTramoCercano(inc);
                continue;
            }
            g.cerrarArista(masCercana.getOrigen(), masCercana.getDestino());
            if (tramosYaMarcados.add(clave(masCercana))) resumen.agregarTramoCerrado(masCercana);
        }
        return resumen;
    }

    private static String clave(Arista a) {
        return Math.min(a.getOrigen(), a.getDestino()) + "-" + Math.max(a.getOrigen(), a.getDestino());
    }

    private static String normalizar(String tipo) {
        return tipo == null ? "" : tipo.trim().toUpperCase(Locale.ROOT);
    }
}
