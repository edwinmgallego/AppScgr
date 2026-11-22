package models.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import models.EstadoReporte;
import models.ReporteExtraviado;
import models.TipoReporte;

/**
 * DAO del tablón de personas y mascotas extraviadas (RF-11 y RF-12).
 * Trabaja sobre la tabla original `reportes_extraviados` (la base de datos no se modifica).
 *
 * @author User
 */
public class ReporteExtraviadoDAO extends BaseDAO {

    // Columnas en el mismo orden que las lee convertir(rs)
    private static final String COLUMNAS =
            "id, usuario_id, tipo, nombre, caracteristicas, estado, latitud_visto, longitud_visto, fecha_reporte";

    // Palabras que no describen nada y no deben contar como coincidencia
    private static final Set<String> PALABRAS_VACIAS = Set.of(
            "con", "sin", "del", "los", "las", "una", "unos", "unas", "que", "por", "para",
            "muy", "tiene", "lleva", "llevaba", "esta", "estaba", "color", "como", "mas");

    // Cuántas palabras deben compartir dos reportes para considerarse posible coincidencia
    private static final int MINIMO_PALABRAS_COMUNES = 2;

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

    // Método UPDATE: actualiza los datos del reporte (ej. pasar de PERDIDO a ENCONTRADO)
    // No cambia usuario_id ni fecha_reporte: quién lo creó y cuándo no debe cambiar
    public boolean actualizarReporte(ReporteExtraviado reporte) {
        if (!sePuedeGuardar(reporte) || reporte.getId() <= 0) {
            return false;
        }
        String sql = "UPDATE reportes_extraviados SET tipo = ?, nombre = ?, caracteristicas = ?, "
                + "estado = ?, latitud_visto = ?, longitud_visto = ? WHERE id = ?";

        try {
            Connection conn = conexion();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

                asignarDatos(pstmt, reporte, 1);   // llena los ? del 1 al 6
                pstmt.setInt(7, reporte.getId());   // el ? del WHERE

                return pstmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error DAO al actualizar el reporte extraviado: " + e.getMessage());
            return false;
        }
    }

    // Método DELETE FÍSICO: borra el reporte de la base de datos
    public boolean eliminarReporte(int id) {
        if (id <= 0) {
            return false;
        }
        String sql = "DELETE FROM reportes_extraviados WHERE id = ?";

        try {
            Connection conn = conexion();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                return pstmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error DAO al eliminar el reporte extraviado: " + e.getMessage());
            return false;
        }
    }

    // Búsqueda con filtros OPCIONALES: si un filtro es null (o el texto está vacío) no se aplica.
    // Ej: buscarPorFiltros(TipoReporte.MASCOTA, null, "collar") = mascotas (perdidas o encontradas) con "collar"
    public List<ReporteExtraviado> buscarPorFiltros(TipoReporte tipo, EstadoReporte estado, String texto) {
        List<ReporteExtraviado> lista = new ArrayList<>();

        // Armamos el SQL según los filtros que lleguen; los valores van aparte para usarlos con ?
        StringBuilder sql = new StringBuilder("SELECT " + COLUMNAS + " FROM reportes_extraviados WHERE 1 = 1");
        List<String> valores = new ArrayList<>();

        if (tipo != null) {
            sql.append(" AND tipo = ?");
            valores.add(tipo.name());
        }
        if (estado != null) {
            sql.append(" AND estado = ?");
            valores.add(estado.name());
        }
        if (texto != null && !texto.isBlank()) {
            sql.append(" AND (nombre LIKE ? OR caracteristicas LIKE ?)");
            String patron = "%" + texto.trim() + "%";
            valores.add(patron);
            valores.add(patron);
        }
        sql.append(" ORDER BY fecha_reporte DESC, id DESC");

        try {
            Connection conn = conexion();
            try (PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

                for (int i = 0; i < valores.size(); i++) {
                    pstmt.setString(i + 1, valores.get(i));   // los ? empiezan en 1
                }
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        lista.add(convertir(rs));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error DAO al filtrar los reportes extraviados: " + e.getMessage());
        }
        return lista;
    }

    // CRUCE DE REPORTES (RF-12): dado un reporte, busca los del MISMO tipo y estado CONTRARIO
    // (si es PERDIDO busca ENCONTRADOS y viceversa) cuyas características se parezcan.
    // Devuelve primero los que comparten más palabras.
    public List<ReporteExtraviado> buscarCoincidencias(ReporteExtraviado reporte) {
        List<ReporteExtraviado> coincidencias = new ArrayList<>();
        if (reporte == null || reporte.getTipo() == null || reporte.getCaracteristicas() == null) {
            return coincidencias;
        }

        Set<String> palabrasReporte = palabrasClave(reporte.getCaracteristicas());
        if (palabrasReporte.isEmpty()) {
            return coincidencias;
        }
        // Si el reporte tiene una sola palabra clave (ej. "chihuahua"), basta con que coincida esa
        int minimo = Math.min(MINIMO_PALABRAS_COMUNES, palabrasReporte.size());

        EstadoReporte estadoBuscado = (reporte.getEstado() == EstadoReporte.ENCONTRADO)
                ? EstadoReporte.PERDIDO
                : EstadoReporte.ENCONTRADO;

        // Reutilizamos el filtro: trae solo los del mismo tipo y estado contrario
        List<ReporteExtraviado> candidatos = buscarPorFiltros(reporte.getTipo(), estadoBuscado, null);

        Map<Integer, Integer> puntajePorId = new HashMap<>();
        for (ReporteExtraviado candidato : candidatos) {
            if (candidato.getId() == reporte.getId()) {
                continue;   // no compararse consigo mismo
            }
            int comunes = contarComunes(palabrasReporte, palabrasClave(candidato.getCaracteristicas()));
            if (comunes >= minimo) {
                coincidencias.add(candidato);
                puntajePorId.put(candidato.getId(), comunes);
            }
        }

        // Ordenamos de mayor a menor número de palabras en común
        coincidencias.sort((a, b) -> Integer.compare(puntajePorId.get(b.getId()), puntajePorId.get(a.getId())));
        return coincidencias;
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

    // "Perro CAFÉ, collar rojo" -> {perro, cafe, collar, rojo}
    // Quita tildes, pasa a minúsculas, separa por cualquier signo y descarta palabras cortas o vacías
    private Set<String> palabrasClave(String texto) {
        Set<String> palabras = new HashSet<>();
        if (texto == null) {
            return palabras;
        }
        String limpio = Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")          // quita las tildes (á -> a)
                .toLowerCase(Locale.ROOT);
        for (String palabra : limpio.split("[^a-z0-9]+")) {
            if (palabra.length() >= 3 && !PALABRAS_VACIAS.contains(palabra)) {
                palabras.add(palabra);
            }
        }
        return palabras;
    }

    // Cuántas palabras aparecen en los dos conjuntos
    private int contarComunes(Set<String> a, Set<String> b) {
        int comunes = 0;
        for (String palabra : a) {
            if (b.contains(palabra)) {
                comunes++;
            }
        }
        return comunes;
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
