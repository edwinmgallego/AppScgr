/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controllers;

/**
 *
 * @author sebas
 */
 
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
 
public class DañosControllerTest {

    @Test
    public void testLeerNumeroConPuntoOComa() {
        assertEquals(4.5, DañosController.leerNumero("4.5", "el ancho"));
        assertEquals(4.5, DañosController.leerNumero(" 4,5 ", "el ancho"));
    }

    @Test
    public void testLeerNumeroInvalidoLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> DañosController.leerNumero("abc", "el ancho"));
        assertThrows(IllegalArgumentException.class, () -> DañosController.leerNumero("", "el ancho"));
    }
}
