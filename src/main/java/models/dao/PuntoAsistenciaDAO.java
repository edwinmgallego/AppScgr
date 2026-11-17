package models.dao;

import config.ConexionDB;
import models.EstadoPunto;
import models.PuntoAsistencia;
import models.ServiciosPunto;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PuntoAsistenciaDAO {

    private static final String COLUMNAS =
            "id, tipo_punto, nombre, latitud, longitud, estado, servicios_disponibles";

    // Método CREATE: inserta el punto y le asigna el id que generó MySQL
    public boolean registrarPunto(PuntoAsistencia punto) {
        if (!sePuedeGuardar(punto)) {
            return false;
        }
        String sql = "INSERT INTO puntos_asistencia "
                + "(tipo_punto, nombre, latitud, longitud, estado, servicios_disponibles) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexionDB.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            asignarDatos(pstmt, punto);
            if (pstmt.executeUpdate() == 0) {
                return false;
            }
            try (ResultSet llaves = pstmt.getGeneratedKeys()) {
                if (llaves.next()) {
                    punto.setId(llaves.getInt(1));
                }
            }
            return true;

        } catch (SQLException e) {
            System.err.println("Error DAO al registrar el punto de asistencia: " + e.getMessage());
            return false;
        }
    }

    // Método READ: todos los puntos, para pintarlos en el mapa o mostrarlos en una tabla
    public List<PuntoAsistencia> obtenerTodosLosPuntos() {
        List<PuntoAsistencia> puntos = new ArrayList<>();
        String sql = "SELECT " + COLUMNAS + " FROM puntos_asistencia ORDER BY id";

        try (Connection conn = ConexionDB.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                try {
                    puntos.add(convertir(rs));
                } catch (IllegalArgumentException e) {
                    // una fila con datos inválidos no debe impedir mostrar las demás
                    System.err.println("Punto omitido (id " + rs.getInt("id") + "): " + e.getMessage());
                }
            }

        } catch (SQLException e) {
            System.err.println("Error DAO al obtener los puntos de asistencia: " + e.getMessage());
        }
        return puntos;
    }

    // Método READ: un punto por su id (null si no existe)
    public PuntoAsistencia obtenerPorId(int id) {
        String sql = "SELECT " + COLUMNAS + " FROM puntos_asistencia WHERE id = ?";

        try (Connection conn = ConexionDB.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return convertir(rs);
                }
            }

        } catch (SQLException | IllegalArgumentException e) {
            System.err.println("Error DAO al obtener el punto de asistencia: " + e.getMessage());
        }
        return null;
    }

    // Método UPDATE: actualiza todos los datos del punto (usa su id)
    public boolean actualizarPunto(PuntoAsistencia punto) {
        if (!sePuedeGuardar(punto)) {
            return false;
        }
        String sql = "UPDATE puntos_asistencia SET tipo_punto = ?, nombre = ?, latitud = ?, "
                + "longitud = ?, estado = ?, servicios_disponibles = ? WHERE id = ?";

        try (Connection conn = ConexionDB.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            asignarDatos(pstmt, punto);
            pstmt.setInt(7, punto.getId());
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error DAO al actualizar el punto de asistencia: " + e.getMessage());
            return false;
        }
    }

    // Abrir o cerrar un punto sin tocar el resto de sus datos (RF-09)
    public boolean cambiarEstado(int id, EstadoPunto estado) {
        if (estado == null) {
            return false;
        }
        String sql = "UPDATE puntos_asistencia SET estado = ? WHERE id = ?";

        try (Connection conn = ConexionDB.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, estado.name());
            pstmt.setInt(2, id);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error DAO al cambiar el estado del punto: " + e.getMessage());
            return false;
        }
    }

    // Método DELETE: borra el punto de forma permanente
    public boolean eliminarPunto(int id) {
        String sql = "DELETE FROM puntos_asistencia WHERE id = ?";

        try (Connection conn = ConexionDB.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error DAO al eliminar el punto de asistencia: " + e.getMessage());
            return false;
        }
    }

    // Verifica antes de guardar lo que MySQL no puede validar por nosotros
    private boolean sePuedeGuardar(PuntoAsistencia punto) {
        if (punto == null) {
            System.err.println("Error DAO: el punto de asistencia es obligatorio.");
            return false;
        }
        if (!punto.getServicios().cabeEnColumna()) {
            System.err.println("Error DAO: los servicios superan los "
                    + ServiciosPunto.LIMITE_TEXTO + " caracteres permitidos.");
            return false;
        }
        return true;
    }

    // Parámetros 1 a 6, iguales para INSERT y UPDATE
    private void asignarDatos(PreparedStatement pstmt, PuntoAsistencia punto) throws SQLException {
        String servicios = punto.getServicios().aTexto();
        pstmt.setString(1, punto.getTipoPunto());
        pstmt.setString(2, punto.getNombre());
        pstmt.setDouble(3, punto.getLatitud());
        pstmt.setDouble(4, punto.getLongitud());
        pstmt.setString(5, punto.getEstado().name());
        pstmt.setString(6, servicios.isEmpty() ? null : servicios);
    }

    // Convierte una fila de la tabla en el objeto de la subclase que corresponde
    private PuntoAsistencia convertir(ResultSet rs) throws SQLException {
        return PuntoAsistencia.crear(
                rs.getString("tipo_punto"),
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getDouble("latitud"),
                rs.getDouble("longitud"),
                EstadoPunto.desdeTexto(rs.getString("estado")),
                ServiciosPunto.desdeTexto(rs.getString("servicios_disponibles")));
    }
}