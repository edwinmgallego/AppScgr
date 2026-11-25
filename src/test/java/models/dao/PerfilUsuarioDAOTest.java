package models.dao;

import models.Usuario;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas de UsuarioDAO.actualizarPerfil. Crea un usuario temporal y lo borra al final.
 * Necesitan XAMPP encendido.
 *
 * @author (escribe aquí tu nombre)
 */
public class PerfilUsuarioDAOTest {

    private UsuarioDAO usuarioDAO;
    private Usuario usuarioPrueba;

    @BeforeEach
    public void setUp() {
        usuarioDAO = new UsuarioDAO();
        String documento = "E" + System.nanoTime() % 1_000_000_000L;
        usuarioPrueba = new Usuario(0, documento, "Nombre Original", "3000000000");
        assertTrue(usuarioDAO.registrarUsuario(usuarioPrueba, "secreta123"));
    }

    @AfterEach
    public void tearDown() {
        usuarioDAO.eliminarUsuario(usuarioPrueba.getId());
    }

    @Test
    public void testActualizarNombreYTelefono() {
        assertTrue(usuarioDAO.actualizarPerfil(usuarioPrueba.getId(), "Nombre Nuevo", "3119999999"));

        Usuario guardado = usuarioDAO.obtenerPorId(usuarioPrueba.getId());
        assertEquals("Nombre Nuevo", guardado.getNombreCompleto());
        assertEquals("3119999999", guardado.getTelefono());
        assertEquals(usuarioPrueba.getDocumento(), guardado.getDocumento(), "El documento no cambia.");
    }

    @Test
    public void testQuitarTelefono() {
        assertTrue(usuarioDAO.actualizarPerfil(usuarioPrueba.getId(), "Nombre Original", null));
        assertNull(usuarioDAO.obtenerPorId(usuarioPrueba.getId()).getTelefono());
    }

    @Test
    public void testLaContrasenaSigueFuncionando() {
        usuarioDAO.actualizarPerfil(usuarioPrueba.getId(), "Nombre Nuevo", "3119999999");
        assertNotNull(usuarioDAO.autenticar(usuarioPrueba.getDocumento(), "secreta123"),
                "Editar el perfil no debe tocar la contraseña.");
    }

    @Test
    public void testUsuarioQueNoExiste() {
        assertFalse(usuarioDAO.actualizarPerfil(-1, "Nadie", null));
    }
}
