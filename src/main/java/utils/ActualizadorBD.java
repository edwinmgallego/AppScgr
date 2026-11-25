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
        boolean listo = true;
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
        } catch (SQLException e) {
            System.err.println("No se pudo actualizar la base de datos: " + e.getMessage());
            listo = false;
        }
        // Equipo 3 va aparte: un error en los cambios anteriores no debe impedir cargar el mapa
        return actualizarEquipo3(con) && listo;
    }

    /**
     * Equipo 3 (RF-03): columna 'estado' de reportes_incidentes y tabla de estaciones
     * del MIO con sus datos iniciales, para que no haya que importar ningún script.
     * 'estado' es VARCHAR y no ENUM porque también la usa el Equipo 5 (ACTIVO / RESUELTO).
     */
    private static boolean actualizarEquipo3(Connection con) {
        try (Statement st = con.createStatement()) {
            if (!existeColumna(con, "reportes_incidentes", "estado")) {
                st.executeUpdate("ALTER TABLE reportes_incidentes "
                        + "ADD COLUMN estado VARCHAR(20) NOT NULL DEFAULT 'CERRADA'");
                System.out.println("Base de datos actualizada: se agregó reportes_incidentes.estado");
            } else if (esEnum(st, "reportes_incidentes", "estado")) {
                // Versiones anteriores del script la creaban como ENUM: se pasa a texto sin perder datos
                st.executeUpdate("ALTER TABLE reportes_incidentes "
                        + "MODIFY COLUMN estado VARCHAR(20) NOT NULL DEFAULT 'CERRADA'");
                System.out.println("Base de datos actualizada: reportes_incidentes.estado pasó de ENUM a VARCHAR");
            }

            // Misma definición que EstacionMioDAO.getSqlCrearTabla()
            st.executeUpdate("CREATE TABLE IF NOT EXISTS estaciones_mio ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY, "
                    + "nombre VARCHAR(100) NOT NULL, "
                    + "latitud DOUBLE NOT NULL, "
                    + "longitud DOUBLE NOT NULL, "
                    + "operativa BOOLEAN NOT NULL DEFAULT TRUE)");

            // Las estaciones iniciales solo se insertan si la tabla está vacía (no se duplican)
            boolean vacia;
            try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM estaciones_mio")) {
                vacia = rs.next() && rs.getInt(1) == 0;
            }
            if (vacia) {
                st.executeUpdate("INSERT INTO estaciones_mio (nombre, latitud, longitud, operativa) VALUES "
                        + "('Estación Universidades', 3.3688, -76.5290, TRUE), "
                        + "('Estación Capri', 3.3958, -76.5449, TRUE), "
                        + "('Estación Unidad Deportiva', 3.4126, -76.5477, TRUE), "
                        + "('Estación Estadio', 3.4285, -76.5401, FALSE), "
                        + "('Estación Torre de Cali', 3.4557, -76.5338, TRUE)");
                System.out.println("Base de datos actualizada: se insertaron las estaciones iniciales del MIO");
            }
            return true;
        } catch (SQLException e) {
            System.err.println("No se pudo preparar la base de datos del Equipo 3: " + e.getMessage());
            return false;
        }
    }

    // Pregunta a MySQL si la columna es de tipo ENUM
    private static boolean esEnum(Statement st, String tabla, String columna) throws SQLException {
        String sql = "SELECT DATA_TYPE FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() "
                + "AND TABLE_NAME = '" + tabla + "' AND COLUMN_NAME = '" + columna + "'";
        try (ResultSet rs = st.executeQuery(sql)) {
            return rs.next() && "enum".equalsIgnoreCase(rs.getString("DATA_TYPE"));
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