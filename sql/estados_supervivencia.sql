-- Equipo 2 (RF-01, RF-02): estados de supervivencia
-- Ejecutar en phpMyAdmin sobre emergencias_db. (La app también crea esta tabla sola la primera
-- vez que se envía un estado, así que este script es opcional.)
USE emergencias_db;

CREATE TABLE IF NOT EXISTS estados_supervivencia (
    id             INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id     INT NOT NULL,
    estado         ENUM('ESTOY_BIEN', 'NECESITO_AYUDA') NOT NULL,
    latitud        DOUBLE NULL,                                  -- ubicación opcional
    longitud       DOUBLE NULL,
    fecha_creacion DATETIME NOT NULL,                            -- cuándo lo emitió el usuario
    fecha_recibido TIMESTAMP DEFAULT CURRENT_TIMESTAMP,          -- cuándo llegó al servidor
    INDEX idx_estados_usuario (usuario_id, fecha_creacion),
    CONSTRAINT fk_estados_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios (id) ON DELETE CASCADE
);
