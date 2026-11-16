package models.dao;

import config.ConexionDB;
import models.Familiar;
import models.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Consultas SQL de la tabla familiares: vincular, listar, cambiar estado y desvincular.
 *
 * @author Equipo 1 - Autenticación y Red Familiar
 */
public class FamiliarDAO {

    private Connection conexion() throws SQLException {
        Connection conn = ConexionDB.getInstance().getConnection();
        if (conn == null) {
            throw new SQLException("No hay conexión con la base de datos.");
        }
        return conn;
    }

    // CREATE: vincula a un usuario registrado como familiar del usuario indicado
    public boolean vincularFamiliar(int usuarioId, Usuario familiar) {
        if (familiar == null || familiar.getId() == usuarioId || estaVinculado(usuarioId, familiar.getId())) {
            return false;
        }
        String sql = "INSERT INTO familiares (usuario_id, familiar_usuario_id, nombre_familiar, telefono) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = conexion().prepareStatement(sql)) {
            pstmt.setInt(1, usuarioId);
            pstmt.setInt(2, familiar.getId());
            pstmt.setString(3, familiar.getNombreCompleto());
            pstmt.setString(4, familiar.getTelefono());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error DAO al vincular el familiar: " + e.getMessage());
            return false;
        }
    }

    // Revisa si ya existe el vínculo para no duplicarlo
    public boolean estaVinculado(int usuarioId, int familiarUsuarioId) {
        String sql = "SELECT 1 FROM familiares WHERE usuario_id = ? AND familiar_usuario_id = ?";
        try (PreparedStatement pstmt = conexion().prepareStatement(sql)) {
            pstmt.setInt(1, usuarioId);
            pstmt.setInt(2, familiarUsuarioId);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("Error DAO al verificar el vínculo: " + e.getMessage());
            return false;
        }
    }

    // READ: familiares de un usuario, con datos actualizados desde la tabla usuarios
    public List<Familiar> obtenerFamiliares(int usuarioId) {
        List<Familiar> lista = new ArrayList<>();
        String sql = "SELECT f.id, f.usuario_id, f.familiar_usuario_id, u.documento, "
                + "COALESCE(u.nombre_completo, f.nombre_familiar) AS nombre, "
                + "COALESCE(u.telefono, f.telefono) AS telefono, f.estado_emergencia "
                + "FROM familiares f LEFT JOIN usuarios u ON u.id = f.familiar_usuario_id "
                + "WHERE f.usuario_id = ? ORDER BY nombre";
        try (PreparedStatement pstmt = conexion().prepareStatement(sql)) {
            pstmt.setInt(1, usuarioId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int idFamiliarUsuario = rs.getInt("familiar_usuario_id");
                    Integer familiarUsuarioId = rs.wasNull() ? null : idFamiliarUsuario;
                    lista.add(new Familiar(rs.getInt("id"), rs.getInt("usuario_id"), familiarUsuarioId,
                            rs.getString("documento"), rs.getString("nombre"),
                            rs.getString("telefono"), rs.getString("estado_emergencia")));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error DAO al obtener los familiares: " + e.getMessage());
        }
        return lista;
    }

    // UPDATE: cambia el estado de emergencia (SEGURO, NECESITA AYUDA, DESCONOCIDO)
    public boolean actualizarEstado(int idFamiliar, int usuarioId, String estado) {
        String sql = "UPDATE familiares SET estado_emergencia = ? WHERE id = ? AND usuario_id = ?";
        try (PreparedStatement pstmt = conexion().prepareStatement(sql)) {
            pstmt.setString(1, estado);
            pstmt.setInt(2, idFamiliar);
            pstmt.setInt(3, usuarioId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error DAO al actualizar el estado: " + e.getMessage());
            return false;
        }
    }

    // DELETE: quita el vínculo (solo si pertenece al usuario que lo pide)
    public boolean desvincularFamiliar(int idFamiliar, int usuarioId) {
        String sql = "DELETE FROM familiares WHERE id = ? AND usuario_id = ?";
        try (PreparedStatement pstmt = conexion().prepareStatement(sql)) {
            pstmt.setInt(1, idFamiliar);
            pstmt.setInt(2, usuarioId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error DAO al desvincular el familiar: " + e.getMessage());
            return false;
        }
    }
}
