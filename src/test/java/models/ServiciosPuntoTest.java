package models;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Pruebas unitarias de ServiciosPunto: conversión texto-lista, validaciones y límite de la columna.
 *
 * @author Equipo 7 - Puntos de Asistencia y Negocios
 */
public class ServiciosPuntoTest {

    @Test
    void nuevoObjetoNoTieneServiciosNiVoluntarios() {
        ServiciosPunto s = new ServiciosPunto();
        assertTrue(s.getServicios().isEmpty());
        assertEquals(0, s.getVoluntariosNecesarios());
        assertEquals("", s.aTexto());
    }

    @Test
    void agregarNormalizaElNombreDelServicio() {
        ServiciosPunto s = new ServiciosPunto();
        assertTrue(s.agregar("  agua potable "));
        assertTrue(s.contiene("AGUA_POTABLE"));
        assertEquals(List.of("AGUA_POTABLE"), s.getServicios());
    }

    @Test
    void agregarUnServicioRepetidoDevuelveFalse() {
        ServiciosPunto s = new ServiciosPunto();
        assertTrue(s.agregar("AGUA"));
        assertFalse(s.agregar("agua"));
        assertEquals(1, s.getServicios().size());
    }

    @Test
    void agregarServicioVacioONuloLanzaExcepcion() {
        ServiciosPunto s = new ServiciosPunto();
        assertThrows(IllegalArgumentException.class, () -> s.agregar(null));
        assertThrows(IllegalArgumentException.class, () -> s.agregar("   "));
    }

    @Test
    void agregarServicioConCaracteresReservadosLanzaExcepcion() {
        ServiciosPunto s = new ServiciosPunto();
        assertThrows(IllegalArgumentException.class, () -> s.agregar("AGUA,LUZ"));
        assertThrows(IllegalArgumentException.class, () -> s.agregar("AGUA;LUZ"));
        assertThrows(IllegalArgumentException.class, () -> s.agregar("AGUA=1"));
    }

    @Test
    void quitarServicioExistenteYNoExistente() {
        ServiciosPunto s = new ServiciosPunto(List.of("AGUA", "INTERNET"), 0);
        assertTrue(s.quitar("agua"));
        assertFalse(s.quitar("AGUA"));
        assertFalse(s.quitar(null));
        assertEquals(List.of("INTERNET"), s.getServicios());
    }

    @Test
    void voluntariosNegativosLanzanExcepcion() {
        ServiciosPunto s = new ServiciosPunto();
        assertThrows(IllegalArgumentException.class, () -> s.setVoluntariosNecesarios(-1));
    }

    @Test
    void aTextoUneLosServiciosYAgregaLosVoluntarios() {
        ServiciosPunto s = new ServiciosPunto(List.of("AGUA", "INTERNET", "ALIMENTACION"), 5);
        assertEquals("AGUA,INTERNET,ALIMENTACION;VOLUNTARIOS=5", s.aTexto());
    }

    @Test
    void aTextoOmiteLaMarcaSiNoHayVoluntarios() {
        ServiciosPunto s = new ServiciosPunto(List.of("AGUA"), 0);
        assertEquals("AGUA", s.aTexto());
    }

    @Test
    void desdeTextoLeeServiciosYVoluntarios() {
        ServiciosPunto s = ServiciosPunto.desdeTexto("AGUA,INTERNET;VOLUNTARIOS=3");
        assertEquals(List.of("AGUA", "INTERNET"), s.getServicios());
        assertEquals(3, s.getVoluntariosNecesarios());
    }

    @Test
    void desdeTextoNuloOVacioDevuelveObjetoVacio() {
        assertTrue(ServiciosPunto.desdeTexto(null).getServicios().isEmpty());
        assertTrue(ServiciosPunto.desdeTexto("").getServicios().isEmpty());
        assertTrue(ServiciosPunto.desdeTexto("   ").getServicios().isEmpty());
    }

    @Test
    void desdeTextoIgnoraServiciosVaciosYCantidadIlegible() {
        ServiciosPunto s = ServiciosPunto.desdeTexto("AGUA,,INTERNET;VOLUNTARIOS=abc");
        assertEquals(List.of("AGUA", "INTERNET"), s.getServicios());
        assertEquals(0, s.getVoluntariosNecesarios());
    }

    @Test
    void desdeTextoIgnoraVoluntariosNegativos() {
        ServiciosPunto s = ServiciosPunto.desdeTexto("AGUA;VOLUNTARIOS=-4");
        assertEquals(0, s.getVoluntariosNecesarios());
    }

    @Test
    void aTextoYDesdeTextoSonInversos() {
        ServiciosPunto original = new ServiciosPunto(List.of("AGUA", "ENERGIA"), 7);
        ServiciosPunto copia = ServiciosPunto.desdeTexto(original.aTexto());
        assertEquals(original.getServicios(), copia.getServicios());
        assertEquals(original.getVoluntariosNecesarios(), copia.getVoluntariosNecesarios());
    }

    @Test
    void soloVoluntariosTambienSobreviveAlViaje() {
        ServiciosPunto original = new ServiciosPunto(List.of(), 4);
        ServiciosPunto copia = ServiciosPunto.desdeTexto(original.aTexto());
        assertTrue(copia.getServicios().isEmpty());
        assertEquals(4, copia.getVoluntariosNecesarios());
    }

    @Test
    void cabeEnColumnaRespetaElLimiteDe255() {
        ServiciosPunto corto = new ServiciosPunto(List.of("AGUA"), 0);
        assertTrue(corto.cabeEnColumna());

        ServiciosPunto largo = new ServiciosPunto();
        for (int i = 0; i < 40; i++) {
            largo.agregar("SERVICIO" + i);
        }
        assertFalse(largo.cabeEnColumna());
    }

    @Test
    void getServiciosDevuelveUnaListaNoModificable() {
        ServiciosPunto s = new ServiciosPunto(List.of("AGUA"), 0);
        List<String> lista = s.getServicios();
        assertThrows(UnsupportedOperationException.class, () -> lista.add("LUZ"));
    }
}
