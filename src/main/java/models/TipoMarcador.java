/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
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
    DERRUMBE("Derrumbe", true),
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
     * Traduce el tipo_incidente guardado en la base de datos. Los tipos que todavía
     * no tienen marcador propio (servicios, señal, daños) se muestran como OTRO.
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
