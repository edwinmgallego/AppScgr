package models.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import models.EstadoReporte;
import models.ReporteExtraviado;
import models.TipoReporte;

/**
 * DAO del tablón de personas y mascotas extraviadas (RF-11 y RF-12).
 * Trabaja sobre la tabla original `reportes_extraviados` (la base de datos no se modifica).
 *
 * @author Santiago Sepulveda - Equipo 8
 */
public class ReporteExtraviadoDAO extends BaseDAO {

    // Columnas en el mismo orden que las lee convertir(rs)
    private static final String COLUMNAS =
            "id, usuario_id, tipo, nombre, caracteristicas, estado, latitud_visto, longitud_visto, fecha_reporte";

    @Override
    protected String getNombreTabla() {
        return "reportes_extraviados";
    }
    // No sobrescribimos getSqlCrearTabla(): la tabla ya existe y no se toca

    // Método CREATE: guarda el reporte y le asigna el id que generó MySQL
    public boolean registrarReporte(ReporteExtraviado reporte, int idUsuarioLogueado) {
        if (!sePuedeGuardar(reporte)) {
            return false;
        }
        String sql = "INSERT INTO reportes_extraviados "
                + "(usuario_id, tipo, nombre, caracteristicas, estado, latitud_visto, longitud_visto) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try {
            Connection conn = conexion();
            try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

                pstmt.setInt(1, idUsuarioLogueado);
                asignarDatos(pstmt, reporte, 2);

                if (pstmt.executeUpdate() == 0) {
                    return false;
                }
                // Le pedimos a MySQL el id que acaba de crear y se lo ponemos al objeto
                try (ResultSet llaves = pstmt.getGeneratedKeys()) {
                    if (llaves.next()) {
                        reporte.setId(llaves.getInt(1));
                    }
                }
                reporte.setUsuarioId(idUsuarioLogueado);
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error DAO al registrar el reporte extraviado: " + e.getMessage());
            return false;
        }
    }

    // Método READ: todos los reportes, del más reciente al más antiguo
    public List<ReporteExtraviado> obtenerTodos() {
        List<ReporteExtraviado> lista = new ArrayList<>();
        String sql = "SELECT " + COLUMNAS + " FROM reportes_extraviados ORDER BY fecha_reporte DESC, id DESC";

        try {
            Connection conn = conexion();
            try (PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {
                    lista.add(convertir(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error DAO al obtener los reportes extraviados: " + e.getMessage());
        }
        return lista;
    }

    // Método READ: un reporte por su id (null si no existe)
    public ReporteExtraviado obtenerPorId(int id) {
        String sql = "SELECT " + COLUMNAS + " FROM reportes_extraviados WHERE id = ?";

        try {
            Connection conn = conexion();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return convertir(rs);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error DAO al obtener el reporte extraviado: " + e.getMessage());
        }
        return null;
    }

    // ----------------- Métodos de apoyo (privados) -----------------

    // Valida lo mismo que exige la tabla: tipo, nombre y caracteristicas son NOT NULL
    private boolean sePuedeGuardar(ReporteExtraviado reporte) {
        return reporte != null
                && reporte.getTipo() != null
                && reporte.getNombre() != null && !reporte.getNombre().isBlank()
                && reporte.getNombre().trim().length() <= 100
                && reporte.getCaracteristicas() != null && !reporte.getCaracteristicas().isBlank();
    }

    // Llena los ? desde la posición "inicio" (lo reutilizaremos en el UPDATE)
    private void asignarDatos(PreparedStatement pstmt, ReporteExtraviado reporte, int inicio) throws SQLException {
        EstadoReporte estado = reporte.getEstado() != null ? reporte.getEstado() : EstadoReporte.PERDIDO;

        pstmt.setString(inicio, reporte.getTipo().name());
        pstmt.setString(inicio + 1, reporte.getNombre().trim());
        pstmt.setString(inicio + 2, reporte.getCaracteristicas().trim());
        pstmt.setString(inicio + 3, estado.name());
        asignarDoubleONull(pstmt, inicio + 4, reporte.getLatitudVisto());
        asignarDoubleONull(pstmt, inicio + 5, reporte.getLongitudVisto());
    }

    // Si no hay coordenada guardamos NULL en vez de 0.0
    private void asignarDoubleONull(PreparedStatement pstmt, int posicion, Double valor) throws SQLException {
        if (valor == null) {
            pstmt.setNull(posicion, Types.DECIMAL);
        } else {
            pstmt.setDouble(posicion, valor);
        }
    }

    // Lee un decimal que puede ser NULL (getDouble devolvería 0.0)
    private Double leerDoubleONull(ResultSet rs, String columna) throws SQLException {
        double valor = rs.getDouble(columna);
        return rs.wasNull() ? null : valor;
    }

    // Convierte la fila actual del ResultSet en un objeto ReporteExtraviado
    private ReporteExtraviado convertir(ResultSet rs) throws SQLException {
        return new ReporteExtraviado(
                rs.getInt("id"),
                rs.getInt("usuario_id"),
                TipoReporte.desdeTexto(rs.getString("tipo")),
                rs.getString("nombre"),
                rs.getString("caracteristicas"),
                EstadoReporte.desdeTexto(rs.getString("estado")),
                leerDoubleONull(rs, "latitud_visto"),
                leerDoubleONull(rs, "longitud_visto"),
                rs.getTimestamp("fecha_reporte"));
    }
}
