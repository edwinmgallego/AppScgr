/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author oriana - Equipo 1 - Autenticación y Red Familiar
 */
public class ActualizadorBDTest {
     @Test
    public void testActualizarDejaLaBaseLista() {
        assertTrue(ActualizadorBD.actualizar());
    }

    @Test
    public void testSePuedeEjecutarVariasVeces() {
        assertTrue(ActualizadorBD.actualizar());
        assertTrue(ActualizadorBD.actualizar()); // la segunda vez no debe fallar
    }
}