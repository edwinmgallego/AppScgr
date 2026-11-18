package models.dao;

import config.ConexionDB;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;


public abstract class BaseDAO {

   
    private static final Set<String> tablasVerificadas = new HashSet<>();

    protected abstract String getNombreTabla();

    protected abstract String getSqlCrearTabla();

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
