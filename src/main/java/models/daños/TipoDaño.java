/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models.daños;

/**
 *
 * @author alex
 */
public enum TipoDaño {
    GRIETA("Grieta peligrosa"),
    TANQUE_AGUA("Falla en tanque de agua"),
    GENERADOR("Generador dañado");
 
    private final String nombre;
 
    TipoDaño(String nombre) {
        this.nombre = nombre;
    }
 
    // para que el combo muestre el nombre y no GRIETA
    @Override
    public String toString() {
        return nombre;
    }
}

