/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
/**
 *
 * @author USER
 */
public class MensajeEstadoTest {

    @Test
    public void testConstructorConUsuarioYUbicacion() {
        MensajeEstado m = new MensajeEstado(5, "Ana Pérez", "3001234567",
                EstadoSupervivencia.NECESITO_AYUDA, 3.4516, -76.5320);

        assertEquals(5, m.getUsuarioId());
        assertEquals("Ana Pérez", m.getNombreUsuario());
        assertEquals("3001234567", m.getTelefonoUsuario());
        assertEquals(EstadoSupervivencia.NECESITO_AYUDA, m.getEstado());
        assertEquals(Double.valueOf(3.4516), m.getLatitud());
        assertEquals(Double.valueOf(-76.5320), m.getLongitud());
        assertTrue(m.tieneUbicacion());
        assertFalse(m.isEnviado());
        assertNotNull(m.getFechaCreacion());
    }

    @Test
    public void testLaUbicacionPuedeSerNula() {
        MensajeEstado m = new MensajeEstado(5, "Ana Pérez", "3001234567",
                EstadoSupervivencia.ESTOY_BIEN, null, null);

        assertFalse(m.tieneUbicacion());
        assertNull(m.getLatitud());
        assertNull(m.getLongitud());
    }
}

