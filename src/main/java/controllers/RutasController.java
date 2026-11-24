package controllers;

import models.Incidente;
import rutas.algoritmos.AlgoritmoRuta;
import rutas.algoritmos.Localizador;
import rutas.algoritmos.ReglasCierre;
import rutas.algoritmos.ResumenCierres;
import rutas.algoritmos.RutaResultado;
import rutas.algoritmos.RutasAlternas;
import rutas.modelo.Grafo;
import rutas.modelo.Nodo;
import rutas.persistencia.RepositorioGrafo;
import rutas.util.Constantes;
import java.io.IOException;
import java.util.List;

/**
 * Equipo 4 (RF-04). Orquesta el caso de uso completo:
 * cargar la red vial, aplicar cierres (CSV) e incidentes (lista en memoria del Equipo 3),
 * calcular la mejor ruta (y alternas) y exportarla.
 *
 * No toca la base de datos ni la interfaz: los incidentes llegan como List&lt;Incidente&gt; (por ejemplo
 * {@code new IncidenteDAO().obtenerTodosLosIncidentes()}) y el resultado se devuelve como RutaResultado.
 */
public class RutasController {
    private final RepositorioGrafo repositorio;
    private final ReglasCierre reglasCierre;
    private AlgoritmoRuta algoritmo;
    private Grafo grafo;

    public RutasController(RepositorioGrafo repositorio, AlgoritmoRuta algoritmo) {
        this(repositorio, algoritmo, new ReglasCierre());
    }

    public RutasController(RepositorioGrafo repositorio, AlgoritmoRuta algoritmo, ReglasCierre reglasCierre) {
        if (repositorio == null || algoritmo == null || reglasCierre == null) {
            throw new IllegalArgumentException("El repositorio, el algoritmo y las reglas de cierre son obligatorios");
        }
        this.repositorio = repositorio;
        this.algoritmo = algoritmo;
        this.reglasCierre = reglasCierre;
    }

    /** Carga nodos y tramos y reemplaza el grafo actual (se descartan cierres previos). */
    public void cargarDatos(String rutaNodos, String rutaTramos) throws IOException {
        this.grafo = repositorio.cargar(rutaNodos, rutaTramos);
    }

    /**
     * Usa un grafo ya construido (por ejemplo la red vial real de OpenStreetMap, ver {@code rutas.osm})
     * en lugar de leer los CSV. Reemplaza el grafo actual.
     */
    public void cargarGrafo(Grafo grafo) {
        if (grafo == null) throw new IllegalArgumentException("El grafo no puede ser null");
        this.grafo = grafo;
    }

    /** Aplica los cierres viales de un CSV sobre el grafo ya cargado. */
    public void aplicarCierres(String rutaCierres) throws IOException {
        repositorio.aplicarCierres(exigirGrafo(), rutaCierres);
    }

    /**
     * Cierra tramos según los incidentes, con el radio por defecto ({@link Constantes#RADIO_INCIDENTE_METROS}).
     * Los cierres se ACUMULAN: aplicar dos veces la lista no abre nada. Si un incidente se resuelve o se
     * elimina de la base de datos, hay que volver a llamar a {@link #cargarDatos(String, String)} y
     * aplicar de nuevo la lista actual de incidentes.
     */
    public ResumenCierres aplicarIncidentes(List<Incidente> incidentes) {
        return aplicarIncidentes(incidentes, Constantes.RADIO_INCIDENTE_METROS);
    }

    /** Cierra tramos según los incidentes; solo los de tipo bloqueante y a menos de radioMetros de un tramo. */
    public ResumenCierres aplicarIncidentes(List<Incidente> incidentes, double radioMetros) {
        return reglasCierre.aplicar(exigirGrafo(), incidentes, radioMetros);
    }

    /** Calcula la mejor ruta entre dos nodos (por id) con el algoritmo configurado. */
    public RutaResultado calcularRuta(int idOrigen, int idDestino) {
        return algoritmo.calcular(exigirGrafo(), idOrigen, idDestino);
    }

    /**
     * Calcula la mejor ruta entre dos puntos del mapa: cada punto se asocia al nodo más cercano del grafo.
     * Si la red vial está vacía devuelve un resultado "no encontrada".
     */
    public RutaResultado calcularRutaDesdeCoordenadas(double latOrigen, double lonOrigen,
                                                      double latDestino, double lonDestino) {
        Grafo g = exigirGrafo();
        Nodo origen = Localizador.nodoMasCercano(g, latOrigen, lonOrigen);
        Nodo destino = Localizador.nodoMasCercano(g, latDestino, lonDestino);
        if (origen == null || destino == null) {
            return RutaResultado.noEncontrada("La red vial no tiene nodos");
        }
        return algoritmo.calcular(g, origen.getId(), destino.getId());
    }

    /** Calcula hasta k rutas distintas (la mejor primero). Lista vacía si no hay ruta. */
    public List<RutaResultado> calcularRutasAlternas(int idOrigen, int idDestino, int k) {
        return new RutasAlternas(algoritmo).calcular(exigirGrafo(), idOrigen, idDestino, k);
    }

    /** Exporta la ruta a CSV para el equipo que dibuja el mapa. */
    public void exportarRuta(RutaResultado ruta, String rutaSalida) throws IOException {
        repositorio.exportarRuta(ruta, rutaSalida);
    }

    public void setAlgoritmo(AlgoritmoRuta algoritmo) {
        if (algoritmo == null) throw new IllegalArgumentException("El algoritmo no puede ser null");
        this.algoritmo = algoritmo;
    }

    public Grafo getGrafo() { return grafo; }

    private Grafo exigirGrafo() {
        if (grafo == null) {
            throw new IllegalStateException("Primero debe llamar a cargarDatos(...)");
        }
        return grafo;
    }
}
