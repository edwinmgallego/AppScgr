/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
/**
 *
 * @author Titoelgato
 */
public class ServiciosPuntoTest {
    
    @Test
    public void testDesdeTextoLeeServiciosYVoluntarios() {
        ServiciosPunto s = ServiciosPunto.desdeTexto("AGUA,INTERNET,ALIMENTACION;VOLUNTARIOS=5");

        assertEquals(List.of("AGUA", "INTERNET", "ALIMENTACION"), s.getServicios());
        assertEquals(5, s.getVoluntariosNecesarios());
    }
    
    @Test
    public void testATextoYDesdeTextoSonInversos() {
        ServiciosPunto original = new ServiciosPunto(List.of("AGUA", "ENERGIA"), 12);

        ServiciosPunto leido = ServiciosPunto.desdeTexto(original.aTexto());

        assertEquals("AGUA,ENERGIA;VOLUNTARIOS=12", original.aTexto());
        assertEquals(original.getServicios(), leido.getServicios());
        assertEquals(12, leido.getVoluntariosNecesarios());
    }
    
        @Test
    public void testTextoNuloOVacioDaServiciosVacios() {
        for (String texto : new String[] {null, "", "   "}) {
            ServiciosPunto s = ServiciosPunto.desdeTexto(texto);
            assertTrue(s.getServicios().isEmpty());
            assertEquals(0, s.getVoluntariosNecesarios());
            assertEquals("", s.aTexto());
        }
    }

    @Test
    public void testSoloVoluntariosSinServicios() {
        ServiciosPunto s = ServiciosPunto.desdeTexto(";VOLUNTARIOS=3");

        assertTrue(s.getServicios().isEmpty());
        assertEquals(3, s.getVoluntariosNecesarios());
        assertEquals(";VOLUNTARIOS=3", s.aTexto());
    
}
}
