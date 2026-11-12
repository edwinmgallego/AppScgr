-- =====================================================================
-- Equipo 7: Puntos de Asistencia y Negocios (RF-09, RF-10)
-- Ejecutar UNA vez sobre emergencias_db (phpMyAdmin > pestaña SQL).
-- =====================================================================
USE emergencias_db;

-- 1. Centro de acopio como tipo de punto + datos para mostrar en el mapa
ALTER TABLE puntos_asistencia
    MODIFY COLUMN tipo_punto
        ENUM('ALBERGUE','SUPERMERCADO','MECANICO','HOSPITAL','CENTRO_ACOPIO') NOT NULL,
    ADD COLUMN direccion VARCHAR(150) NULL AFTER nombre,
    ADD COLUMN descripcion VARCHAR(255) NULL AFTER direccion;

-- 2. Un servicio por fila (agua, internet, alimentación...)
CREATE TABLE servicios_punto (
    id INT(11) NOT NULL AUTO_INCREMENT,
    punto_id INT(11) NOT NULL,
    servicio ENUM('AGUA','INTERNET','ALIMENTACION','ENERGIA','ATENCION_MEDICA') NOT NULL,
    disponible TINYINT(1) NOT NULL DEFAULT 1,
    PRIMARY KEY (id),
    UNIQUE KEY uk_servicio_punto (punto_id, servicio),
    CONSTRAINT fk_servicio_punto FOREIGN KEY (punto_id)
        REFERENCES puntos_asistencia (id) ON DELETE CASCADE
);

-- 3. Necesidad de voluntarios de cada punto
CREATE TABLE necesidades_voluntarios (
    id INT(11) NOT NULL AUTO_INCREMENT,
    punto_id INT(11) NOT NULL,
    perfil VARCHAR(100) NOT NULL,
    cantidad_requerida INT(11) NOT NULL,
    activa TINYINT(1) NOT NULL DEFAULT 1,
    fecha_actualizacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_necesidad_punto FOREIGN KEY (punto_id)
        REFERENCES puntos_asistencia (id) ON DELETE CASCADE
);
