/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
/**
 *
 * @author USER
 */
public class ColaMensajesTest {

    private ColaMensajes cola;

    @BeforeEach
    public void setUp() {
        cola = new ColaMensajes();
    }

    @Test
    public void testColaNuevaEstaVacia() {
        assertTrue(cola.estaVacia());
        assertEquals(0, cola.cantidadPendientes());
    }

    @Test
    public void testEncolarAumentaPendientes() {
        cola.encolar(new MensajeEstado(1, EstadoSupervivencia.ESTOY_BIEN));
        cola.encolar(new MensajeEstado(1, EstadoSupervivencia.NECESITO_AYUDA));
        assertEquals(2, cola.cantidadPendientes());
    }

    @Test
    public void testEncolarNuloLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> cola.encolar(null));
    }

    @Test
    public void testSincronizarEnviaEnOrdenFIFO() {
        MensajeEstado primero = new MensajeEstado(1, EstadoSupervivencia.NECESITO_AYUDA);
        MensajeEstado segundo = new MensajeEstado(1, EstadoSupervivencia.ESTOY_BIEN);
        cola.encolar(primero);
        cola.encolar(segundo);

        List<MensajeEstado> ordenDeEnvio = new ArrayList<>();
        int enviados = cola.sincronizar(m -> ordenDeEnvio.add(m));

        assertEquals(2, enviados);
        assertSame(primero, ordenDeEnvio.get(0));
        assertSame(segundo, ordenDeEnvio.get(1));
        assertTrue(cola.estaVacia());
        assertTrue(primero.isEnviado());
        assertTrue(segundo.isEnviado());
    }

    @Test
    public void testSiUnEnvioFallaSeConservaEseMensajeYLosSiguientes() {
        MensajeEstado m1 = new MensajeEstado(1, EstadoSupervivencia.ESTOY_BIEN);
        MensajeEstado m2 = new MensajeEstado(1, EstadoSupervivencia.NECESITO_AYUDA);
        MensajeEstado m3 = new MensajeEstado(1, EstadoSupervivencia.ESTOY_BIEN);
        cola.encolar(m1);
        cola.encolar(m2);
        cola.encolar(m3);

        // El segundo mensaje falla: se envía m1 y se detiene
        int enviados = cola.sincronizar(m -> m != m2);

        assertEquals(1, enviados);
        assertEquals(2, cola.cantidadPendientes());
        assertTrue(m1.isEnviado());
        assertFalse(m2.isEnviado());
        assertFalse(m3.isEnviado());
        assertSame(m2, cola.verPendientes().get(0)); // el orden se conserva
    }

    @Test
    public void testSincronizarColaVaciaNoEnviaNada() {
        assertEquals(0, cola.sincronizar(m -> true));
    }

    @Test
    public void testVerPendientesDevuelveCopia() {
        cola.encolar(new MensajeEstado(1, EstadoSupervivencia.ESTOY_BIEN));
        cola.verPendientes().clear(); // modificar la copia no debe afectar la cola
        assertEquals(1, cola.cantidadPendientes());
    }

    @Test
    public void testMensajeNuevoNoEstaEnviado() {
        MensajeEstado m = new MensajeEstado(1, EstadoSupervivencia.ESTOY_BIEN);
        assertFalse(m.isEnviado());
        assertNotNull(m.getFechaCreacion());
    }
}
