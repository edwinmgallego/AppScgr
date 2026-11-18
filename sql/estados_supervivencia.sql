
USE emergencias_db;

CREATE TABLE IF NOT EXISTS estados_supervivencia (
    id             INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id     INT NOT NULL,
    estado         ENUM('ESTOY_BIEN', 'NECESITO_AYUDA') NOT NULL,
    latitud        DOUBLE NULL,                                
    longitud       DOUBLE NULL,
    fecha_creacion DATETIME NOT NULL,                            
    fecha_recibido TIMESTAMP DEFAULT CURRENT_TIMESTAMP,        
    INDEX idx_estados_usuario (usuario_id, fecha_creacion),
    CONSTRAINT fk_estados_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios (id) ON DELETE CASCADE
);
