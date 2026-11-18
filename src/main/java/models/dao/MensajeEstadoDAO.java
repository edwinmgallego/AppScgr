package models.dao;

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
}
