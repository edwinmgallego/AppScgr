/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models.daños;

/**
 *
 * @author alex
 */
public enum NivelRiesgo {
    BAJO, MEDIO, ALTO, CRITICO;
 
    // sube un nivel (no pasa de CRITICO)
    public NivelRiesgo subir() {
        int posicion = Math.min(ordinal() + 1, CRITICO.ordinal());
        return values()[posicion];
    }
}

