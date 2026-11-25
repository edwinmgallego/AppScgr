/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models.dao;

/**
 *
 * @author juans
 */

import config.ConexionDB;
import models.daños.DañoEstructural;
import models.daños.DañoGenerador;
import models.daños.FallaTanque;
import models.daños.Grieta;
import models.daños.NivelRiesgo;
import models.daños.TipoDaño;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
 
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
 
import static org.junit.jupiter.api.Assertions.*;
 
public class DañoEstructuralDAOTest {
 
    private static final String BARRIO = "Barrio JUnit Equipo6";
    private final DañoEstructuralDAO dao = new DañoEstructuralDAO();
    private final List<Integer> idsCreados = new ArrayList<>();
 
    @AfterEach
    public void limpiar() throws SQLException {
        for (int id : idsCreados) {
            try (PreparedStatement ps = ConexionDB.getInstance().getConnection()
                    .prepareStatement("DELETE FROM reportes_incidentes WHERE id = ?")) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }
        }
    }
 
    private Grieta grieta() {
        return new Grieta(1, "Cra 4 # 12-40", BARRIO, 3.4473, -76.539, "Prueba JUnit", "Columna", 4.5, 80);
    }
 
    private DañoGenerador generador() {
        return new DañoGenerador(1, "Cl 70 # 2A-30", BARRIO, 3.4764, -76.4918, "Prueba JUnit", "No enciende", 60, false);
    }
 
    private int guardar(DañoEstructural d) {
        int id = dao.insertar(d);
        if (id > 0) idsCreados.add(id);
        return id;
    }
 
    // mete un reporte como si fuera de otro equipo
    private int insertarFilaAjena(String tipo, String descripcion) throws SQLException {
        String sql = "INSERT INTO reportes_incidentes (usuario_id, tipo_incidente, descripcion, latitud, longitud) VALUES (1, ?, ?, 3.45, -76.53)";
        try (PreparedStatement ps = ConexionDB.getInstance().getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, tipo);
            ps.setString(2, descripcion);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            rs.next();
            idsCreados.add(rs.getInt(1));
            return rs.getInt(1);
        }
    }
 
    @Test
    public void testInsertarDevuelveId() {
        Grieta g = grieta();
        int id = guardar(g);
        assertTrue(id > 0);
        assertEquals(id, g.getId());
    }
 
    @Test
    public void testInsertarConUsuarioInexistenteFalla() {
        Grieta g = grieta();
        g.setUsuarioId(999999); // este usuario no existe
        assertEquals(-1, dao.insertar(g));
    }
 
    @Test
    public void testBuscarPorIdDevuelveLaClaseCorrecta() {
        int idGrieta = guardar(grieta());
        int idTanque = guardar(new FallaTanque(1, "Av 6N", BARRIO, 3.46, -76.53, "", "Fuga", 2000, true));
        Grieta leida = (Grieta) dao.buscarPorId(idGrieta);
        assertEquals("Columna", leida.getElemento());
        assertEquals(4.5, leida.getAnchoMm());
        assertEquals(BARRIO, leida.getBarrio());
        assertInstanceOf(FallaTanque.class, dao.buscarPorId(idTanque));
    }
 
    @Test
    public void testBuscarPorIdInexistenteDevuelveNull() {
        assertNull(dao.buscarPorId(99999999));
    }
 
    @Test
    public void testListarConSobrecargas() {
        int idGrieta = guardar(grieta());
        int idGenerador = guardar(generador());
        assertTrue(dao.listar().stream().anyMatch(d -> d.getId() == idGrieta));
        assertTrue(dao.listar(TipoDaño.GRIETA).stream().allMatch(d -> d instanceof Grieta));
        assertEquals(2, dao.listar(BARRIO.toUpperCase()).size());
        // la grieta es CRITICA y el generador BAJO
        assertTrue(dao.listar(NivelRiesgo.ALTO).stream().anyMatch(d -> d.getId() == idGrieta));
        assertTrue(dao.listar(NivelRiesgo.ALTO).stream().noneMatch(d -> d.getId() == idGenerador));
    }
 
    @Test
    public void testNoLeeReportesDeOtrosEquipos() throws SQLException {
        int idVia = insertarFilaAjena("VIA_CERRADA", "Prueba JUnit");
        int idLibre = insertarFilaAjena("DANO_ESTRUCTURAL", "Prueba JUnit: texto libre");
        assertNull(dao.buscarPorId(idVia));
        assertNull(dao.buscarPorId(idLibre));
    }
 
    @Test
    public void testActualizar() {
        int id = guardar(grieta());
        Grieta g = (Grieta) dao.buscarPorId(id);
        g.setDireccion("Nueva dirección");
        g.setAnchoMm(0.8);
        assertTrue(dao.actualizar(g));
        Grieta leida = (Grieta) dao.buscarPorId(id);
        assertEquals("Nueva dirección", leida.getDireccion());
        assertEquals(0.8, leida.getAnchoMm());
    }
 
    @Test
    public void testNoSePuedeCambiarElTipo() {
        int id = guardar(grieta());
        DañoGenerador otro = generador();
        otro.setId(id);
        assertFalse(dao.actualizar(otro));
        assertInstanceOf(Grieta.class, dao.buscarPorId(id));
    }
 
    @Test
    public void testEliminar() {
        int id = guardar(grieta());
        assertTrue(dao.eliminar(id));
        assertNull(dao.buscarPorId(id));
        assertFalse(dao.eliminar(99999999));
    }
 
    @Test
    public void testNoBorraReportesDeOtrosEquipos() throws SQLException {
        int idVia = insertarFilaAjena("VIA_CERRADA", "Prueba JUnit");
        assertFalse(dao.eliminar(idVia));
    }
}
