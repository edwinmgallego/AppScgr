package models.dao;

import models.Familiar;
import models.Usuario;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas del contacto principal de emergencia.
 * Crea tres usuarios temporales y los borra al final (los vínculos se borran en cascada).
 * Necesitan XAMPP encendido y el script sql/contacto_principal.sql ejecutado.
 *
 * @author (escribe aquí tu nombre)
 */
public class ContactoPrincipalDAOTest {

    private UsuarioDAO usuarioDAO;
    private FamiliarDAO familiarDAO;
    private Usuario titular;
    private Usuario ana;
    private Usuario zoe;

    @BeforeEach
    public void setUp() {
        usuarioDAO = new UsuarioDAO();
        familiarDAO = new FamiliarDAO();
        long sufijo = System.nanoTime() % 100_000_000L;
        titular = new Usuario(0, "C" + sufijo, "Titular Principal", "3001111111");
        // "Ana" va antes que "Zoe" en orden alfabético: así se nota si el principal sale primero
        ana = new Usuario(0, "N" + sufijo, "Ana Principal", "3002222222");
        zoe = new Usuario(0, "Z" + sufijo, "Zoe Principal", "3003333333");
        assertTrue(usuarioDAO.registrarUsuario(titular, "clave123"));
        assertTrue(usuarioDAO.registrarUsuario(ana, "clave123"));
        assertTrue(usuarioDAO.registrarUsuario(zoe, "clave123"));
        assertTrue(familiarDAO.vincularFamiliar(titular.getId(), ana));
        assertTrue(familiarDAO.vincularFamiliar(titular.getId(), zoe));
    }

    @AfterEach
    public void tearDown() {
        usuarioDAO.eliminarUsuario(titular.getId());
        usuarioDAO.eliminarUsuario(ana.getId());
        usuarioDAO.eliminarUsuario(zoe.getId());
    }

    // Busca en la red del titular el id del vínculo con ese usuario
    private int idVinculoCon(Usuario familiar) {
        for (Familiar f : familiarDAO.obtenerFamiliares(titular.getId())) {
            if (f.getFamiliarUsuarioId() != null && f.getFamiliarUsuarioId() == familiar.getId()) {
                return f.getId();
            }
        }
        fail("No se encontró el vínculo");
        return -1;
    }

    @Test
    public void testSinPrincipalAlInicio() {
        assertEquals(0, familiarDAO.obtenerIdPrincipal(titular.getId()));
    }

    @Test
    public void testPrincipalSaleDePrimero() {
        int idZoe = idVinculoCon(zoe);
        assertTrue(familiarDAO.marcarPrincipal(idZoe, titular.getId()));

        assertEquals(idZoe, familiarDAO.obtenerIdPrincipal(titular.getId()));
        List<Familiar> lista = familiarDAO.obtenerFamiliares(titular.getId());
        assertEquals(idZoe, lista.get(0).getId(), "Zoe es la principal: debe salir antes que Ana.");
    }

    @Test
    public void testSoloPuedeHaberUnPrincipal() {
        int idAna = idVinculoCon(ana);
        int idZoe = idVinculoCon(zoe);
        assertTrue(familiarDAO.marcarPrincipal(idAna, titular.getId()));
        assertTrue(familiarDAO.marcarPrincipal(idZoe, titular.getId()));

        assertEquals(idZoe, familiarDAO.obtenerIdPrincipal(titular.getId()), "Al marcar a Zoe, Ana deja de ser principal.");
    }

    @Test
    public void testOtroUsuarioNoPuedeMarcar() {
        int idAna = idVinculoCon(ana);
        assertTrue(familiarDAO.marcarPrincipal(idAna, titular.getId()));

        assertFalse(familiarDAO.marcarPrincipal(idAna, zoe.getId()), "No puede marcar un vínculo ajeno.");
        assertEquals(idAna, familiarDAO.obtenerIdPrincipal(titular.getId()), "El intento fallido no debe cambiar nada.");
    }

    @Test
    public void testQuitarPrincipal() {
        familiarDAO.marcarPrincipal(idVinculoCon(ana), titular.getId());
        assertTrue(familiarDAO.quitarPrincipal(titular.getId()));
        assertEquals(0, familiarDAO.obtenerIdPrincipal(titular.getId()));
    }
}
