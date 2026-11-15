/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models.dao;

/**
 *
 * @author juans
 */

import config.ConexionDB;
import models.daños.DañoEstructural;
import models.daños.NivelRiesgo;
import models.daños.TipoDaño;
 
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
 
public class DañoEstructuralDAO {
 
    private static final String TIPO = FormatoDescripcion.TIPO_INCIDENTE;
 
    // Método CREATE
    public int insertar(DañoEstructural daño) {
        String sql = "INSERT INTO reportes_incidentes (usuario_id, tipo_incidente, descripcion, latitud, longitud) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conexion().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, daño.getUsuarioId());
            ps.setString(2, TIPO);
            ps.setString(3, FormatoDescripcion.codificar(daño));
            ps.setDouble(4, daño.getLatitud());
            ps.setDouble(5, daño.getLongitud());
            ps.executeUpdate();
 
            // sacamos el id que le puso MySQL
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    daño.setId(rs.getInt(1));
                    return daño.getId();
                }
            }
        } catch (SQLException e) {
            System.err.println("Error DAO al insertar daño: " + e.getMessage());
        }
        return -1;
    }
 
    // La conexión es compartida (Singleton), por eso no la cerramos
    private Connection conexion() {
        return ConexionDB.getInstance().getConnection();
    }
}
