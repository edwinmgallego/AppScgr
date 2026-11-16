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
    
}
