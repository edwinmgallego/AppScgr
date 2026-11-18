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

    @Test
    public void testObtenerPorUsuarioTraeNombreYUbicacion() {
        assertTrue(dao.registrarEstado(mensaje(EstadoSupervivencia.NECESITO_AYUDA, 3.4516, -76.5320)));

        List<MensajeEstado> lista = dao.obtenerPorUsuario(usuarioPrueba.getId());

        assertEquals(1, lista.size());
        MensajeEstado m = lista.get(0);
        assertEquals(EstadoSupervivencia.NECESITO_AYUDA, m.getEstado());
        assertEquals("Usuario Prueba Estados", m.getNombreUsuario());
        assertTrue(m.tieneUbicacion());
        assertEquals(Double.valueOf(3.4516), m.getLatitud());
        assertTrue(m.isEnviado(), "Lo leído de la base de datos ya fue enviado.");
    }

    @Test
    public void testRegistrarEstadoSeReflejaEnLaRedFamiliar() {
        Usuario dueno = new Usuario(0, "T2D" + System.currentTimeMillis(), "Dueño Prueba", "3111111111");
        assertTrue(usuarioDAO.registrarUsuario(dueno, "clave123"));
        FamiliarDAO familiarDAO = new FamiliarDAO();
        try {

            assertTrue(familiarDAO.vincularFamiliar(dueno.getId(), usuarioPrueba), familiarDAO.getUltimoError());

            assertTrue(dao.registrarEstado(mensaje(EstadoSupervivencia.NECESITO_AYUDA, null, null)));

            List<Familiar> red = familiarDAO.obtenerFamiliares(dueno.getId());
            assertEquals(1, red.size());
            assertEquals("NECESITA AYUDA", red.get(0).getEstadoEmergencia());
            
        } finally {
            for (Familiar f : familiarDAO.obtenerFamiliares(dueno.getId())) {
                familiarDAO.desvincularFamiliar(f.getId(), dueno.getId());
            }
            usuarioDAO.eliminarUsuario(dueno.getId());
        }
    }
}
