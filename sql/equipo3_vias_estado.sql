/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/SQLTemplate.sql to edit this template
 */
/**
 * Author:  Daniel Alejandro
 * Created: 23/11/2026
 */

USE emergencias_db;

-- La columna 'estado' es compartida: el Equipo 3 guarda ABIERTA/CERRADA (estado de la via)
-- y el Equipo 5 guarda ACTIVO/RESUELTO (reportes SIN_SENAL). Por eso es texto (VARCHAR) y
-- no ENUM: un ENUM('ABIERTA','CERRADA') hace fallar los INSERT del Equipo 5 en modo estricto.
ALTER TABLE reportes_incidentes
ADD COLUMN IF NOT EXISTS estado VARCHAR(20) NOT NULL DEFAULT 'CERRADA';

-- Si la columna ya se habia creado como ENUM con la version anterior de este script,
-- se convierte a VARCHAR conservando los valores guardados.
ALTER TABLE reportes_incidentes
MODIFY COLUMN estado VARCHAR(20) NOT NULL DEFAULT 'CERRADA';