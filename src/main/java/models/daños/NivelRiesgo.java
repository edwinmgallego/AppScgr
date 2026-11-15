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
        return subir(1);
    }
 
    // sobrecarga: sube varios niveles
    public NivelRiesgo subir(int niveles) {
        int posicion = Math.min(ordinal() + niveles, CRITICO.ordinal());
        return values()[posicion];
    }
 
    // mira si es igual o mayor que otro
    public boolean esAlMenos(NivelRiesgo otro) {
        return ordinal() >= otro.ordinal();
    }
}


