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
}
