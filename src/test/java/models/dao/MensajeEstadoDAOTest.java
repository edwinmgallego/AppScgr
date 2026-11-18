package models.dao;

import models.EstadoSupervivencia;
import models.Familiar;
import models.MensajeEstado;
import models.Usuario;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas del DAO de estados de supervivencia.
 * Requieren XAMPP encendido y la base emergencias_db con las tablas del proyecto.
 * Cada prueba crea sus propios usuarios y los borra al terminar (no dependen de datos existentes).
 */
public class MensajeEstadoDAOTest {

    private MensajeEstadoDAO dao;
    private UsuarioDAO usuarioDAO;
    private Usuario usuarioPrueba;

    @BeforeEach
    public void setUp() {
        dao = new MensajeEstadoDAO();
        usuarioDAO = new UsuarioDAO();
        usuarioPrueba = new Usuario(0, "T2" + System.currentTimeMillis(), "Usuario Prueba Estados", "3000000000");
        assertTrue(usuarioDAO.registrarUsuario(usuarioPrueba, "clave123"),
                "No se pudo crear el usuario de prueba (¿está encendido MySQL?).");
    }

    @AfterEach
    public void tearDown() {
        // ON DELETE CASCADE borra también los estados de este usuario
        usuarioDAO.eliminarUsuario(usuarioPrueba.getId());
    }

    private MensajeEstado mensaje(EstadoSupervivencia estado, Double lat, Double lon) {
        return new MensajeEstado(usuarioPrueba.getId(), usuarioPrueba.getNombreCompleto(),
                usuarioPrueba.getTelefono(), estado, lat, lon);
    }

    @Test
    public void testRegistrarEstado() {
        assertTrue(dao.registrarEstado(mensaje(EstadoSupervivencia.ESTOY_BIEN, null, null)),
                "El estado debería registrarse correctamente en la base de datos.");
    }
}
