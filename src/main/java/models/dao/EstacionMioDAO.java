package models.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import models.EstacionMio;

/**
 * Acceso a datos para la tabla estaciones_mio en MySQL.
 *
 * @author equipo3
 */
public class EstacionMioDAO extends BaseDAO {

    @Override
    protected String getNombreTabla() {
        return "estaciones_mio";
    }

    @Override
    protected String getSqlCrearTabla() {
        return "CREATE TABLE IF NOT EXISTS estaciones_mio ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "nombre VARCHAR(100) NOT NULL, "
                + "latitud DOUBLE NOT NULL, "
                + "longitud DOUBLE NOT NULL, "
                + "operativa BOOLEAN NOT NULL DEFAULT TRUE"
                + ")";
    }

    public boolean insertar(EstacionMio estacion) {
        String sql = "INSERT INTO estaciones_mio (nombre, latitud, longitud, operativa) VALUES (?, ?, ?, ?)";
        try {
            Connection conn = conexion();
            asegurarTabla(conn);
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, estacion.getNombre());
                ps.setDouble(2, estacion.getLatitud());
                ps.setDouble(3, estacion.getLongitud());
                ps.setBoolean(4, estacion.isOperativa());

                if (ps.executeUpdate() > 0) {
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next()) {
                            estacion.setId(rs.getInt(1));
                        }
                    }
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al insertar estación MIO: " + e.getMessage());
        }
        return false;
    }

    public List<EstacionMio> listarTodas() {
        List<EstacionMio> lista = new ArrayList<>();
        String sql = "SELECT id, nombre, latitud, longitud, operativa FROM estaciones_mio";
        try {
            Connection conn = conexion();
            asegurarTabla(conn);
            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al listar estaciones MIO: " + e.getMessage());
        }
        return lista;
    }

    public EstacionMio buscarPorId(int id) {
        String sql = "SELECT id, nombre, latitud, longitud, operativa FROM estaciones_mio WHERE id = ?";
        try {
            Connection conn = conexion();
            asegurarTabla(conn);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return mapear(rs);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar estación MIO: " + e.getMessage());
        }
        return null;
    }

    public boolean actualizarEstado(int id, boolean operativa) {
        String sql = "UPDATE estaciones_mio SET operativa = ? WHERE id = ?";
        try {
            Connection conn = conexion();
            asegurarTabla(conn);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setBoolean(1, operativa);
                ps.setInt(2, id);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error al actualizar estado de estación MIO: " + e.getMessage());
        }
        return false;
    }

    public boolean eliminar(int id) {
        String sql = "DELETE FROM estaciones_mio WHERE id = ?";
        try {
            Connection conn = conexion();
            asegurarTabla(conn);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, id);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error al eliminar estación MIO: " + e.getMessage());
        }
        return false;
    }

    private EstacionMio mapear(ResultSet rs) throws SQLException {
        return new EstacionMio(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getDouble("latitud"),
                rs.getDouble("longitud"),
                rs.getBoolean("operativa")
        );
    }
}