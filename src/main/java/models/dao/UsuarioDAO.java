package models.dao;

import config.ConexionDB;
import models.Usuario;
import utils.PasswordUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Consultas SQL de la tabla usuarios: registro, login y búsqueda.
 * La conexión es compartida (Singleton), por eso NO va dentro del try-with-resources:
 * solo se cierran los PreparedStatement y ResultSet.
 *
 * @author Equipo 1 - Autenticación y Red Familiar
 */
public class UsuarioDAO {

    private Connection conexion() throws SQLException {
        Connection conn = ConexionDB.getInstance().getConnection();
        if (conn == null) {
            throw new SQLException("No hay conexión con la base de datos.");
        }
        return conn;
    }

    private Usuario mapear(ResultSet rs) throws SQLException {
        return new Usuario(rs.getInt("id"), rs.getString("documento"),
                rs.getString("nombre_completo"), rs.getString("telefono"));
    }

    // CREATE: registra el usuario con la contraseña cifrada y le asigna el id generado
    public boolean registrarUsuario(Usuario usuario, String password) {
        String sql = "INSERT INTO usuarios (documento, nombre_completo, telefono, password) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = conexion().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, usuario.getDocumento());
            pstmt.setString(2, usuario.getNombreCompleto());
            pstmt.setString(3, usuario.getTelefono());
            pstmt.setString(4, PasswordUtil.cifrar(password));

            if (pstmt.executeUpdate() == 0) {
                return false;
            }
            try (ResultSet llaves = pstmt.getGeneratedKeys()) {
                if (llaves.next()) {
                    usuario.setId(llaves.getInt(1));
                }
            }
            return true;
        } catch (SQLException e) {
            System.err.println("Error DAO al registrar el usuario: " + e.getMessage());
            return false;
        }
    }

    // Verifica si ya hay alguien registrado con ese documento
    public boolean existeDocumento(String documento) {
        String sql = "SELECT 1 FROM usuarios WHERE documento = ?";
        try (PreparedStatement pstmt = conexion().prepareStatement(sql)) {
            pstmt.setString(1, documento);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("Error DAO al verificar el documento: " + e.getMessage());
            return false;
        }
    }

    // LOGIN: devuelve el usuario si documento y contraseña coinciden, o null si no
    public Usuario autenticar(String documento, String password) {
        String sql = "SELECT id, documento, nombre_completo, telefono, password FROM usuarios WHERE documento = ?";
        try (PreparedStatement pstmt = conexion().prepareStatement(sql)) {
            pstmt.setString(1, documento);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                String guardado = rs.getString("password");
                if (!PasswordUtil.verificar(password, guardado)) {
                    return null;
                }
                return mapear(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error DAO al autenticar: " + e.getMessage());
            return null;
        }
    }

    // UPDATE: cambia la contraseña (siempre se guarda cifrada)
    public boolean actualizarPassword(int idUsuario, String nuevaPassword) {
        String sql = "UPDATE usuarios SET password = ? WHERE id = ?";
        try (PreparedStatement pstmt = conexion().prepareStatement(sql)) {
            pstmt.setString(1, PasswordUtil.cifrar(nuevaPassword));
            pstmt.setInt(2, idUsuario);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error DAO al actualizar la contraseña: " + e.getMessage());
            return false;
        }
    }

    // READ: busca por documento exacto o por parte del nombre, sin incluir al usuario que busca
    public List<Usuario> buscarUsuarios(String texto, int idExcluir) {
        List<Usuario> resultados = new ArrayList<>();
        String sql = "SELECT id, documento, nombre_completo, telefono FROM usuarios "
                + "WHERE (documento = ? OR nombre_completo LIKE ?) AND id <> ? "
                + "ORDER BY nombre_completo LIMIT 50";
        try (PreparedStatement pstmt = conexion().prepareStatement(sql)) {
            pstmt.setString(1, texto);
            pstmt.setString(2, "%" + texto + "%");
            pstmt.setInt(3, idExcluir);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    resultados.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error DAO al buscar usuarios: " + e.getMessage());
        }
        return resultados;
    }

    // READ: un usuario por su id, o null si no existe
    public Usuario obtenerPorId(int id) {
        String sql = "SELECT id, documento, nombre_completo, telefono FROM usuarios WHERE id = ?";
        try (PreparedStatement pstmt = conexion().prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        } catch (SQLException e) {
            System.err.println("Error DAO al obtener el usuario: " + e.getMessage());
            return null;
        }
    }

    // DELETE: borra el usuario (sus vínculos familiares se borran en cascada)
    public boolean eliminarUsuario(int id) {
        String sql = "DELETE FROM usuarios WHERE id = ?";
        try (PreparedStatement pstmt = conexion().prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error DAO al eliminar el usuario: " + e.getMessage());
            return false;
        }
    }

    public boolean actualizarPerfil(int id, String nombre, String telefonoFinal) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
