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
 
    // Método READ: un daño por su id
    public DañoEstructural buscarPorId(int id) {
        List<DañoEstructural> lista = consultar(" AND id = ?", id);
        return lista.isEmpty() ? null : lista.get(0);
    }
 
    // Método READ: todos los daños
    public List<DañoEstructural> listar() {
        return consultar(" ORDER BY id DESC");
    }
 
    // Sobrecarga: solo los daños de un tipo
    public List<DañoEstructural> listar(TipoDaño tipo) {
        return consultar(" AND descripcion LIKE ? ORDER BY id DESC", "daño=" + tipo.name() + " |%");
    }
 
    // Sobrecarga: solo los daños de un barrio
    public List<DañoEstructural> listar(String barrio) {
        List<DañoEstructural> resultado = new ArrayList<>();
        for (DañoEstructural d : listar()) {
            if (d.getBarrio().equalsIgnoreCase(barrio.trim())) {
                resultado.add(d);
            }
        }
        return resultado;
    }
 
    // Sobrecarga: los que tengan ese riesgo o mas
    public List<DañoEstructural> listar(NivelRiesgo minimo) {
        List<DañoEstructural> resultado = new ArrayList<>();
        for (DañoEstructural d : listar()) {
            if (d.calcularNivelRiesgo().esAlMenos(minimo)) {
                resultado.add(d);
            }
        }
        return resultado;
    }
 
    // Hace el SELECT y vuelve cada fila un objeto
    private List<DañoEstructural> consultar(String filtro, Object... parametros) {
        List<DañoEstructural> lista = new ArrayList<>();
        String sql = "SELECT id, usuario_id, descripcion, latitud, longitud FROM reportes_incidentes "
                   + "WHERE tipo_incidente = ?" + filtro;
        try (PreparedStatement ps = conexion().prepareStatement(sql)) {
            ps.setString(1, TIPO);
            for (int i = 0; i < parametros.length; i++) {
                ps.setObject(i + 2, parametros[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    try {
                        lista.add(FormatoDescripcion.crear(rs.getInt("id"), rs.getInt("usuario_id"),
                                rs.getDouble("latitud"), rs.getDouble("longitud"), rs.getString("descripcion")));
                    } catch (IllegalArgumentException e) {
                        // si no tiene nuestro formato la saltamos
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error DAO al consultar daños: " + e.getMessage());
        }
        return lista;
    }
 
    // La conexión es compartida (Singleton), por eso no la cerramos
    private Connection conexion() {
        return ConexionDB.getInstance().getConnection();
    }
}