package controllers;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas de las validaciones del perfil (no necesitan base de datos).
 *
 * @author (escribe aquí tu nombre)
 */
public class PerfilControllerTest {

    @Test
    public void testDatosValidos() {
        assertNull(PerfilController.validar("Ana Pérez", "3001234567"));
        assertNull(PerfilController.validar("Ana Pérez", ""), "El teléfono es opcional.");
        assertNull(PerfilController.validar("Ana Pérez", "+57 300 123 4567"));
    }

    @Test
    public void testNombreMuyCorto() {
        assertNotNull(PerfilController.validar("An", "3001234567"));
    }

    @Test
    public void testNombreMuyLargo() {
        assertNotNull(PerfilController.validar("a".repeat(101), "3001234567"));
    }

    @Test
    public void testTelefonoInvalido() {
        assertNotNull(PerfilController.validar("Ana Pérez", "abc"));
        assertNotNull(PerfilController.validar("Ana Pérez", "123"), "Muy corto.");
    }
}
