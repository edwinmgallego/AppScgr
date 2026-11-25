/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models.daños;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


/**
 *
 * @author alex
 */
public class NivelRiesgoTest {
 
    @Test
    public void testSubirUnNivel() {
        assertEquals(NivelRiesgo.MEDIO, NivelRiesgo.BAJO.subir());
        assertEquals(NivelRiesgo.CRITICO, NivelRiesgo.ALTO.subir());
    }
 
    @Test
    public void testSubirNoPasaDeCritico() {
        assertEquals(NivelRiesgo.CRITICO, NivelRiesgo.CRITICO.subir());
        assertEquals(NivelRiesgo.CRITICO, NivelRiesgo.BAJO.subir(10));
    }
 
    @Test
    public void testSubirVariosNiveles() {
        assertEquals(NivelRiesgo.ALTO, NivelRiesgo.BAJO.subir(2));
    }
 
    @Test
    public void testEsAlMenos() {
        assertTrue(NivelRiesgo.ALTO.esAlMenos(NivelRiesgo.MEDIO));
        assertTrue(NivelRiesgo.ALTO.esAlMenos(NivelRiesgo.ALTO));
        assertFalse(NivelRiesgo.BAJO.esAlMenos(NivelRiesgo.MEDIO));
    }
}

