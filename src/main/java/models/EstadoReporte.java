package models;

import java.util.Locale;

/**
 * 
 *
 * @author User
 */
public enum EstadoReporte {

    PERDIDO("Perdido"),
    ENCONTRADO("Encontrado");

    private final String etiqueta;

    private EstadoReporte(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    // Si viene vacío o null devolvemos PERDIDO, igual que el valor por defecto en MySQL
    public static EstadoReporte desdeTexto(String texto) {
        if (texto == null || texto.isBlank()) {
            return PERDIDO;
        }
        return valueOf(texto.trim().toUpperCase(Locale.ROOT));
    }

    @Override
    public String toString() {
        return etiqueta;
    }
}
