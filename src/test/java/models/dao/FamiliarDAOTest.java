package models.dao;

import models.Familiar;
import models.Usuario;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas de FamiliarDAO. Crea dos usuarios temporales (titular y familiar)
 * y los borra al final; los vínculos se borran solos por ON DELETE CASCADE.
 *
 * @author Equipo 1 - Autenticación y Red Familiar
 */
public class FamiliarDAOTest {

    private UsuarioDAO usuarioDAO;
    private FamiliarDAO familiarDAO;
    private Usuario titular;
    private Usuario pariente;

    @BeforeEach
    public void setUp() {
        usuarioDAO = new UsuarioDAO();
        familiarDAO = new FamiliarDAO();
        long sufijo = System.nanoTime() % 100_000_000L;
        titular = new Usuario(0, "A" + sufijo, "Titular JUnit", "3001111111");
        pariente = new Usuario(0, "B" + sufijo, "Pariente JUnit", "3002222222");
        assertTrue(usuarioDAO.registrarUsuario(titular, "clave123"));
        assertTrue(usuarioDAO.registrarUsuario(pariente, "clave123"));
    }

    @AfterEach
    public void tearDown() {
        usuarioDAO.eliminarUsuario(titular.getId());
        usuarioDAO.eliminarUsuario(pariente.getId());
    }

    @Test
    public void testVincularYListar() {
        assertTrue(familiarDAO.vincularFamiliar(titular.getId(), pariente));

        List<Familiar> familiares = familiarDAO.obtenerFamiliares(titular.getId());
        assertEquals(1, familiares.size());
        Familiar f = familiares.get(0);
        assertEquals(pariente.getId(), f.getFamiliarUsuarioId());
        assertEquals("Pariente JUnit", f.getNombreFamiliar());
        assertEquals("DESCONOCIDO", f.getEstadoEmergencia());
    }

    @Test
    public void testNoVinculaDosVecesNiASiMismo() {
        assertTrue(familiarDAO.vincularFamiliar(titular.getId(), pariente));
        assertFalse(familiarDAO.vincularFamiliar(titular.getId(), pariente), "No debe duplicar el vínculo.");
        assertFalse(familiarDAO.vincularFamiliar(titular.getId(), titular), "No debe vincularse a sí mismo.");
    }

    @Test
    public void testActualizarEstadoYDesvincular() {
        familiarDAO.vincularFamiliar(titular.getId(), pariente);
        int idVinculo = familiarDAO.obtenerFamiliares(titular.getId()).get(0).getId();

        assertTrue(familiarDAO.actualizarEstado(idVinculo, titular.getId(), "SEGURO"));
        assertEquals("SEGURO", familiarDAO.obtenerFamiliares(titular.getId()).get(0).getEstadoEmergencia());

        assertFalse(familiarDAO.desvincularFamiliar(idVinculo, pariente.getId()),
                "Otro usuario no puede borrar un vínculo ajeno.");
        assertTrue(familiarDAO.desvincularFamiliar(idVinculo, titular.getId()));
        assertTrue(familiarDAO.obtenerFamiliares(titular.getId()).isEmpty());
    }
}
