/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models.dao;

/**
 *
 * @author inged
 */


import models.Incidente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class IncidenteDAOTest {

    private IncidenteDAO incidenteDAO;

    // @BeforeEach se ejecuta automáticamente antes de cada @Test
    // Garantiza que tengamos una instancia fresca del DAO para probar
    @BeforeEach
    public void setUp() {
        incidenteDAO = new IncidenteDAO();
    }

    // @Test le indica a JUnit que este método es una prueba unitaria
    @Test
    public void testRegistrarIncidente() {
        // 1. Preparar los datos de prueba (Arrange)
        // Usamos ID 0 porque MySQL lo auto-incrementará
        Incidente incidentePrueba = new Incidente(0, "VIA_CERRADA", 3.4516, -76.5320, "Prueba unitaria JUnit");
        int idUsuarioPrueba = 1; // Asumiendo que el usuario con ID 1 existe en la BD

        // 2. Ejecutar la acción a probar (Act)
        boolean resultado = incidenteDAO.registrarIncidente(incidentePrueba, idUsuarioPrueba);

        // 3. Validar el resultado (Assert)
        // assertTrue verifica que el método devolvió 'true' (inserción exitosa)
        assertTrue(resultado, "El incidente debería registrarse correctamente en la base de datos.");
    }

    @Test
    public void testObtenerTodosLosIncidentes() {
        // 1. Ejecutar la acción (Act)
        List<Incidente> lista = incidenteDAO.obtenerTodosLosIncidentes();

        // 2. Validar el resultado (Assert)
        // Comprobamos que la lista no sea nula (evita NullPointerException)
        assertNotNull(lista, "La lista de incidentes no debería ser nula.");
        
        // Comprobamos que al menos traiga el registro que insertamos en la prueba anterior
        assertTrue(lista.size() > 0, "Debería haber al menos un incidente registrado en la base de datos.");
    }

    // ===== Equipo 3: pruebas de actualizar y eliminar reportes =====

    /** Registra un reporte de prueba y lo busca en la BD para conocer su id. */
    private Incidente crearIncidenteDePrueba(String descripcion) {
        assertTrue(incidenteDAO.registrarIncidente(
                new Incidente(0, "VIA_CERRADA", 3.4516, -76.5320, descripcion), 1));
        for (Incidente inc : incidenteDAO.obtenerTodosLosIncidentes()) {
            if (descripcion.equals(inc.getDescripcion())) {
                return inc;
            }
        }
        fail("No se encontró el reporte de prueba recién registrado.");
        return null;
    }

    /** Busca un reporte por su id, o devuelve null si ya no existe. */
    private Incidente buscarPorId(int id) {
        for (Incidente inc : incidenteDAO.obtenerTodosLosIncidentes()) {
            if (inc.getId() == id) {
                return inc;
            }
        }
        return null;
    }

    @Test
    public void testActualizarIncidente() {
        Incidente original = crearIncidenteDePrueba("Prueba actualizar " + System.nanoTime());
        Incidente cambios = new Incidente(original.getId(), "DERRUMBE", 3.45, -76.53, "Descripción actualizada");

        assertTrue(incidenteDAO.actualizarIncidente(cambios), "El reporte debería actualizarse.");

        Incidente guardado = buscarPorId(original.getId());
        assertNotNull(guardado, "El reporte actualizado debería seguir existiendo.");
        assertEquals("DERRUMBE", guardado.getTipo());
        assertEquals("Descripción actualizada", guardado.getDescripcion());

        incidenteDAO.eliminarIncidente(original.getId()); // Limpiar la BD
    }

    @Test
    public void testEliminarIncidente() {
        Incidente inc = crearIncidenteDePrueba("Prueba eliminar " + System.nanoTime());

        assertTrue(incidenteDAO.eliminarIncidente(inc.getId()), "El reporte debería eliminarse.");
        assertNull(buscarPorId(inc.getId()), "El reporte ya no debería existir en la BD.");
    }

    @Test
    public void testActualizarIncidenteInexistente() {
        // Un id que no existe: no debería cambiar ninguna fila
        Incidente fantasma = new Incidente(-1, "VIA_CERRADA", 0, 0, "No existe");
        assertFalse(incidenteDAO.actualizarIncidente(fantasma));
    }

    @Test
    public void testEliminarIncidenteInexistente() {
        assertFalse(incidenteDAO.eliminarIncidente(-1));
    }
}
