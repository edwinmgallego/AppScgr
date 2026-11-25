/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

/**
 *
 * @author Daniel Alejandro
 */
public enum EstadoVia {
    CERRADA("Cerrada"),
    ABIERTA("Abierta");

    private final String etiqueta;

    EstadoVia(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    /** Estado al que pasa la vía cuando el usuario la reabre o la vuelve a cerrar. */
    public EstadoVia opuesto() {
        return this == ABIERTA ? CERRADA : ABIERTA;
    }

    public static EstadoVia fromString(String valor) {
        if (valor == null) return CERRADA;
        for (EstadoVia ev : values()) {
            if (ev.name().equalsIgnoreCase(valor.trim()) || ev.etiqueta.equalsIgnoreCase(valor.trim())) {
                return ev;
            }
        }
        return CERRADA;
    }
}