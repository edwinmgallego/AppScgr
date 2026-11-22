/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Titoelgato
 */
public class PuntoAsistenciaTest {
    
    @Test
    public void testCadaSubclaseDefineSuTipo() {
        assertEquals("ALBERGUE", new Albergue("A", 3.45, -76.53).getTipoPunto());
        assertEquals("SUPERMERCADO", new Supermercado("S", 3.45, -76.53).getTipoPunto());
        assertEquals("MECANICO", new Mecanico("M", 3.45, -76.53).getTipoPunto());
        assertEquals("HOSPITAL", new Hospital("H", 3.45, -76.53).getTipoPunto());
        assertEquals("ALBERGUE", new CentroAcopio("C", 3.45, -76.53).getTipoPunto());
    }
    @Test
    public void testPolimorfismoDeEtiquetasYServiciosSugeridos() {
        PuntoAsistencia[] puntos = {
            new Albergue("A", 3.45, -76.53),
            new Supermercado("S", 3.45, -76.53),
            new Mecanico("M", 3.45, -76.53),
            new CentroAcopio("C", 3.45, -76.53)
        };
        for (PuntoAsistencia punto : puntos) {
            assertFalse(punto.getEtiquetaTipo().isBlank());
            assertFalse(punto.getServiciosSugeridos().isEmpty());
        }
        assertEquals("Centro de acopio", puntos[3].getEtiquetaTipo());
    }

}
