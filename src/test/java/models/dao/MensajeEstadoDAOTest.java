package models.dao;

import models.EstadoSupervivencia;
import models.Familiar;
import models.MensajeEstado;
import models.Usuario;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.ActualizadorBD;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MensajeEstadoDAOTest {

    private MensajeEstadoDAO dao;
    private UsuarioDAO usuarioDAO;
    private Usuario usuarioPrueba;

    @BeforeEach
    public void setUp() {
        ActualizadorBD.actualizar();
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
    public void testRegistrarEstadoGuardaLaUltimaUbicacion() {
        assertTrue(dao.registrarEstado(mensaje(EstadoSupervivencia.NECESITO_AYUDA, 3.4516, -76.5320)));

        double[] ubicacion = dao.obtenerUltimaUbicacion(usuarioPrueba.getId());

        assertNotNull(ubicacion, "Debería haberse guardado la ubicación del usuario.");
        assertEquals(3.4516, ubicacion[0], 0.0000001);
        assertEquals(-76.5320, ubicacion[1], 0.0000001);
    }

    @Test
    public void testUnEstadoSinUbicacionNoBorraLaUltimaUbicacion() {
        assertTrue(dao.registrarEstado(mensaje(EstadoSupervivencia.NECESITO_AYUDA, 3.4516, -76.5320)));
        assertTrue(dao.registrarEstado(mensaje(EstadoSupervivencia.ESTOY_BIEN, null, null)));

        double[] ubicacion = dao.obtenerUltimaUbicacion(usuarioPrueba.getId());

        assertNotNull(ubicacion, "La ubicación anterior debería conservarse.");
        assertEquals(3.4516, ubicacion[0], 0.0000001);
    }

    @Test
    public void testUsuarioSinUbicacionDevuelveNull() {
        assertNull(dao.obtenerUltimaUbicacion(usuarioPrueba.getId()));
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
