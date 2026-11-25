package models.dao;

import config.ConexionDB;
import models.ReporteCoberturaMovil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Persistencia del módulo Cobertura móvil (RF-08).
 * Tabla propia: reportes_cobertura_movil.
 *
 * Incluye la API del Grupo 9 (insertar/listar/buscarPorId/actualizar/eliminar)
 * y la API del Equipo 5 (registrarFallaCobertura, obtenerReportesSinSenal, ...)
 * para que ServiciosController y sus pruebas sigan funcionando.
 *
 * @author Grupo 9 - Validación Comunitaria y Dashboard
 */
public class CoberturaMovilDAO extends BaseDAO {

    @Override
    protected String getNombreTabla() {
        return "reportes_cobertura_movil";
    }

    @Override
    protected String getSqlCrearTabla() {
        return "CREATE TABLE IF NOT EXISTS reportes_cobertura_movil ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "usuario_id INT NOT NULL, "
                + "zona_barrio VARCHAR(100) NOT NULL, "
                + "operador VARCHAR(100) NOT NULL, "
                + "nivel_falla VARCHAR(30) NOT NULL, "
                + "latitud DOUBLE NOT NULL, "
                + "longitud DOUBLE NOT NULL, "
                + "observaciones TEXT, "
                + "fecha_reporte TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, "
                + "CONSTRAINT fk_cm_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id)"
                + ") ENGINE=InnoDB DEFAULT CHARSET=utf8";
    }

    // ===== API del Grupo 9 =====

