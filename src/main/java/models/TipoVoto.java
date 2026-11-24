/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

/**
 * Tipo de voto que un ciudadano puede emitir sobre un reporte de otro usuario.
 * Forma parte del RF-13 (validación comunitaria).
 *
 * @author Grupo 9 Gabriela Moreno - Validación Comunitaria y Dashboard
 */
public enum TipoVoto {
    CONFIRMA("Confirmar"),
    CUESTIONA("Cuestionar");

    private final String etiqueta;

    TipoVoto(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public String getCodigo() {
        return name();
    }   
}
