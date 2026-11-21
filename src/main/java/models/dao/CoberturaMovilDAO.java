/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models.dao;

import config.ConexionDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import models.ReporteCoberturaMovil;


public class CoberturaMovilDAO {

    
    private ReporteCoberturaMovil mapearFila(ResultSet rs) throws SQLException {
        ReporteCoberturaMovil reporte = new ReporteCoberturaMovil();
        reporte.setId(rs.getInt("id"));
        reporte.setUsuarioId(rs.getInt("usuario_id"));
        reporte.setLatitud(rs.getDouble("latitud"));
        reporte.setLongitud(rs.getDouble("longitud"));
        reporte.setVerificaciones(rs.getInt("verificaciones"));
        reporte.setFechaReporte(rs.getTimestamp("fecha_reporte"));

        String desc = rs.getString("descripcion");
        reporte.setObservaciones(desc);

        
        if (desc != null && desc.startsWith("[BARRIO:")) {
            try {
                int cierre = desc.indexOf("]");
                String cabecera = desc.substring(1, cierre);
                String[] partes = cabecera.split("\\|");
                reporte.setZonaBarrio(partes[0].replace("BARRIO:", "").trim());
                reporte.setOperador(partes[1].replace("OPERADOR:", "").trim());
                reporte.setNivelFalla(partes[2].replace("FALLA:", "").trim());
            } catch (Exception e) {
                reporte.setZonaBarrio("Sin especificar");
                reporte.setOperador("Desconocido");
                reporte.setNivelFalla("SIN_SENAL");
            }
        } else {
            reporte.setZonaBarrio("General");
            reporte.setOperador("No especificado");
            reporte.setNivelFalla("SIN_SENAL");
        }
        return reporte;
    }
}
    
