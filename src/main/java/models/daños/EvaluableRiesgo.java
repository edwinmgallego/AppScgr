/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models.daños;

/**
 *
 * @author alex
 */
public interface EvaluableRiesgo {
 
    NivelRiesgo calcularNivelRiesgo();
 
    //evacuar si el riesgo es ALTO o mas
    default boolean requiereEvacuacion() {
        return calcularNivelRiesgo().esAlMenos(NivelRiesgo.ALTO);
    }
}

