/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/SQLTemplate.sql to edit this template
 */
/**
 * Author:  gabby
 * Created: 23/11/2026
 */

 -- =====================================================================
-- Grupo 9: Validación Comunitaria y Dashboard Principal (RF-13 y RF-14)
  -- Ejecutar UNA vez sobre emergencias_db (phpMyAdmin > pestaña SQL).
  -- La aplicación también crea la tabla automáticamente desde ValidacionDAO.
  -- =====================================================================
  USE emergencias_db;
 
  -- 1. Origen del reporte: permite diferenciar información oficial de la ciudadana.
  ALTER TABLE reportes_incidentes
  ADD COLUMN origen_reporte VARCHAR(20) NOT NULL DEFAULT 'CIUDADANO';
 
  -- 2. Votos de validación comunitaria.
  -- La llave única evita que un usuario vote dos veces el mismo reporte.
  CREATE TABLE IF NOT EXISTS validaciones_reporte (
  id INT AUTO_INCREMENT PRIMARY KEY,
  reporte_id INT NOT NULL,
  usuario_id INT NOT NULL,
  voto VARCHAR(20) NOT NULL,
  fecha TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_voto_usuario (reporte_id, usuario_id),
  CONSTRAINT fk_val_reporte FOREIGN KEY (reporte_id)
  REFERENCES reportes_incidentes(id) ON DELETE CASCADE,
  CONSTRAINT fk_val_usuario FOREIGN KEY (usuario_id)
  REFERENCES usuarios(id) ON DELETE CASCADE
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
 
  -- 3. Datos de ejemplo para probar la identificación visual (opcional).
  -- Reemplace 1 por un id real de reporte existente.
  -- UPDATE reportes_incidentes SET origen_reporte = 'OFICIAL' WHERE id = 1;
