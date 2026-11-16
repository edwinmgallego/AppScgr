/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

/**
 *
 * @author Titoelgato
 */
public enum EstadoPunto {
    
    ABIERTO("Abierto"),
    CERRADO("Cerrado");
    
    private final String etiqueta;

    private EstadoPunto(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
    
    public static EstadoPunto desdeTexto(String texto) {
        if (texto == null || texto.isBlank()) {
            return ABIERTO;
        }
        return valueOf(texto.trim().toUpperCase(java.util.Locale.ROOT));
    }
}
