package controllers;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas de las validaciones del registro (no necesitan base de datos).
 *
 * @author Equipo 1 - Autenticación y Red Familiar
 */
public class RegistroControllerTest {

    @Test
    public void testDatosValidos() {
        assertNull(RegistroController.validar("1144000000", "Ana Pérez", "3001234567", "Secreta#26", "Secreta#26"));
        assertNull(RegistroController.validar("1144000000", "Ana Pérez", "", "Secreta#26", "Secreta#26"), "El teléfono es opcional.");
    }

    @Test
    public void testDocumentoInvalido() {
        assertNotNull(RegistroController.validar("12ab", "Ana Pérez", "", "Secreta#26", "Secreta#26"));
    }

    @Test
    public void testPasswordCortaONoCoincide() {
        assertNotNull(RegistroController.validar("1144000000", "Ana Pérez", "", "123", "123"));
        assertNotNull(RegistroController.validar("1144000000", "Ana Pérez", "", "Secreta#26", "Otra#12345"));
    }

    @Test
    public void testPasswordInsegura() {
        // Cada una incumple UNA regla: sin mayúscula, sin minúscula, sin especial y muy corta
        assertNotNull(RegistroController.validar("1144000000", "Ana Pérez", "", "secreta#26", "secreta#26"));
        assertNotNull(RegistroController.validar("1144000000", "Ana Pérez", "", "SECRETA#26", "SECRETA#26"));
        assertNotNull(RegistroController.validar("1144000000", "Ana Pérez", "", "Secreta126", "Secreta126"));
        assertNotNull(RegistroController.validar("1144000000", "Ana Pérez", "", "Se#1", "Se#1"));
    }

    @Test
    public void testTelefonoInvalido() {
        assertNotNull(RegistroController.validar("1144000000", "Ana Pérez", "abc", "Secreta#26", "Secreta#26"));
    }
}
