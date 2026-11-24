package models.dao;
import models.TipoVoto;
import models.ValidacionReporte;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Persistencia de la validación comunitaria (RF-13).
 *
 * La tabla se crea automáticamente la primera vez que se usa, gracias a
 * {@link BaseDAO#asegurarTabla(Connection)}. La llave única
 * (reporte_id, usuario_id) es la garantía definitiva contra votos duplicados.
 *
 * @author santi
 */
public class ValidacionDAO extends BaseDAO {
 @Override
 protected String getNombreTabla() {
 return "validaciones_reporte";
 }
 @Override
 protected String getSqlCrearTabla() {
 return "CREATE TABLE IF NOT EXISTS validaciones_reporte ("
 + "id INT AUTO_INCREMENT PRIMARY KEY, "
 + "reporte_id INT NOT NULL, "
 + "usuario_id INT NOT NULL, "
 + "voto VARCHAR(20) NOT NULL, "
 + "fecha TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, "
 + "UNIQUE KEY uk_voto_usuario (reporte_id, usuario_id), "
 + "CONSTRAINT fk_val_reporte FOREIGN KEY (reporte_id) "
 + "REFERENCES reportes_incidentes(id) ON DELETE CASCADE, "
 + "CONSTRAINT fk_val_usuario FOREIGN KEY (usuario_id) "
 + "REFERENCES usuarios(id) ON DELETE CASCADE"
 + ") ENGINE=InnoDB DEFAULT CHARSET=utf8";
 }
 /**
 * Registra un voto. Devuelve false si el usuario ya había votado ese reporte
 * o si ocurre un error de base de datos.
 */
 public boolean registrarVoto(int reporteId, int usuarioId, TipoVoto voto) {
 if (yaVoto(reporteId, usuarioId)) {
 return false;
 }
 String sql = "INSERT INTO validaciones_reporte (reporte_id, usuario_id, voto) VALUES (?, ?, ?)";
 try {
 Connection conn = conexion();
 asegurarTabla(conn);
 try (PreparedStatement ps = conn.prepareStatement(sql)) {
 ps.setInt(1, reporteId);
 ps.setInt(2, usuarioId);
 ps.setString(3, voto.getCodigo());
 return ps.executeUpdate() > 0;
 }
 } catch (SQLException e) {
 // 1062 = entrada duplicada. Puede ocurrir por dos clics simultáneos.
 if (e.getErrorCode() == 1062) {
 return false;
 }
 System.err.println("Error DAO al registrar voto: " + e.getMessage());
 return false;
 }
 }
 /** Indica si un usuario ya emitió un voto para ese reporte. */
 public boolean yaVoto(int reporteId, int usuarioId) {
 String sql = "SELECT 1 FROM validaciones_reporte WHERE reporte_id = ? AND usuario_id = ?";
 try {
 Connection conn = conexion();
 asegurarTabla(conn);
 try (PreparedStatement ps = conn.prepareStatement(sql)) {
 ps.setInt(1, reporteId);
 ps.setInt(2, usuarioId);
 try (ResultSet rs = ps.executeQuery()) {
 return rs.next();
 }
 }
 } catch (SQLException e) {
 System.err.println("Error DAO al verificar voto: " + e.getMessage());
 return false;
 }
 }
 /** Devuelve el voto del usuario o null si todavía no ha votado. */
 public TipoVoto votoDeUsuario(int reporteId, int usuarioId) {
 String sql = "SELECT voto FROM validaciones_reporte WHERE reporte_id = ? AND usuario_id = ?";
 try {
 Connection conn = conexion();
 asegurarTabla(conn);
 try (PreparedStatement ps = conn.prepareStatement(sql)) {
 ps.setInt(1, reporteId);
 ps.setInt(2, usuarioId);
 try (ResultSet rs = ps.executeQuery()) {
 if (rs.next()) {
 try {
 return TipoVoto.valueOf(rs.getString("voto"));
 } catch (IllegalArgumentException ex) {
 return null;
 }
 }
 }
 }
 } catch (SQLException e) {
 System.err.println("Error DAO al obtener voto: " + e.getMessage());
 }
 return null;
 }
 /** Cuenta los votos de un tipo para un reporte. */
 public int contarVotos(int reporteId, TipoVoto voto) {
 String sql = "SELECT COUNT(*) FROM validaciones_reporte WHERE reporte_id = ? AND voto = ?";
 try {
 Connection conn = conexion();
 asegurarTabla(conn);
 try (PreparedStatement ps = conn.prepareStatement(sql)) {
 ps.setInt(1, reporteId);
 ps.setString(2, voto.getCodigo());
 try (ResultSet rs = ps.executeQuery()) {
 return rs.next() ? rs.getInt(1) : 0;
 }
 }
 } catch (SQLException e) {
 System.err.println("Error DAO al contar votos: " + e.getMessage());
 return 0;
 }
 }
 /**
 * Cuenta confirmaciones y cuestionamientos de varios reportes en una sola consulta.
 * El arreglo devuelto tiene la forma {confirmaciones, cuestionamientos}.
 */
 public Map<Integer, int[]> contarVotos(List<Integer> ids) {
 Map<Integer, int[]> resultado = new HashMap<>();
 if (ids == null || ids.isEmpty()) {
 return resultado;
 }
 for (Integer id : ids) {
 resultado.put(id, new int[]{0, 0});
 }
 StringBuilder placeholders = new StringBuilder();
 for (int i = 0; i < ids.size(); i++) {
 if (i > 0) {
 placeholders.append(",");
 }
 placeholders.append("?");
 }
 String sql = "SELECT reporte_id, voto, COUNT(*) AS c FROM validaciones_reporte "
 + "WHERE reporte_id IN (" + placeholders + ") GROUP BY reporte_id, voto";
 try {
 Connection conn = conexion();
 asegurarTabla(conn);
 try (PreparedStatement ps = conn.prepareStatement(sql)) {
 for (int i = 0; i < ids.size(); i++) {
 ps.setInt(i + 1, ids.get(i));
 }
 try (ResultSet rs = ps.executeQuery()) {
 while (rs.next()) {
 int rid = rs.getInt("reporte_id");
 String voto = rs.getString("voto");
 int cantidad = rs.getInt("c");
 int[] contadores = resultado.get(rid);
 if (contadores == null) {
 contadores = new int[]{0, 0};
 resultado.put(rid, contadores);
 }
 if (TipoVoto.CONFIRMA.getCodigo().equals(voto)) {
 contadores[0] += cantidad;
 } else if (TipoVoto.CUESTIONA.getCodigo().equals(voto)) {
 contadores[1] += cantidad;
 }
 }
 }
 }
 } catch (SQLException e) {
 System.err.println("Error DAO al contar votos agrupados: " + e.getMessage());
 }
 return resultado;
 }
 /** Devuelve el id del usuario que creó el reporte (0 si no existe). */
 public int obtenerUsuarioIdReporte(int reporteId) {
 String sql = "SELECT usuario_id FROM reportes_incidentes WHERE id = ?";
 try {
 Connection conn = conexion();
 try (PreparedStatement ps = conn.prepareStatement(sql)) {
 ps.setInt(1, reporteId);
 try (ResultSet rs = ps.executeQuery()) {
 return rs.next() ? rs.getInt("usuario_id") : 0;
 }
 }
 } catch (SQLException e) {
 System.err.println("Error DAO al obtener propietario del reporte: " + e.getMessage());
 return 0;
 }
 }
 /**
 * Devuelve el origen del reporte (CIUDADANO u OFICIAL).
 * Si la columna todavía no existe en una base antigua, se asume CIUDADANO.
 */
 public String obtenerOrigenReporte(int reporteId) {
 String sql = "SELECT origen_reporte FROM reportes_incidentes WHERE id = ?";
 try {
 Connection conn = conexion();
 try (PreparedStatement ps = conn.prepareStatement(sql)) {
 ps.setInt(1, reporteId);
 try (ResultSet rs = ps.executeQuery()) {
 return rs.next() ? rs.getString("origen_reporte") : "CIUDADANO";
 }
 }
 } catch (SQLException e) {
 return "CIUDADANO";
 }
 }
 /** Lista los votos de un reporte, útil para auditoría. */
 public List<ValidacionReporte> listarPorReporte(int reporteId) {
 List<ValidacionReporte> lista = new ArrayList<>();
 String sql = "SELECT id, reporte_id, usuario_id, voto, fecha "
 + "FROM validaciones_reporte WHERE reporte_id = ? ORDER BY fecha";
 try {
 Connection conn = conexion();
 asegurarTabla(conn);
 try (PreparedStatement ps = conn.prepareStatement(sql)) {
 ps.setInt(1, reporteId);
 try (ResultSet rs = ps.executeQuery()) {
 while (rs.next()) {
 TipoVoto voto;
 try {
 voto = TipoVoto.valueOf(rs.getString("voto"));
 } catch (IllegalArgumentException ex) {
 voto = null;
 }
 lista.add(new ValidacionReporte(
 rs.getInt("id"),
 rs.getInt("reporte_id"),
 rs.getInt("usuario_id"),
 voto,
 rs.getTimestamp("fecha")));
 }
 }
 }
 } catch (SQLException e) {
 System.err.println("Error DAO al listar validaciones: " + e.getMessage());
 }
 return lista;
 }
}