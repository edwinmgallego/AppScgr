package models.dao;

import models.EstadoSupervivencia;
import models.MensajeEstado;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;


public class MensajeEstadoDAO extends BaseDAO {

    private static final String CREAR_TABLA =
            "CREATE TABLE IF NOT EXISTS estados_supervivencia ("
          + "id INT AUTO_INCREMENT PRIMARY KEY, "
          + "usuario_id INT NOT NULL, "
          + "estado ENUM('ESTOY_BIEN','NECESITO_AYUDA') NOT NULL, "
          + "latitud DOUBLE NULL, "
          + "longitud DOUBLE NULL, "
          + "fecha_creacion DATETIME NOT NULL, "
          + "fecha_recibido TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
          + "INDEX idx_estados_usuario (usuario_id, fecha_creacion), "
          + "CONSTRAINT fk_estados_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios (id) ON DELETE CASCADE)";

    @Override
    protected String getNombreTabla() {
        return "estados_supervivencia";
    }

    @Override
    protected String getSqlCrearTabla() {
        return CREAR_TABLA;
    }

    public boolean registrarEstado(MensajeEstado mensaje) {
        String insertar = "INSERT INTO estados_supervivencia (usuario_id, estado, latitud, longitud, fecha_creacion) "
                        + "VALUES (?, ?, ?, ?, ?)";
        String actualizarRed = "UPDATE familiares SET estado_emergencia = ? WHERE familiar_usuario_id = ?";
        try {
            Connection conn = conexion();
            asegurarTabla(conn);
            conn.setAutoCommit(false); 
            try (PreparedStatement psInsertar = conn.prepareStatement(insertar);
                 PreparedStatement psRed = conn.prepareStatement(actualizarRed)) {
                psInsertar.setInt(1, mensaje.getUsuarioId());
                psInsertar.setString(2, mensaje.getEstado().name());
                if (mensaje.tieneUbicacion()) {
                    psInsertar.setDouble(3, mensaje.getLatitud());
                    psInsertar.setDouble(4, mensaje.getLongitud());
                } else {
                    psInsertar.setNull(3, Types.DOUBLE);
                    psInsertar.setNull(4, Types.DOUBLE);
                }
                
                psInsertar.setTimestamp(5, Timestamp.valueOf(mensaje.getFechaCreacion()));
                psInsertar.executeUpdate();

                psRed.setString(1, mensaje.getEstado().getEstadoRed());
                psRed.setInt(2, mensaje.getUsuarioId());
                psRed.executeUpdate(); 

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

   
    public List<MensajeEstado> obtenerPorUsuario(int usuarioId) {
        return obtenerPorUsuario(usuarioId, 0);
    }

    public List<MensajeEstado> obtenerPorUsuario(int usuarioId, int limite) {
        List<MensajeEstado> lista = new ArrayList<>();
        String sql = "SELECT e.id, e.usuario_id, e.estado, e.latitud, e.longitud, e.fecha_creacion, "
                   + "u.nombre_completo, u.telefono "
                   + "FROM estados_supervivencia e LEFT JOIN usuarios u ON u.id = e.usuario_id "
                   + "WHERE e.usuario_id = ? ORDER BY e.fecha_creacion DESC, e.id DESC"
                   + (limite > 0 ? " LIMIT " + limite : "");
        try {
            Connection conn = conexion();
            asegurarTabla(conn);
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, usuarioId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        MensajeEstado m = new MensajeEstado(
                                rs.getInt("id"),
                                rs.getInt("usuario_id"),
                                EstadoSupervivencia.valueOf(rs.getString("estado")),
                                rs.getTimestamp("fecha_creacion").toLocalDateTime(),
                                true); 
                        m.setNombreUsuario(rs.getString("nombre_completo"));
                        m.setTelefonoUsuario(rs.getString("telefono"));
                        double lat = rs.getDouble("latitud");
                        if (!rs.wasNull()) {
                            m.setLatitud(lat);
                        }
                        double lon = rs.getDouble("longitud");
                        if (!rs.wasNull()) {
                            m.setLongitud(lon);
                        }
                        lista.add(m);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error DAO al obtener los estados: " + e.getMessage());
        }
        return lista;
    }
}
