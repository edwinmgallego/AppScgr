package rutas.algoritmos;

import models.Incidente;
import rutas.modelo.Arista;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Qué pasó con cada incidente al aplicarlos como cierres sobre el grafo. */
public class ResumenCierres {
    private final List<Arista> tramosCerrados = new ArrayList<>();
    private final List<Incidente> noBloqueantes = new ArrayList<>();
    private final List<Incidente> sinTramoCercano = new ArrayList<>();
    private final List<Incidente> invalidos = new ArrayList<>();

    void agregarTramoCerrado(Arista a) { tramosCerrados.add(a); }
    void agregarNoBloqueante(Incidente i) { noBloqueantes.add(i); }
    void agregarSinTramoCercano(Incidente i) { sinTramoCercano.add(i); }
    void agregarInvalido(Incidente i) { invalidos.add(i); }

    /** Tramos distintos que quedaron cerrados por incidentes (un representante por tramo). */
    public List<Arista> getTramosCerrados() { return Collections.unmodifiableList(tramosCerrados); }

    /** Incidentes cuyo tipo no cierra la vía (servicios, señal, daños...). */
    public List<Incidente> getNoBloqueantes() { return Collections.unmodifiableList(noBloqueantes); }

    /** Incidentes que cierran vía pero no están a menos del radio de ningún tramo del grafo. */
    public List<Incidente> getSinTramoCercano() { return Collections.unmodifiableList(sinTramoCercano); }

    /** Incidentes nulos o con coordenadas fuera de rango. */
    public List<Incidente> getInvalidos() { return Collections.unmodifiableList(invalidos); }

    @Override
    public String toString() {
        return "Cierres: " + tramosCerrados.size() + " tramo(s) cerrado(s), "
                + noBloqueantes.size() + " no bloqueante(s), "
                + sinTramoCercano.size() + " sin tramo cercano, "
                + invalidos.size() + " inválido(s)";
    }
}
