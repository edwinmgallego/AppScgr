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
 
    // Texto -> pares clave=valor
    public static Map<String, String> leer(String texto) {
        Map<String, String> datos = new HashMap<>();
        for (String parte : texto.split(" \\| ")) {
            int igual = parte.indexOf('=');
            if (igual > 0) {
                datos.put(parte.substring(0, igual), parte.substring(igual + 1));
            }
        }
        return datos;
    }
 
    // Texto -> objeto
    public static DañoEstructural crear(int id, int usuarioId, double latitud, double longitud, String texto) {
        Map<String, String> d = leer(texto);
        try {
            DañoEstructural daño = switch (TipoDaño.valueOf(d.get("daño"))) {
                case GRIETA -> new Grieta(usuarioId, d.get("direccion"), d.get("barrio"), latitud, longitud,
                        d.get("nota"), d.get("elemento"),
                        Double.parseDouble(d.get("ancho")), Double.parseDouble(d.get("largo")));
                case TANQUE_AGUA -> new FallaTanque(usuarioId, d.get("direccion"), d.get("barrio"), latitud, longitud,
                        d.get("nota"), d.get("falla"),
                        Integer.parseInt(d.get("capacidad")), Boolean.parseBoolean(d.get("elevado")));
                case GENERADOR -> new DañoGenerador(usuarioId, d.get("direccion"), d.get("barrio"), latitud, longitud,
                        d.get("nota"), d.get("falla"),
                        Double.parseDouble(d.get("potencia")), Boolean.parseBoolean(d.get("critico")));
            };
            daño.setId(id);
            return daño;
        } catch (RuntimeException e) {
            // pasa si alguien escribio el daño desde otra pantalla
            throw new IllegalArgumentException("La descripción no tiene el formato del módulo de daños");
        }
    }
 
    // Quita las | para que no se dañe el formato
    private static String limpiar(String texto) {
        return texto == null ? "" : texto.replace("|", "/");
    }
}
