package models.dao;

import models.ReporteServicioZona;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Persistencia del módulo Servicios por zona (RF-06).
 * Tabla base: estado_servicios_zona.
 *
 * Incluye la API del Grupo 9 (insertar/listar/buscarPorId/actualizar/eliminar)
 * y la API del Equipo 5 (registrarReporteServicio, obtenerServiciosPorBarrio, ...)
 * para que ServiciosController y sus pruebas sigan funcionando.
 *
 * @author Grupo 9 - Validación Comunitaria y Dashboard
 */
public class ServicioZonaDAO extends BaseDAO {

    @Override
    protected String getNombreTabla() {
        return "estado_servicios_zona";
    }

    @Override
    protected String getSqlCrearTabla() {
        return "CREATE TABLE IF NOT EXISTS estado_servicios_zona ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "usuario_id INT NOT NULL, "
                + "zona_barrio VARCHAR(100) NOT NULL, "
                + "servicio VARCHAR(30) NOT NULL, "
                + "estado VARCHAR(20) NOT NULL, "
                + "observaciones TEXT, "
                + "verificaciones INT DEFAULT 0, "
                + "fecha_actualizacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, "
                + "CONSTRAINT fk_sz_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id)"
                + ") ENGINE=InnoDB DEFAULT CHARSET=utf8";
    }

    // ===== API del Grupo 9 =====

