package models.dao;

import models.MensajeEstado;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;


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
        String sql = "INSERT INTO estados_supervivencia (usuario_id, estado, latitud, longitud, fecha_creacion) "
                   + "VALUES (?, ?, ?, ?, ?)";
        try {
            Connection conn = conexion();
            asegurarTabla(conn);
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, mensaje.getUsuarioId());
                pstmt.setString(2, mensaje.getEstado().name());
                if (mensaje.tieneUbicacion()) {
                    pstmt.setDouble(3, mensaje.getLatitud());
                    pstmt.setDouble(4, mensaje.getLongitud());
                } else {
                    pstmt.setNull(3, Types.DOUBLE);
                    pstmt.setNull(4, Types.DOUBLE);
                }
                
                pstmt.setTimestamp(5, Timestamp.valueOf(mensaje.getFechaCreacion()));
                return pstmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error DAO al registrar el estado: " + e.getMessage());
            return false;
        }
    }
}
