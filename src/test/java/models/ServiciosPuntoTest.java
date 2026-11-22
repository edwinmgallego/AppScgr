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
    @Test
    public void testNormalizaMayusculasEspaciosYRepetidos() {
        ServiciosPunto s = ServiciosPunto.desdeTexto(" agua , Atención Medica,AGUA ");

        assertEquals(List.of("AGUA", "ATENCIÓN_MEDICA"), s.getServicios());
        assertTrue(s.contiene("agua"));
    }

    @Test
    public void testVoluntariosIlegiblesONegativosQuedanEnCero() {
        assertEquals(0, ServiciosPunto.desdeTexto("AGUA;VOLUNTARIOS=muchos").getVoluntariosNecesarios());
        assertEquals(0, ServiciosPunto.desdeTexto("AGUA;VOLUNTARIOS=-4").getVoluntariosNecesarios());
    }
    @Test
    public void testDesdeTextoIgnoraServiciosMalEscritos() {
        ServiciosPunto s = ServiciosPunto.desdeTexto("AGUA,RARO=1,INTERNET");

        assertEquals(List.of("AGUA", "INTERNET"), s.getServicios());
    }

    @Test
    public void testAgregarRechazaVaciosYSeparadores() {
        ServiciosPunto s = new ServiciosPunto();

        assertThrows(IllegalArgumentException.class, () -> s.agregar(null));
        assertThrows(IllegalArgumentException.class, () -> s.agregar("  "));
        assertThrows(IllegalArgumentException.class, () -> s.agregar("AGUA,INTERNET"));
        assertThrows(IllegalArgumentException.class, () -> s.agregar("A;B"));
    }
}