    public int insertar(ReporteServicioZona r) {
        String sql = "INSERT INTO estado_servicios_zona (usuario_id, zona_barrio, servicio, estado, observaciones, verificaciones) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try {
            Connection conn = conexion();
            asegurarTabla(conn);
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, r.getUsuarioId());
                ps.setString(2, r.getZonaBarrio());
                ps.setString(3, r.getServicio());
                ps.setString(4, r.getEstado());
                ps.setString(5, r.getObservaciones());
                ps.setInt(6, r.getVerificaciones());
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        int id = rs.getInt(1);
                        r.setId(id);
                        return id;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error DAO al insertar servicio por zona: " + e.getMessage());
        }
        return -1;
    }

    public List<ReporteServicioZona> listar() {
        List<ReporteServicioZona> lista = new ArrayList<>();
        String sql = "SELECT id, usuario_id, zona_barrio, servicio, estado, observaciones, verificaciones, fecha_actualizacion "
                + "FROM estado_servicios_zona ORDER BY id DESC";
        try {
            Connection conn = conexion();
            asegurarTabla(conn);
            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ReporteServicioZona r = new ReporteServicioZona(
                            rs.getInt("usuario_id"),
                            rs.getString("zona_barrio"),
                            rs.getString("servicio"),
                            rs.getString("estado"),
                            rs.getString("observaciones"));
                    r.setId(rs.getInt("id"));
                    r.setVerificaciones(rs.getInt("verificaciones"));
                    java.sql.Timestamp ts = rs.getTimestamp("fecha_actualizacion");
                    if (ts != null) {
                        r.setFechaReporte(ts);
                    }
                    lista.add(r);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error DAO al listar servicios por zona: " + e.getMessage());
        }
        return lista;
    }

    public ReporteServicioZona buscarPorId(int id) {
        String sql = "SELECT id, usuario_id, zona_barrio, servicio, estado, observaciones, verificaciones, fecha_actualizacion "
                + "FROM estado_servicios_zona WHERE id = ?";
        try {
            Connection conn = conexion();
            asegurarTabla(conn);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        ReporteServicioZona r = new ReporteServicioZona(
                                rs.getInt("usuario_id"),
                                rs.getString("zona_barrio"),
                                rs.getString("servicio"),
                                rs.getString("estado"),
                                rs.getString("observaciones"));
                        r.setId(rs.getInt("id"));
                        r.setVerificaciones(rs.getInt("verificaciones"));
                        java.sql.Timestamp ts = rs.getTimestamp("fecha_actualizacion");
                        if (ts != null) {
                            r.setFechaReporte(ts);
                        }
                        return r;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error DAO al buscar servicio por zona: " + e.getMessage());
        }
        return null;
    }

    public boolean actualizar(ReporteServicioZona r) {
        String sql = "UPDATE estado_servicios_zona SET zona_barrio = ?, servicio = ?, estado = ?, "
                + "observaciones = ?, verificaciones = ? WHERE id = ?";
        try {
            Connection conn = conexion();
            asegurarTabla(conn);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, r.getZonaBarrio());
                ps.setString(2, r.getServicio());
                ps.setString(3, r.getEstado());
                ps.setString(4, r.getObservaciones());
                ps.setInt(5, r.getVerificaciones());
                ps.setInt(6, r.getId());
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error DAO al actualizar servicio por zona: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminar(int id) {
        String sql = "DELETE FROM estado_servicios_zona WHERE id = ?";
        try {
            Connection conn = conexion();
            asegurarTabla(conn);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, id);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error DAO al eliminar servicio por zona: " + e.getMessage());
            return false;
        }
    }

    // ===== API del Equipo 5 (conservada por compatibilidad) =====

    public boolean registrarReporteServicio(ReporteServicioZona reporte) {
        if (reporte == null || reporte.getZonaBarrio() == null) {
            return false;
        }
        String sql = "INSERT INTO estado_servicios_zona (usuario_id, zona_barrio, servicio, estado, observaciones) VALUES (?, ?, ?, ?, ?)";
        try {
            Connection conn = conexion();
            asegurarTabla(conn);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, reporte.getUsuarioId());
                ps.setString(2, reporte.getZonaBarrio());
                ps.setString(3, reporte.getServicio());
                ps.setString(4, reporte.getEstado());
                ps.setString(5, reporte.getObservaciones());
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error DAO al registrar servicio en zona: " + e.getMessage());
            return false;
        }
    }

    public List<ReporteServicioZona> obtenerServiciosPorBarrio(String barrio) {
        if (barrio == null || barrio.trim().isEmpty()) {
            return listarTodosLosServicios();
        }
        List<ReporteServicioZona> lista = new ArrayList<>();
        String sql = "SELECT * FROM estado_servicios_zona WHERE zona_barrio LIKE ? ORDER BY fecha_actualizacion DESC";
        try {
            Connection conn = conexion();
            asegurarTabla(conn);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, "%" + barrio.trim() + "%");
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        lista.add(mapearFila(rs));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error DAO al consultar servicios por barrio: " + e.getMessage());
        }
        return lista;
    }

    public List<ReporteServicioZona> listarTodosLosServicios() {
        List<ReporteServicioZona> lista = new ArrayList<>();
        String sql = "SELECT * FROM estado_servicios_zona ORDER BY fecha_actualizacion DESC";
        try {
            Connection conn = conexion();
            asegurarTabla(conn);
            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearFila(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error DAO al listar todos los servicios: " + e.getMessage());
        }
        return lista;
    }

    public boolean actualizarEstadoServicio(int idReporte, String nuevoEstado) {
        String sql = "UPDATE estado_servicios_zona SET estado = ? WHERE id = ?";
        try {
            Connection conn = conexion();
            asegurarTabla(conn);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, nuevoEstado);
                ps.setInt(2, idReporte);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error DAO al actualizar estado del servicio: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminarReporte(int idReporte) {
        String sql = "DELETE FROM estado_servicios_zona WHERE id = ?";
        try {
            Connection conn = conexion();
            asegurarTabla(conn);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, idReporte);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error DAO al eliminar reporte de servicio: " + e.getMessage());
            return false;
        }
    }

    protected ReporteServicioZona mapearFila(ResultSet rs) throws SQLException {
        ReporteServicioZona reporte = new ReporteServicioZona();
        reporte.setId(rs.getInt("id"));
        reporte.setUsuarioId(rs.getInt("usuario_id"));
        reporte.setZonaBarrio(rs.getString("zona_barrio"));
        reporte.setServicio(rs.getString("servicio"));
        reporte.setEstado(rs.getString("estado"));
        reporte.setObservaciones(rs.getString("observaciones"));
        reporte.setVerificaciones(rs.getInt("verificaciones"));
        reporte.setFechaReporte(rs.getTimestamp("fecha_actualizacion"));
        return reporte;
    }
}