    public int insertar(ReporteCoberturaMovil r) {
        String sql = "INSERT INTO reportes_cobertura_movil "
                + "(usuario_id, zona_barrio, operador, nivel_falla, latitud, longitud, observaciones) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try {
            Connection conn = conexion();
            asegurarTabla(conn);
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, r.getUsuarioId());
                ps.setString(2, r.getZonaBarrio());
                ps.setString(3, r.getOperador());
                ps.setString(4, r.getNivelFalla());
                ps.setDouble(5, r.getLatitud());
                ps.setDouble(6, r.getLongitud());
                ps.setString(7, r.getObservaciones());
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
            System.err.println("Error DAO al insertar cobertura móvil: " + e.getMessage());
        }
        return -1;
    }

    public List<ReporteCoberturaMovil> listar() {
        List<ReporteCoberturaMovil> lista = new ArrayList<>();
        String sql = "SELECT id, usuario_id, zona_barrio, operador, nivel_falla, latitud, longitud, observaciones, fecha_reporte "
                + "FROM reportes_cobertura_movil ORDER BY id DESC";
        try {
            Connection conn = conexion();
            asegurarTabla(conn);
            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ReporteCoberturaMovil r = new ReporteCoberturaMovil(
                            rs.getInt("usuario_id"),
                            rs.getString("zona_barrio"),
                            rs.getString("operador"),
                            rs.getString("nivel_falla"),
                            rs.getDouble("latitud"),
                            rs.getDouble("longitud"),
                            rs.getString("observaciones"));
                    r.setId(rs.getInt("id"));
                    java.sql.Timestamp ts = rs.getTimestamp("fecha_reporte");
                    if (ts != null) {
                        r.setFechaReporte(ts);
                    }
                    lista.add(r);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error DAO al listar cobertura móvil: " + e.getMessage());
        }
        return lista;
    }

    public ReporteCoberturaMovil buscarPorId(int id) {
        String sql = "SELECT id, usuario_id, zona_barrio, operador, nivel_falla, latitud, longitud, observaciones, fecha_reporte "
                + "FROM reportes_cobertura_movil WHERE id = ?";
        try {
            Connection conn = conexion();
            asegurarTabla(conn);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        ReporteCoberturaMovil r = new ReporteCoberturaMovil(
                                rs.getInt("usuario_id"),
                                rs.getString("zona_barrio"),
                                rs.getString("operador"),
                                rs.getString("nivel_falla"),
                                rs.getDouble("latitud"),
                                rs.getDouble("longitud"),
                                rs.getString("observaciones"));
                        r.setId(rs.getInt("id"));
                        java.sql.Timestamp ts = rs.getTimestamp("fecha_reporte");
                        if (ts != null) {
                            r.setFechaReporte(ts);
                        }
                        return r;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error DAO al buscar cobertura móvil: " + e.getMessage());
        }
        return null;
    }

    public boolean actualizar(ReporteCoberturaMovil r) {
        String sql = "UPDATE reportes_cobertura_movil SET zona_barrio = ?, operador = ?, nivel_falla = ?, "
                + "latitud = ?, longitud = ?, observaciones = ? WHERE id = ?";
        try {
            Connection conn = conexion();
            asegurarTabla(conn);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, r.getZonaBarrio());
                ps.setString(2, r.getOperador());
                ps.setString(3, r.getNivelFalla());
                ps.setDouble(4, r.getLatitud());
                ps.setDouble(5, r.getLongitud());
                ps.setString(6, r.getObservaciones());
                ps.setInt(7, r.getId());
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error DAO al actualizar cobertura móvil: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminar(int id) {
        String sql = "DELETE FROM reportes_cobertura_movil WHERE id = ?";
        try {
            Connection conn = conexion();
            asegurarTabla(conn);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, id);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error DAO al eliminar cobertura móvil: " + e.getMessage());
            return false;
        }
    }

    // ===== API del Equipo 5 (conservada por compatibilidad) =====

    public boolean registrarFallaCobertura(ReporteCoberturaMovil reporte) {
        if (reporte == null || reporte.getZonaBarrio() == null || reporte.getOperador() == null) {
            return false;
        }
        String sql = "INSERT INTO reportes_incidentes (usuario_id, tipo_incidente, descripcion, latitud, longitud, estado) "
                + "VALUES (?, 'SIN_SENAL', ?, ?, ?, 'ACTIVO')";

        String descripcionFormateada = String.format("[BARRIO: %s | OPERADOR: %s | FALLA: %s] %s",
                reporte.getZonaBarrio(),
                reporte.getOperador(),
                reporte.getNivelFalla(),
                reporte.getObservaciones() != null ? reporte.getObservaciones() : "");

        try (Connection conn = ConexionDB.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, reporte.getUsuarioId());
            ps.setString(2, descripcionFormateada.trim());
            ps.setDouble(3, reporte.getLatitud());
            ps.setDouble(4, reporte.getLongitud());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al registrar falla de cobertura movil: " + e.getMessage());
            return false;
        }
    }

    public List<ReporteCoberturaMovil> obtenerReportesSinSenal() {
        List<ReporteCoberturaMovil> lista = new ArrayList<>();
        String sql = "SELECT * FROM reportes_incidentes WHERE tipo_incidente = 'SIN_SENAL' AND estado = 'ACTIVO' ORDER BY fecha_reporte DESC";

        try (Connection conn = ConexionDB.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearFila(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al consultar reportes sin senal: " + e.getMessage());
        }
        return lista;
    }

    public List<ReporteCoberturaMovil> buscarPorOperadorOZona(String criterio) {
        if (criterio == null || criterio.trim().isEmpty()) {
            return obtenerReportesSinSenal();
        }
        List<ReporteCoberturaMovil> lista = new ArrayList<>();
        String sql = "SELECT * FROM reportes_incidentes WHERE tipo_incidente = 'SIN_SENAL' AND descripcion LIKE ? ORDER BY fecha_reporte DESC";

        try (Connection conn = ConexionDB.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + criterio.trim() + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearFila(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al filtrar cobertura por operador o zona: " + e.getMessage());
        }
        return lista;
    }

    public boolean marcarSenalRestablecida(int idReporte) {
        String sql = "UPDATE reportes_incidentes SET estado = 'RESUELTO' WHERE id = ? AND tipo_incidente = 'SIN_SENAL'";

        try (Connection conn = ConexionDB.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idReporte);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al marcar senal como restablecida: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminarReporteCobertura(int idReporte) {
        String sql = "DELETE FROM reportes_incidentes WHERE id = ? AND tipo_incidente = 'SIN_SENAL'";

        try (Connection conn = ConexionDB.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idReporte);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar reporte de cobertura: " + e.getMessage());
            return false;
        }
    }

    private ReporteCoberturaMovil mapearFila(ResultSet rs) throws SQLException {
        ReporteCoberturaMovil reporte = new ReporteCoberturaMovil();
        reporte.setId(rs.getInt("id"));
        reporte.setUsuarioId(rs.getInt("usuario_id"));
        reporte.setLatitud(rs.getDouble("latitud"));
        reporte.setLongitud(rs.getDouble("longitud"));
        reporte.setVerificaciones(rs.getInt("verificaciones"));
        reporte.setFechaReporte(rs.getTimestamp("fecha_reporte"));

        String desc = rs.getString("descripcion");
        reporte.setObservaciones(desc);

        if (desc != null && desc.startsWith("[BARRIO:")) {
            try {
                int cierre = desc.indexOf("]");
                String cabecera = desc.substring(1, cierre);
                String[] partes = cabecera.split("\\|");
                reporte.setZonaBarrio(partes[0].replace("BARRIO:", "").trim());
                reporte.setOperador(partes[1].replace("OPERADOR:", "").trim());
                reporte.setNivelFalla(partes[2].replace("FALLA:", "").trim());
            } catch (Exception e) {
                reporte.setZonaBarrio("Sin especificar");
                reporte.setOperador("Desconocido");
                reporte.setNivelFalla("SIN_SENAL");
            }
        } else {
            reporte.setZonaBarrio("General");
            reporte.setOperador("No especificado");
            reporte.setNivelFalla("SIN_SENAL");
        }
        return reporte;
    }
}