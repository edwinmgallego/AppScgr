package models.dao;

import models.Usuario;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas de UsuarioDAO. Cada prueba crea su propio usuario y lo borra al final,
 * así no deja basura en la base de datos ni depende del orden de ejecución.
 *
 * @author Equipo 1 - Autenticación y Red Familiar
 */
public class UsuarioDAOTest {

    private UsuarioDAO usuarioDAO;
    private Usuario usuarioPrueba;

    @BeforeEach
    public void setUp() {
        usuarioDAO = new UsuarioDAO();
        String documento = "T" + System.nanoTime() % 1_000_000_000L;
        usuarioPrueba = new Usuario(0, documento, "Prueba JUnit Usuario", "3000000000");
        assertTrue(usuarioDAO.registrarUsuario(usuarioPrueba, "secreta123"), "Debe registrar el usuario de prueba.");
    }

    @AfterEach
    public void tearDown() {
        if (usuarioPrueba.getId() > 0) {
            usuarioDAO.eliminarUsuario(usuarioPrueba.getId());
        }
    }

    @Test
    public void testRegistrarAsignaId() {
        assertTrue(usuarioPrueba.getId() > 0, "El registro debe asignar el id generado por MySQL.");
        assertTrue(usuarioDAO.existeDocumento(usuarioPrueba.getDocumento()));
    }

    @Test
    public void testNoPermiteDocumentoDuplicado() {
        Usuario duplicado = new Usuario(0, usuarioPrueba.getDocumento(), "Otro", null);
        assertFalse(usuarioDAO.registrarUsuario(duplicado, "otra123"), "No debe aceptar un documento repetido.");
    }

    @Test
    public void testAutenticarCorrecto() {
        Usuario logueado = usuarioDAO.autenticar(usuarioPrueba.getDocumento(), "secreta123");
        assertNotNull(logueado, "Con la contraseña correcta debe iniciar sesión.");
        assertEquals(usuarioPrueba.getId(), logueado.getId());
    }

    @Test
    public void testAutenticarPasswordIncorrecta() {
        assertNull(usuarioDAO.autenticar(usuarioPrueba.getDocumento(), "incorrecta"));
    }

    @Test
    public void testBuscarPorNombreExcluyeAlMismoUsuario() {
        List<Usuario> encontrados = usuarioDAO.buscarUsuarios("Prueba JUnit Usuario", 0);
        assertTrue(contieneUsuario(encontrados, usuarioPrueba.getId()));

        List<Usuario> sinMi = usuarioDAO.buscarUsuarios("Prueba JUnit Usuario", usuarioPrueba.getId());
        assertFalse(contieneUsuario(sinMi, usuarioPrueba.getId()),
                "El usuario no debe encontrarse a sí mismo.");
    }

    // Recorre la lista y devuelve true si encuentra un usuario con ese id
    private boolean contieneUsuario(List<Usuario> lista, int id) {
        for (Usuario u : lista) {
            if (u.getId() == id) {
                return true;
            }
        }
        return false;
    }
}
