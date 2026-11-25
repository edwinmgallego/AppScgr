package models;

import java.util.Locale;

/**
 * Categorías de marcador que sabe dibujar el mapa. Las marcadas como "de incidente"
 * coinciden con el texto guardado en reportes_incidentes.tipo_incidente (base datos).
 *
 * @author Equipo 3 - Core del mapa y cierres viales
 */
public enum TipoMarcador {

    VIA_CERRADA("Vía cerrada", true),
    VIA_ABIERTA("Vía abierta", false),
    DERRUMBE("Derrumbe", true),
    ESTACION_MIO("Estación MIO", false),
    ESTACION_MIO_NO_OPERATIVA("MIO no operativa", false),
    FALLA_SERVICIO("Falla de servicio", true),
    SIN_SENAL("Sin señal", true),
    DANO_ESTRUCTURAL("Daño estructural", true),
    OTRO("Otro incidente", false),
    SELECCION("Punto seleccionado", false);

    private final String etiqueta;
    private final boolean deIncidente;

    TipoMarcador(String etiqueta, boolean deIncidente) {
        this.etiqueta = etiqueta;
        this.deIncidente = deIncidente;
    }

    public String getEtiqueta() { return etiqueta; }

    /**
     * Traduce el tipo_incidente guardado en la base de datos. Cada tipo del formulario
     * (vía cerrada, derrumbe, falla de servicio, sin señal, daño estructural) tiene su
     * propio marcador; solo un texto desconocido, nulo o vacío se muestra como OTRO.
     */
    public static TipoMarcador desdeTipoIncidente(String tipoIncidente) {
        if (tipoIncidente != null) {
            String codigo = tipoIncidente.trim().toUpperCase(Locale.ROOT);
            for (TipoMarcador tipo : values()) {
                if (tipo.deIncidente && tipo.name().equals(codigo)) {
                    return tipo;
                }
            }
        }
        return OTRO;
    }

    @Override
    public String toString() { return etiqueta; }
}