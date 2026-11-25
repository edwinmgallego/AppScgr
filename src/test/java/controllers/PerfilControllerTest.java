package controllers;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas de las validaciones del perfil (no necesitan base de datos).
 *
 * @author
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

    @Test
    public void testPasswordValida() {
        assertNull(PerfilController.validarPassword("Vieja123!", "Nueva456@", "Nueva456@"));
    }

    @Test
    public void testPasswordActualVacia() {
        assertNotNull(PerfilController.validarPassword("", "Nueva456@", "Nueva456@"));
    }

    @Test
    public void testPasswordNuevaDebil() {
        assertNotNull(PerfilController.validarPassword("Vieja123!", "abc", "abc"), "Muy corta.");
        assertNotNull(PerfilController.validarPassword("Vieja123!", "nueva456@", "nueva456@"), "Sin mayúscula.");
        assertNotNull(PerfilController.validarPassword("Vieja123!", "Nueva4567", "Nueva4567"), "Sin carácter especial.");
    }

    @Test
    public void testPasswordNoCoincide() {
        assertNotNull(PerfilController.validarPassword("Vieja123!", "Nueva456@", "Otra456@"));
    }

    @Test
    public void testPasswordIgualALaActual() {
        assertNotNull(PerfilController.validarPassword("Vieja123!", "Vieja123!", "Vieja123!"));
    }
}
