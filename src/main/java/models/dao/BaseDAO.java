package models.dao;

import config.ConexionDB;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

/**
 * Clase abstracta con lo que comparten los DAO del módulo: obtener la conexión compartida y
 * crear la tabla propia una sola vez por ejecución de la app.
 *
 * Cada DAO concreto sobrescribe (@Override) los dos métodos abstractos para decir cuál es su
 * tabla y cómo se crea (patrón "método plantilla": asegurarTabla es común, los datos son de cada hijo).
 */
public abstract class BaseDAO {

    // Tablas ya verificadas en esta ejecución (compartido por todos los DAO)
    private static final Set<String> tablasVerificadas = new HashSet<>();

    /** Nombre de la tabla que administra el DAO. */
    protected abstract String getNombreTabla();

    /** Sentencia CREATE TABLE IF NOT EXISTS de esa tabla. */
    protected abstract String getSqlCrearTabla();

    // Misma idea que FamiliarDAO: la conexión es compartida, así que NO se cierra aquí
    protected Connection conexion() throws SQLException {
        Connection conn = ConexionDB.getInstance().getConnection();
        if (conn == null) {
            throw new SQLException("No hay conexión con la base de datos.");
        }
        return conn;
    }

    protected void asegurarTabla(Connection conn) throws SQLException {
        synchronized (tablasVerificadas) {
            if (tablasVerificadas.contains(getNombreTabla())) {
                return;
            }
            try (Statement st = conn.createStatement()) {
                st.executeUpdate(getSqlCrearTabla());
            }
            tablasVerificadas.add(getNombreTabla());
        }
    }
}
