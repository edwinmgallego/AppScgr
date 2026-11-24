package utils;

import config.ConexionDB;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author oriana - Equipo 1 - Autenticación y Red Familiar
 */
public final class ActualizadorBD {

    private ActualizadorBD() {
    }

    /**
     * Devuelve true si la base queda lista (ya estaba actualizada o se
     * actualizó ahora).
     */
    public static boolean actualizar() {
        Connection con = ConexionDB.getInstance().getConnection();
        if (con == null) {
            return false;
        }
        try (Statement st = con.createStatement()) {
            if (!existeColumna(con, "familiares", "familiar_usuario_id")) {
                st.executeUpdate("ALTER TABLE familiares "
                        + "ADD COLUMN familiar_usuario_id INT(11) NULL AFTER usuario_id, "
                        + "ADD UNIQUE KEY uk_vinculo_familiar (usuario_id, familiar_usuario_id), "
                        + "ADD CONSTRAINT fk_familiar_usuario "
                        + "FOREIGN KEY (familiar_usuario_id) REFERENCES usuarios (id) ON DELETE CASCADE");
                System.out.println("Base de datos actualizada: se agregó familiares.familiar_usuario_id");
            }
            if (!existeColumna(con, "familiares", "es_principal")) {
                st.executeUpdate("ALTER TABLE familiares ADD COLUMN es_principal TINYINT(1) NOT NULL DEFAULT 0");
                System.out.println("Base de datos actualizada: se agregó familiares.es_principal");
            }
            // Grupo 9 (RF-13): origen del reporte para diferenciar información oficial de la ciudadana.
            if (!existeColumna(con, "reportes_incidentes", "origen_reporte")) {
                st.executeUpdate("ALTER TABLE reportes_incidentes "
                        + "ADD COLUMN origen_reporte VARCHAR(20) NOT NULL DEFAULT 'CIUDADANO'");
                System.out.println("Base de datos actualizada: se agregó reportes_incidentes.origen_reporte");
            }
            return true;
        } catch (SQLException e) {
            System.err.println("No se pudo actualizar la base de datos: " + e.getMessage());
            return false;
        }
    }

    // Pregunta a MySQL si la tabla tiene esa columna
    private static boolean existeColumna(Connection con, String tabla, String columna) throws SQLException {
        DatabaseMetaData info = con.getMetaData();
        try (ResultSet rs = info.getColumns(con.getCatalog(), null, tabla, columna)) {
            return rs.next(); // true si encontró la columna
        }
    }

}