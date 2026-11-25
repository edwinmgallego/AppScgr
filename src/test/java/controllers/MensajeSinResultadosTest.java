package controllers;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas del mensaje que sale cuando la búsqueda de familiares no encuentra a nadie
 * (no necesitan base de datos).
 *
 * @author (escribe aquí tu nombre)
 */
public class MensajeSinResultadosTest {

    @Test
    public void testIncluyeLoQueSeBusco() {
        String mensaje = FamiliaresController.mensajeSinResultados("Ana Pérez");
        assertTrue(mensaje.contains("Ana Pérez"), "Debe repetir lo que el usuario buscó.");
    }

    @Test
    public void testPideQueSeRegistre() {
        String mensaje = FamiliaresController.mensajeSinResultados("1144000000");
        assertTrue(mensaje.contains("se registre"), "Debe sugerir que el familiar se registre.");
    }

    @Test
    public void testRecortaBusquedasMuyLargas() {
        String larga = "a".repeat(200);
        String mensaje = FamiliaresController.mensajeSinResultados(larga);
        assertFalse(mensaje.contains(larga), "Un texto muy largo no debe salir completo en el mensaje.");
        assertTrue(mensaje.contains("..."));
    }
}
