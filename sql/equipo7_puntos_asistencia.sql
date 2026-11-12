USE emergencias_db;

ALTER TABLE puntos_asistencia
    MODIFY COLUMN tipo_punto
        ENUM('ALBERGUE','SUPERMERCADO','MECANICO','HOSPITAL','CENTRO_ACOPIO') NOT NULL,
    ADD COLUMN IF NOT EXISTS direccion VARCHAR(150) NULL,
    ADD COLUMN IF NOT EXISTS descripcion VARCHAR(255) NULL;

CREATE TABLE IF NOT EXISTS servicios_punto (
    id INT(11) NOT NULL AUTO_INCREMENT,
    punto_id INT(11) NOT NULL,
    servicio ENUM('AGUA','INTERNET','ALIMENTACION','ENERGIA','ATENCION_MEDICA') NOT NULL,
    disponible TINYINT(1) NOT NULL DEFAULT 1,
    PRIMARY KEY (id),
    UNIQUE KEY uk_servicio_punto (punto_id, servicio),
    CONSTRAINT fk_servicio_punto FOREIGN KEY (punto_id)
        REFERENCES puntos_asistencia (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

CREATE TABLE IF NOT EXISTS necesidades_voluntarios (
    id INT(11) NOT NULL AUTO_INCREMENT,
    punto_id INT(11) NOT NULL,
    perfil VARCHAR(100) NOT NULL,
    cantidad_requerida INT(11) NOT NULL,
    activa TINYINT(1) NOT NULL DEFAULT 1,
    fecha_actualizacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_necesidad_punto FOREIGN KEY (punto_id)
        REFERENCES puntos_asistencia (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;
