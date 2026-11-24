/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models.daños;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
/**
 *
 * @author juans
 */
public class DañoEstructuralTest {
    @Test
    public void testDireccionYBarrioSonObligatorios() {
        assertThrows(IllegalArgumentException.class,
                () -> new Grieta(1, "", "Barrio", 3, -76, "", "Muro", 1, 10));
        assertThrows(IllegalArgumentException.class,
                () -> new Grieta(1, "Dir", "  ", 3, -76, "", "Muro", 1, 10));
    }
 
    @Test
    public void testPolimorfismo() {
        // todos son DañoEstructural pero cada uno calcula distinto
        List<DañoEstructural> daños = List.of(
                new Grieta(1, "Dir", "Barrio", 3, -76, "", "Columna", 4, 10),
                new FallaTanque(1, "Dir", "Barrio", 3, -76, "", "Fuga", 500, false),
                new DañoGenerador(1, "Dir", "Barrio", 3, -76, "", "No enciende", 5, false));
 
        assertEquals(NivelRiesgo.CRITICO, daños.get(0).calcularNivelRiesgo());
        assertEquals(NivelRiesgo.MEDIO, daños.get(1).calcularNivelRiesgo());
        assertEquals(NivelRiesgo.BAJO, daños.get(2).calcularNivelRiesgo());
    }
 
    @Test
    public void testToString() {
        Grieta g = new Grieta(1, "Cra 4", "San Antonio", 3, -76, "", "Columna", 4, 10);
        assertEquals("Grieta peligrosa en Cra 4 (San Antonio) - riesgo CRITICO", g.toString());
    }
}

