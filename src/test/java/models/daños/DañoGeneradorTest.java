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
public class DañoGeneradorTest {
    private DañoGenerador generador(String falla, boolean critico) {
        return new DañoGenerador(1, "Cl 70 # 2A-30", "Alfonso López", 3.47, -76.49, "", falla, 60, critico);
    }
 
    @Test
    public void testRiesgoSegunLaFalla() {
        assertEquals(NivelRiesgo.CRITICO, generador("Fuga de combustible", false).calcularNivelRiesgo());
        assertEquals(NivelRiesgo.ALTO, generador("Cortocircuito", false).calcularNivelRiesgo());
        assertEquals(NivelRiesgo.MEDIO, generador("Daño físico", false).calcularNivelRiesgo());
        assertEquals(NivelRiesgo.BAJO, generador("No enciende", false).calcularNivelRiesgo());
    }
 
    @Test
    public void testServicioCriticoSubeUnNivel() {
        assertEquals(NivelRiesgo.MEDIO, generador("No enciende", true).calcularNivelRiesgo());
    }
 
    @Test
    public void testEvacuacionPorDefecto() {
        assertTrue(generador("Fuga de combustible", false).requiereEvacuacion());
        assertFalse(generador("No enciende", false).requiereEvacuacion());
    }
 
    @Test
    public void testPotenciaInvalidaLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> new DañoGenerador(1, "Dir", "Barrio", 3, -76, "", "No enciende", 0, false));
    }
}

