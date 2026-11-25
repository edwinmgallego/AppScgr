package utils;

import config.ConexionDB;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas de la parte del Equipo 3 en ActualizadorBD (requieren XAMPP encendido).
 *
 * @author equipo3/samuellopez
 */
public class ActualizadorBDTest {

    private int contarEstaciones(Connection con) throws SQLException {
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM estaciones_mio")) {
            rs.next();
            return rs.getInt(1);
        }
    }

    @Test
    public void testCreaLaColumnaEstadoComoTexto() throws SQLException {
        ActualizadorBD.actualizar();
        String sql = "SELECT DATA_TYPE FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() "
                + "AND TABLE_NAME = 'reportes_incidentes' AND COLUMN_NAME = 'estado'";
        try (Statement st = ConexionDB.getInstance().getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            assertTrue(rs.next(), "Debe existir la columna reportes_incidentes.estado");
            assertEquals("varchar", rs.getString("DATA_TYPE").toLowerCase(),
                    "estado debe ser texto y no ENUM: el Equipo 5 guarda ACTIVO/RESUELTO");
        }
    }

    @Test
    public void testHayEstacionesDelMioDespuesDeActualizar() throws SQLException {
        ActualizadorBD.actualizar();
        assertTrue(contarEstaciones(ConexionDB.getInstance().getConnection()) > 0,
                "Después de actualizar debe haber estaciones del MIO para mostrar en el mapa");
    }

    @Test
    public void testActualizarDosVecesNoDuplicaEstaciones() throws SQLException {
        ActualizadorBD.actualizar();
        Connection con = ConexionDB.getInstance().getConnection();
        int antes = contarEstaciones(con);
        ActualizadorBD.actualizar();
        assertEquals(antes, contarEstaciones(con), "Las estaciones iniciales no se deben insertar otra vez");
    }
}
