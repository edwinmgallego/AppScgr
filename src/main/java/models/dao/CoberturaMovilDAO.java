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

    public boolean registrarFallaCobertura(ReporteCoberturaMovil reporte) {
        String sql = "INSERT INTO reportes_incidentes (usuario_id, tipo_incidente, descripcion, latitud, longitud, estado) "
                   + "VALUES (?, 'SIN_SENAL', ?, ?, ?, 'ACTIVO')";

        String descripcionFormateada = String.format("[BARRIO: %s | OPERADOR: %s | FALLA: %s] %s",
                reporte.getZonaBarrio(),
                reporte.getOperador(),
                reporte.getNivelFalla(),
                reporte.getObservaciones() != null ? reporte.getObservaciones() : "");

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, reporte.getUsuarioId());
            ps.setString(2, descripcionFormateada.trim());
            ps.setDouble(3, reporte.getLatitud());
            ps.setDouble(4, reporte.getLongitud());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al registrar falla de cobertura movil: " + e.getMessage());
            return false;
        }
    }

    public List<ReporteCoberturaMovil> obtenerReportesSinSenal() {
        List<ReporteCoberturaMovil> lista = new ArrayList<>();
        String sql = "SELECT * FROM reportes_incidentes WHERE tipo_incidente = 'SIN_SENAL' AND estado = 'ACTIVO' ORDER BY fecha_reporte DESC";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearFila(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al consultar reportes sin senal: " + e.getMessage());
        }
        return lista;
    }

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