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
        assertNull(RegistroController.validar("1144000000", "Ana Pérez", "3001234567", "secreta", "secreta"));
        assertNull(RegistroController.validar("1144000000", "Ana Pérez", "", "secreta", "secreta"), "El teléfono es opcional.");
    }

    @Test
    public void testDocumentoInvalido() {
        assertNotNull(RegistroController.validar("12ab", "Ana Pérez", "", "secreta", "secreta"));
    }

    @Test
    public void testPasswordCortaONoCoincide() {
        assertNotNull(RegistroController.validar("1144000000", "Ana Pérez", "", "123", "123"));
        assertNotNull(RegistroController.validar("1144000000", "Ana Pérez", "", "secreta", "otra123"));
    }

    @Test
    public void testTelefonoInvalido() {
        assertNotNull(RegistroController.validar("1144000000", "Ana Pérez", "abc", "secreta", "secreta"));
    }
}
