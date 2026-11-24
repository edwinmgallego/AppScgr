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
import models.EstadoVia;
import java.sql.DatabaseMetaData;
import java.sql.Statement;

public class IncidenteDAO {
    private static boolean columnaEstadoVerificada = false;

    private void asegurarColumnaEstado(Connection conn) {
        if (columnaEstadoVerificada) {
            return;
        }
        try {
            DatabaseMetaData meta = conn.getMetaData();
            try (ResultSet rs = meta.getColumns(conn.getCatalog(), null, "reportes_incidentes", "estado")) {
                if (!rs.next()) {
                    try (Statement st = conn.createStatement()) {
                        st.executeUpdate("ALTER TABLE reportes_incidentes ADD COLUMN estado VARCHAR(20) NOT NULL DEFAULT 'CERRADA'");
                    }
                }
            }
            columnaEstadoVerificada = true;
        } catch (SQLException e) {
            System.err.println("Aviso DAO al verificar columna 'estado': " + e.getMessage());
        }
    }

    // Método CREATE: Inserta un nuevo incidente en la base de datos
    public boolean registrarIncidente(Incidente incidente, int idUsuarioLogueado) {
        String sql = "INSERT INTO reportes_incidentes (usuario_id, tipo_incidente, descripcion, latitud, longitud, estado) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexionDB.getInstance().getConnection()) {
            asegurarColumnaEstado(conn);
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, idUsuarioLogueado);
                pstmt.setString(2, incidente.getTipo());
                pstmt.setString(3, incidente.getDescripcion());
                pstmt.setDouble(4, incidente.getLatitud());
                pstmt.setDouble(5, incidente.getLongitud());
                pstmt.setString(6, incidente.getEstadoVia() != null
                        ? incidente.getEstadoVia().name()
                        : EstadoVia.CERRADA.name());

                int filasAfectadas = pstmt.executeUpdate();
                return filasAfectadas > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error DAO al registrar el incidente: " + e.getMessage());
            return false;
        }
    }

    // Método READ: Consulta todos los incidentes para mostrarlos en el mapa o en una tabla
    public List<Incidente> obtenerTodosLosIncidentes() {
        List<Incidente> listaIncidentes = new ArrayList<>();
        String sql = "SELECT id, tipo_incidente, latitud, longitud, descripcion, estado FROM reportes_incidentes";

        try (Connection conn = ConexionDB.getInstance().getConnection()) {
            asegurarColumnaEstado(conn);
            try (PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {
                    int id = rs.getInt("id");
                    String tipo = rs.getString("tipo_incidente");
                    double latitud = rs.getDouble("latitud");
                    double longitud = rs.getDouble("longitud");
                    String descripcion = rs.getString("descripcion");
                    EstadoVia estado = EstadoVia.fromString(rs.getString("estado"));

                    Incidente incidente = new Incidente(id, tipo, latitud, longitud, descripcion, estado);
                    listaIncidentes.add(incidente);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error DAO al obtener los incidentes: " + e.getMessage());
        }

        return listaIncidentes;
    }

    // Método UPDATE: Actualiza los detalles o coordenadas de un incidente existente
    public boolean actualizarIncidente(Incidente incidente) {
        String sql = "UPDATE reportes_incidentes SET tipo_incidente = ?, descripcion = ?, latitud = ?, longitud = ? WHERE id = ?";
        
        try (Connection conn = ConexionDB.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, incidente.getTipo());
            pstmt.setString(2, incidente.getDescripcion());
            pstmt.setDouble(3, incidente.getLatitud());
            pstmt.setDouble(4, incidente.getLongitud());
            
            // Pasamos el ID del incidente que queremos modificar
            pstmt.setInt(5, incidente.getId()); // Asume que la clase Incidente tiene un getId()
            
            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;
            
        } catch (SQLException e) {
            System.err.println("Error DAO al actualizar el incidente: " + e.getMessage());
            return false;
        }
    }

    public boolean actualizarEstado(int idIncidente, EstadoVia nuevoEstado) {
        String sql = "UPDATE reportes_incidentes SET estado = ? WHERE id = ?";

        try (Connection conn = ConexionDB.getInstance().getConnection()) {
            asegurarColumnaEstado(conn);
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, nuevoEstado != null ? nuevoEstado.name() : EstadoVia.CERRADA.name());
                pstmt.setInt(2, idIncidente);
                return pstmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error DAO al actualizar estado del incidente: " + e.getMessage());
            return false;
        }
    }

    // Método DELETE FÍSICO: Borra permanentemente el incidente de la base de datos
    public boolean eliminarIncidente(int idIncidente) {
        String sql = "DELETE FROM reportes_incidentes WHERE id = ?";
        
        try (Connection conn = ConexionDB.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idIncidente);
            
            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;
            
        } catch (SQLException e) {
            System.err.println("Error DAO al eliminar el incidente: " + e.getMessage());
            return false;
        }
    }

    // Método DELETE LÓGICO (Alternativa Recomendada para historiales de emergencia):
    // Nota: Para usar este, los estudiantes tendrían que agregar una columna 'estado' (ACTIVO/RESUELTO) en la BD.
    /*
    public boolean resolverIncidente(int idIncidente) {
        String sql = "UPDATE reportes_incidentes SET estado = 'RESUELTO' WHERE id = ?";
        // ... (misma lógica del PreparedStatement)
    }
    */
}
