/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models.dao;

/**
 *
 * @author juans
 */

import models.daños.DañoEstructural;
import models.daños.DañoGenerador;
import models.daños.FallaTanque;
import models.daños.Grieta;
import models.daños.TipoDaño;
 
import java.util.HashMap;
import java.util.Map;
 
public class FormatoDescripcion {
 
    // asi esta en el ENUM del profe (sin ñ)
    public static final String TIPO_INCIDENTE = "DANO_ESTRUCTURAL";
 
    // Objeto -> texto
    public static String codificar(DañoEstructural daño) {
        String texto = "daño=" + daño.getTipoDaño().name()
                + " | direccion=" + limpiar(daño.getDireccion())
                + " | barrio=" + limpiar(daño.getBarrio());
 
        if (daño instanceof Grieta g) {
            texto += " | elemento=" + g.getElemento() + " | ancho=" + g.getAnchoMm() + " | largo=" + g.getLargoCm();
        } else if (daño instanceof FallaTanque t) {
            texto += " | falla=" + t.getTipoFalla() + " | capacidad=" + t.getCapacidadLitros() + " | elevado=" + t.isElevado();
        } else if (daño instanceof DañoGenerador gen) {
            texto += " | falla=" + gen.getTipoFalla() + " | potencia=" + gen.getPotenciaKva() + " | critico=" + gen.isServicioCritico();
        }
        return texto + " | nota=" + limpiar(daño.getDescripcion());
    }
 
    // Quita las | para que no se dañe el formato
    private static String limpiar(String texto) {
        return texto == null ? "" : texto.replace("|", "/");
    }
}
