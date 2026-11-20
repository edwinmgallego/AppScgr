/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models.dao;

/**
 *
 * @author 57323
 */
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import models.ReporteServicioZona;


public class ServicioZonaDAO extends BaseDAO {

    @Override
    protected String getNombreTabla() {
        return "estado_servicios_zona";
    }

    @Override
    protected String getSqlCrearTabla() {
        return "CREATE TABLE IF NOT EXISTS `estado_servicios_zona` ("
                + "`id` int(11) NOT NULL AUTO_INCREMENT,"
                + "`usuario_id` int(11) NOT NULL DEFAULT 1,"
                + "`zona_barrio` varchar(100) NOT NULL,"
                + "`servicio` enum('AGUA','ENERGIA','TELECOMUNICACIONES') NOT NULL,"
                + "`estado` enum('OPERANDO','CON_FALLAS','CAIDO') NOT NULL,"
                + "`observaciones` text DEFAULT NULL,"
                + "`verificaciones` int(11) DEFAULT 0,"
                + "`fecha_actualizacion` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),"
                + "PRIMARY KEY (`id`),"
                + "KEY `usuario_id` (`usuario_id`),"
                + "CONSTRAINT `fk_servicios_usuario` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`)"
                + ") ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;";
    }

    public boolean registrarReporteServicio(ReporteServicioZona reporte) {
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