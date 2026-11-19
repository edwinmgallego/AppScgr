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
public class FallaTanqueTest {
 
    private FallaTanque tanque(String falla, int litros, boolean elevado) {
        return new FallaTanque(1, "Av 6N # 25-60", "Granada", 3.46, -76.53, "", falla, litros, elevado);
    }
 
    @Test
    public void testRiesgoSegunLaFalla() {
        assertEquals(NivelRiesgo.CRITICO, tanque("Colapso", 1000, true).calcularNivelRiesgo());
        assertEquals(NivelRiesgo.ALTO, tanque("Contaminación", 1000, false).calcularNivelRiesgo());
        assertEquals(NivelRiesgo.MEDIO, tanque("Fuga", 1000, false).calcularNivelRiesgo());
        assertEquals(NivelRiesgo.BAJO, tanque("Fisura", 1000, false).calcularNivelRiesgo());
    }
 
    @Test
    public void testTanqueGrandeSubeUnNivel() {
        assertEquals(NivelRiesgo.MEDIO, tanque("Fisura", 5000, false).calcularNivelRiesgo());
    }
 
    @Test
    public void testEvacuacionSobreescrita() {
        assertTrue(tanque("Colapso", 1000, true).requiereEvacuacion());
        // es ALTO pero no toca salir de la casa
        assertFalse(tanque("Contaminación", 1000, true).requiereEvacuacion());
    }
 
    @Test
    public void testCapacidadInvalidaLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> tanque("Fuga", 0, false));
    }
}

