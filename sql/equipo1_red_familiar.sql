-- =====================================================================
-- Equipo 1: Autenticación y Red Familiar
-- Ejecutar UNA vez sobre emergencias_db (phpMyAdmin > pestaña SQL).
-- Agrega el vínculo entre un usuario y otro usuario registrado (su familiar).
-- =====================================================================
USE emergencias_db;

ALTER TABLE familiares
    ADD COLUMN familiar_usuario_id INT(11) NULL AFTER usuario_id,
    ADD UNIQUE KEY uk_vinculo_familiar (usuario_id, familiar_usuario_id),
    ADD CONSTRAINT fk_familiar_usuario
        FOREIGN KEY (familiar_usuario_id) REFERENCES usuarios (id) ON DELETE CASCADE;
