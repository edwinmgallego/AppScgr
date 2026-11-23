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

    @Test
    @DisplayName("obtenerTodos: nunca es null e incluye el reporte recién creado")
    void obtenerTodos() {
        ReporteExtraviado r = registrar(TipoReporte.MASCOTA, EstadoReporte.PERDIDO, "loro verde que habla");

        List<ReporteExtraviado> todos = dao.obtenerTodos();

        assertNotNull(todos);
        assertTrue(contieneId(todos, r.getId()));
    }

    @Test
    @DisplayName("obtenerPorId: devuelve el reporte con los mismos datos")
    void obtenerPorIdExistente() {
        ReporteExtraviado r = registrar(TipoReporte.PERSONA, EstadoReporte.PERDIDO, "niña de 8 años, vestido amarillo");

        ReporteExtraviado leido = dao.obtenerPorId(r.getId());

        assertNotNull(leido);
        assertEquals(r.getId(), leido.getId());
        assertEquals(TipoReporte.PERSONA, leido.getTipo());
        assertEquals(r.getNombre(), leido.getNombre());
        assertEquals("niña de 8 años, vestido amarillo", leido.getCaracteristicas());
        assertEquals(EstadoReporte.PERDIDO, leido.getEstado());
        assertNotNull(leido.getFechaReporte(), "MySQL debe poner la fecha automáticamente");
    }

    @Test
    @DisplayName("obtenerPorId: devuelve null si el id no existe")
    void obtenerPorIdInexistente() {
        assertNull(dao.obtenerPorId(Integer.MAX_VALUE));
        assertNull(dao.obtenerPorId(-1));
    }

    @Test
    @DisplayName("actualizarReporte: pasa de PERDIDO a ENCONTRADO y se guarda")
    void actualizarPerdidoAEncontrado() {
        ReporteExtraviado r = registrar(TipoReporte.MASCOTA, EstadoReporte.PERDIDO, "perro beagle con collar azul");

        r.setEstado(EstadoReporte.ENCONTRADO);
        r.setCaracteristicas("perro beagle con collar azul, encontrado en el parque");
        assertTrue(dao.actualizarReporte(r));

        // Releer de la BD para confirmar que el cambio quedó guardado
        ReporteExtraviado leido = dao.obtenerPorId(r.getId());
        assertEquals(EstadoReporte.ENCONTRADO, leido.getEstado());
        assertEquals("perro beagle con collar azul, encontrado en el parque", leido.getCaracteristicas());
        assertEquals(ID_USUARIO, leido.getUsuarioId(), "Actualizar no debe cambiar el usuario");
    }

    @Test
    @DisplayName("actualizarReporte: devuelve false con id inválido o inexistente")
    void actualizarInvalido() {
        ReporteExtraviado idCero = new ReporteExtraviado(TipoReporte.MASCOTA, nombreUnico(), "algo", EstadoReporte.PERDIDO, null, null);

        ReporteExtraviado inexistente = new ReporteExtraviado(TipoReporte.MASCOTA, nombreUnico(), "algo", EstadoReporte.PERDIDO, null, null);
        inexistente.setId(Integer.MAX_VALUE);

        assertFalse(dao.actualizarReporte(idCero));
        assertFalse(dao.actualizarReporte(inexistente));
    }

    @Test
    @DisplayName("eliminarReporte: borra el reporte y después ya no se encuentra")
    void eliminarReporte() {
        ReporteExtraviado r = registrar(TipoReporte.MASCOTA, EstadoReporte.ENCONTRADO, "conejo blanco");

        assertTrue(dao.eliminarReporte(r.getId()));
        assertNull(dao.obtenerPorId(r.getId()));
    }

    @Test
    @DisplayName("eliminarReporte: devuelve false si el id no existe o es <= 0")
    void eliminarInexistente() {
        assertFalse(dao.eliminarReporte(Integer.MAX_VALUE));
        assertFalse(dao.eliminarReporte(0));
        assertFalse(dao.eliminarReporte(-5));
    }
}
