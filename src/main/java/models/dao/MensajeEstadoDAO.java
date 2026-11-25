package models.dao;

import models.MensajeEstado;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class MensajeEstadoDAO extends BaseDAO {

    @Override
    protected String getNombreTabla() {
        return "familiares"; 
    }

    public boolean registrarEstado(MensajeEstado mensaje) {
        String actualizarRed = "UPDATE familiares SET estado_emergencia = ? WHERE familiar_usuario_id = ?";
        String guardarUbicacion = "UPDATE usuarios SET ultima_latitud = ?, ultima_longitud = ?, "
                                + "fecha_actualizacion = ? WHERE id = ?";
        try {
            Connection conn = conexion();
            conn.setAutoCommit(false);
            try (PreparedStatement psRed = conn.prepareStatement(actualizarRed)) {
                psRed.setString(1, mensaje.getEstado().getEstadoRed());
                psRed.setInt(2, mensaje.getUsuarioId());
                psRed.executeUpdate();

                if (mensaje.tieneUbicacion()) {
                    try (PreparedStatement psUbicacion = conn.prepareStatement(guardarUbicacion)) {
                        psUbicacion.setDouble(1, mensaje.getLatitud());
                        psUbicacion.setDouble(2, mensaje.getLongitud());
                        psUbicacion.setTimestamp(3, Timestamp.valueOf(mensaje.getFechaCreacion()));
                        psUbicacion.setInt(4, mensaje.getUsuarioId());
                        psUbicacion.executeUpdate();
                    }
                }
                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("Error DAO al registrar el estado: " + e.getMessage());
            return false;
        }
    }
    
    public double[] obtenerUltimaUbicacion(int usuarioId) {
        String sql = "SELECT ultima_latitud, ultima_longitud FROM usuarios WHERE id = ?";
        try (PreparedStatement pstmt = conexion().prepareStatement(sql)) {
            pstmt.setInt(1, usuarioId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    double lat = rs.getDouble("ultima_latitud");
                    boolean latNula = rs.wasNull();
                    double lon = rs.getDouble("ultima_longitud");
                    boolean lonNula = rs.wasNull();
                    if (!latNula && !lonNula) {
                        return new double[]{lat, lon};
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error DAO al obtener la última ubicación: " + e.getMessage());
        }
        return null;
    }
}
