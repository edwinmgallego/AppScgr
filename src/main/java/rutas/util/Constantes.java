package rutas.util;

/** Rutas por defecto de los archivos de datos (siempre relativas al directorio del proyecto). */
public final class Constantes {
    public static final String NODOS_CSV = "data/nodos.csv";
    public static final String TRAMOS_CSV = "data/tramos.csv";
    public static final String CIERRES_CSV = "data/cierres.csv";
    public static final String SALIDA_RUTA_CSV = "salida/ruta.csv";
    public static final int NUM_RUTAS_ALTERNAS = 3;

    /**
     * Distancia máxima (metros) entre un incidente y un tramo para considerar que el incidente
     * está sobre ese tramo. Un clic en el mapa no es exacto, por eso no puede ser cero.
     */
    public static final double RADIO_INCIDENTE_METROS = 50.0;

    private Constantes() { }
}
