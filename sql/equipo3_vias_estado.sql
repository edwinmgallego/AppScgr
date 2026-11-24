/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/SQLTemplate.sql to edit this template
 */
/**
 * Author:  Daniel Alejandro
 * Created: 23/11/2026
 */

USE emergencias_db;

ALTER TABLE reportes_incidentes
ADD COLUMN IF NOT EXISTS estado ENUM('ABIERTA', 'CERRADA') NOT NULL DEFAULT 'CERRADA';