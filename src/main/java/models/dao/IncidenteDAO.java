/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models.dao;

/**
 *
 * @author inged
 */


import config.ConexionDB;
import models.Incidente;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class IncidenteDAO {

    // Método CREATE: Inserta un nuevo incidente en la base de datos
    public boolean registrarIncidente(Incidente incidente, int idUsuarioLogueado) {
        // Usamos ? para evitar Inyección SQL (Seguridad básica)
        String sql = "INSERT INTO reportes_incidentes (usuario_id, tipo_incidente, descripcion, latitud, longitud) VALUES (?, ?, ?, ?, ?)";
        
        // El bloque try-with-resources asegura que los PreparedStatement se cierren automáticamente
        try (Connection conn = ConexionDB.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // Reemplazamos los signos de interrogación con los datos del objeto
            pstmt.setInt(1, idUsuarioLogueado);
            pstmt.setString(2, incidente.getTipo());
            pstmt.setString(3, incidente.getDescripcion());
            pstmt.setDouble(4, incidente.getLatitud());
            pstmt.setDouble(5, incidente.getLongitud());
            
            // executeUpdate() devuelve el número de filas afectadas
            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0; // Retorna true si se guardó exitosamente
            
        } catch (SQLException e) {
            System.err.println("Error DAO al registrar el incidente: " + e.getMessage());
            return false;
        }
    }

    // Método READ: Consulta todos los incidentes para mostrarlos en el mapa o en una tabla
    public List<Incidente> obtenerTodosLosIncidentes() {
        List<Incidente> listaIncidentes = new ArrayList<>();
        String sql = "SELECT id, tipo_incidente, latitud, longitud, descripcion FROM reportes_incidentes";
        
        try (Connection conn = ConexionDB.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            // Iteramos sobre los resultados de MySQL
            while (rs.next()) {
                int id = rs.getInt("id");
                String tipo = rs.getString("tipo_incidente");
                double latitud = rs.getDouble("latitud");
                double longitud = rs.getDouble("longitud");
                String descripcion = rs.getString("descripcion");
                
                // Instanciamos el objeto con los datos obtenidos
                Incidente incidente = new Incidente(id, tipo, latitud, longitud, descripcion);
                
                // Lo agregamos a la lista
                listaIncidentes.add(incidente);
            }
            
        } catch (SQLException e) {
            System.err.println("Error DAO al obtener los incidentes: " + e.getMessage());
        }
        
        return listaIncidentes;
    }
}
