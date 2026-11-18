package models.dao;

import models.MensajeEstado;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;

/**
 * Persistencia de los mensajes de estado de supervivencia (RF-01, RF-02).
 * Tabla: estados_supervivencia (se crea sola la primera vez; ver sql/estados_supervivencia.sql).
 *
 * Al guardar un estado también se actualiza familiares.estado_emergencia de todos los usuarios que
 * tienen al emisor en su red familiar, para que lo vean en "Mi red familiar" (Equipo 1).
 *
 * Hereda de BaseDAO la conexión compartida y la creación de la tabla (herencia); aquí solo se
 * sobrescriben los datos propios de esta tabla.
 */
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

    // La tabla se crea sola la primera vez (lo hace BaseDAO.asegurarTabla con este SQL)
    @Override
    protected String getSqlCrearTabla() {
        return CREAR_TABLA;
    }

    // CREATE + UPDATE con transacción: guarda el estado y lo refleja en la red familiar.
    // Si algo falla se deshace todo y el mensaje se queda en la cola para reintentar.
    public boolean registrarEstado(MensajeEstado mensaje) {
        String insertar = "INSERT INTO estados_supervivencia (usuario_id, estado, latitud, longitud, fecha_creacion) "
                        + "VALUES (?, ?, ?, ?, ?)";
        String actualizarRed = "UPDATE familiares SET estado_emergencia = ? WHERE familiar_usuario_id = ?";
        try {
            Connection conn = conexion();
            asegurarTabla(conn);
            conn.setAutoCommit(false); // Inicia la transacción
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
                // Se guarda la fecha original de emisión, no la del momento de sincronizar
                psInsertar.setTimestamp(5, Timestamp.valueOf(mensaje.getFechaCreacion()));
                psInsertar.executeUpdate();

                psRed.setString(1, mensaje.getEstado().getEstadoRed());
                psRed.setInt(2, mensaje.getUsuarioId());
                psRed.executeUpdate(); // Puede afectar 0 filas: nadie lo tiene como familiar

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true); // La conexión es compartida: se deja como estaba
            }
        } catch (SQLException e) {
            System.err.println("Error DAO al registrar el estado: " + e.getMessage());
            return false;
        }
    }
}
