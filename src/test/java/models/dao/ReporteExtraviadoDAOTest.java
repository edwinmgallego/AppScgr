package models.dao;

import models.EstadoReporte;
import models.ReporteExtraviado;
import models.TipoReporte;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas de integración de ReporteExtraviadoDAO (Grupo 8, RF-11 y RF-12). Usan
 * la base de datos REAL: necesitan XAMPP con MySQL encendido. Cada reporte
 * creado se llama "PRUEBA_..." y se borra en @AfterEach.
 */
public class ReporteExtraviadoDAOTest {

    private ReporteExtraviadoDAO dao;
    private List<Integer> idsCreados;

    /**
     * Usuario de prueba (existe el usuario 1 en la tabla usuarios).
     */
    private static final int ID_USUARIO = 1;

    @BeforeEach
    void setUp() {
        // Antes de cada prueba: DAO nuevo y lista de ids vacía
        dao = new ReporteExtraviadoDAO();
        idsCreados = new ArrayList<>();
    }

    @AfterEach
    void tearDown() {
        // Después de cada prueba (aunque falle): borra lo que se creó
        for (Integer id : idsCreados) {
            dao.eliminarReporte(id);
        }
    }

    /**
     * Nombre único para no chocar con datos reales.
     */
    private String nombreUnico() {
        return "PRUEBA_" + System.nanoTime();
    }

    /**
     * Crea y registra un reporte, verifica que salió bien y guarda su id.
     */
    private ReporteExtraviado registrar(TipoReporte tipo, EstadoReporte estado, String caracteristicas) {
        ReporteExtraviado r = new ReporteExtraviado(tipo, nombreUnico(), caracteristicas, estado, null, null);
        assertTrue(dao.registrarReporte(r, ID_USUARIO), "No se pudo registrar el reporte. ¿XAMPP está encendido?");
        idsCreados.add(r.getId());
        return r;
    }

    /**
     * Dice si en la lista hay un reporte con ese id.
     */
    private boolean contieneId(List<ReporteExtraviado> lista, int id) {
        for (ReporteExtraviado r : lista) {
            if (r.getId() == id) {
                return true;
            }
        }
        return false;
    }

    @Test
    @DisplayName("registrarReporte: guarda el reporte y le asigna el id generado")
    void registrarReporteValido() {
        ReporteExtraviado r = new ReporteExtraviado(TipoReporte.MASCOTA, nombreUnico(),
                "perro criollo cafe, collar rojo", EstadoReporte.PERDIDO, null, null);

        boolean resultado = dao.registrarReporte(r, ID_USUARIO);
        if (r.getId() > 0) {
            idsCreados.add(r.getId());
        }

        assertTrue(resultado);
        assertTrue(r.getId() > 0, "Después de registrar, el objeto debe tener el id de MySQL");
        assertEquals(ID_USUARIO, r.getUsuarioId());
    }

    @Test
    @DisplayName("registrarReporte: si el estado es null se guarda como PERDIDO")
    void registrarReporteEstadoNullQuedaPerdido() {
        ReporteExtraviado r = registrar(TipoReporte.PERSONA, null, "hombre adulto, chaqueta azul");

        ReporteExtraviado leido = dao.obtenerPorId(r.getId());

        assertNotNull(leido);
        assertEquals(EstadoReporte.PERDIDO, leido.getEstado());
    }

    @Test
    @DisplayName("registrarReporte: guarda bien las coordenadas y respeta null")
    void registrarReporteConYSinCoordenadas() {
        ReporteExtraviado conUbicacion = new ReporteExtraviado(TipoReporte.MASCOTA, nombreUnico(),
                "gata gris pequeña", EstadoReporte.PERDIDO, 3.4516, -76.5320);
        assertTrue(dao.registrarReporte(conUbicacion, ID_USUARIO));
        idsCreados.add(conUbicacion.getId());

        ReporteExtraviado sinUbicacion = registrar(TipoReporte.MASCOTA, EstadoReporte.PERDIDO, "gato negro");

        ReporteExtraviado leido1 = dao.obtenerPorId(conUbicacion.getId());
        ReporteExtraviado leido2 = dao.obtenerPorId(sinUbicacion.getId());

        assertEquals(3.4516, leido1.getLatitudVisto(), 0.000001);
        assertEquals(-76.5320, leido1.getLongitudVisto(), 0.000001);
        assertNull(leido2.getLatitudVisto());   // null = sin ubicación, NO 0.0
        assertNull(leido2.getLongitudVisto());
    }

    @Test
    @DisplayName("registrarReporte: rechaza datos inválidos")
    void registrarReporteInvalido() {
        ReporteExtraviado sinTipo = new ReporteExtraviado(null, nombreUnico(), "algo", EstadoReporte.PERDIDO, null, null);
        ReporteExtraviado nombreVacio = new ReporteExtraviado(TipoReporte.MASCOTA, "", "algo", EstadoReporte.PERDIDO, null, null);
        ReporteExtraviado nombreLargo = new ReporteExtraviado(TipoReporte.MASCOTA, "a".repeat(101), "algo", EstadoReporte.PERDIDO, null, null);
        ReporteExtraviado sinCaracteristicas = new ReporteExtraviado(TipoReporte.MASCOTA, nombreUnico(), "", EstadoReporte.PERDIDO, null, null);

        assertFalse(dao.registrarReporte(null, ID_USUARIO), "Reporte null");
        assertFalse(dao.registrarReporte(sinTipo, ID_USUARIO), "Tipo null");
        assertFalse(dao.registrarReporte(nombreVacio, ID_USUARIO), "Nombre vacío");
        assertFalse(dao.registrarReporte(nombreLargo, ID_USUARIO), "Nombre de más de 100 caracteres");
        assertFalse(dao.registrarReporte(sinCaracteristicas, ID_USUARIO), "Características vacías");

        // Por si alguno se guardó por error, que @AfterEach lo borre
        for (ReporteExtraviado r : List.of(sinTipo, nombreVacio, nombreLargo, sinCaracteristicas)) {
            if (r.getId() > 0) {
                idsCreados.add(r.getId());
            }
        }
    }
}
