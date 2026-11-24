package models.dao;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import models.EstacionMio;

public class EstacionMioDAOTest {

    private EstacionMioDAO dao;

    @BeforeEach
    public void setUp() {
        dao = new EstacionMioDAO();
    }

    @Test
    public void testCrudCompletoEstacionMio() {
        EstacionMio nueva = new EstacionMio("Estación Prueba JUnit", 3.4516, -76.5320, true);

        assertTrue(dao.insertar(nueva));
        assertTrue(nueva.getId() > 0);

        EstacionMio encontrada = dao.buscarPorId(nueva.getId());
        assertNotNull(encontrada);
        assertEquals("Estación Prueba JUnit", encontrada.getNombre());
        assertTrue(encontrada.isOperativa());

        assertTrue(dao.actualizarEstado(nueva.getId(), false));
        EstacionMio actualizada = dao.buscarPorId(nueva.getId());
        assertNotNull(actualizada);
        assertFalse(actualizada.isOperativa());

        List<EstacionMio> lista = dao.listarTodas();
        assertFalse(lista.isEmpty());

        assertTrue(dao.eliminar(nueva.getId()));
        assertNull(dao.buscarPorId(nueva.getId()));
    }
}