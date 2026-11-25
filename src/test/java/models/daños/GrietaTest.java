/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models.daños;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
/**
 *
 * @author juans
 */
public class GrietaTest {
 
    private Grieta grieta(String elemento, double ancho, double largo) {
        return new Grieta(1, "Cl 5 # 10-20", "San Fernando", 3.43, -76.54, "", elemento, ancho, largo);
    }
 
    @Test
    public void testRiesgoEnElementoEstructural() {
        assertEquals(NivelRiesgo.MEDIO, grieta("Columna", 0.5, 20).calcularNivelRiesgo());
        assertEquals(NivelRiesgo.ALTO, grieta("Viga", 1, 20).calcularNivelRiesgo());
        assertEquals(NivelRiesgo.CRITICO, grieta("Losa", 3, 20).calcularNivelRiesgo());
    }
 
    @Test
    public void testRiesgoEnMuroOPiso() {
        assertEquals(NivelRiesgo.BAJO, grieta("Muro", 0.5, 20).calcularNivelRiesgo());
        assertEquals(NivelRiesgo.MEDIO, grieta("Piso", 1, 20).calcularNivelRiesgo());
        assertEquals(NivelRiesgo.ALTO, grieta("Muro", 5, 20).calcularNivelRiesgo());
    }
 
    @Test
    public void testGrietaLargaSubeUnNivel() {
        assertEquals(NivelRiesgo.MEDIO, grieta("Muro", 0.5, 100).calcularNivelRiesgo());
    }
 
    @Test
    public void testAnchoInvalidoLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> grieta("Muro", 0, 10));
        assertThrows(IllegalArgumentException.class, () -> grieta("Muro", 1, -5));
    }
 
    @Test
    public void testTipoYDetalle() {
        Grieta g = grieta("Columna", 4.5, 80);
        assertEquals(TipoDaño.GRIETA, g.getTipoDaño());
        assertEquals("Columna, 4.5 mm de ancho, 80.0 cm de largo", g.getDetalle());
    }
}

