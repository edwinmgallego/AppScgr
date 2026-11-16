/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Set;

/**
 *
 * @author Titoelgato
 */
public class ServiciosPunto {
    
    public static final int LIMITE_TEXTO = 255;
    
    public static final String ACOPIO = "ACOPIO";
    
    private static final String SEPARADOR = ",";
    private static final String MARCA_VOLUNTARIOS = ";VOLUNTARIOS=";

    private final Set<String> servicios = new LinkedHashSet<>();
    private int voluntariosNecesarios;

    public ServiciosPunto() {
    }
    
    public ServiciosPunto(List<String> servicios, int voluntariosNecesarios) {
        if (servicios != null) {
            for (String servicio : servicios) {
                agregar(servicio);
            }
        }
        setVoluntariosNecesarios(voluntariosNecesarios);
    }
    public static ServiciosPunto desdeTexto(String texto) {
        ServiciosPunto resultado = new ServiciosPunto();
        if (texto == null || texto.isBlank()) {
            return resultado;
        }
        
        String listaServicios = texto;
        int posicion = texto.toUpperCase(Locale.ROOT).indexOf(MARCA_VOLUNTARIOS);
        if (posicion >= 0) {
            listaServicios = texto.substring(0, posicion);
            String cantidad = texto.substring(posicion + MARCA_VOLUNTARIOS.length()).trim();
            try {
                int numero = Integer.parseInt(cantidad);
                if (numero >= 0) {
                    resultado.voluntariosNecesarios = numero;
                }
            } catch (NumberFormatException e) {
                // cantidad ilegible: se deja en 0
            }
        }
        for (String servicio : listaServicios.split(SEPARADOR)) {
            try {
                resultado.agregar(servicio);
            } catch (IllegalArgumentException e) {
                // servicio mal escrito: se ignora
            }
        }
        return resultado;
    }
}

