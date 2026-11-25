package models;

import java.util.Locale;

/**
 * 
 * 
 *
 * @author User
 */
public enum TipoReporte {

    PERSONA("Persona"),
    MASCOTA("Mascota");

    private final String etiqueta;

    private TipoReporte(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    // Convierte el texto que viene de MySQL ("PERSONA") en el enum
    public static TipoReporte desdeTexto(String texto) {
        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException("El tipo de reporte no puede estar vacío");
        }
        return valueOf(texto.trim().toUpperCase(Locale.ROOT));
    }

    // Lo que se ve en un JComboBox o en la JTable
    @Override
    public String toString() {
        return etiqueta;
    }
}
