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
 * Pruebas de integración de ReporteExtraviadoDAO (Grupo 8, RF-11 y RF-12).
 * Usan la base de datos REAL: necesitan XAMPP con MySQL encendido.
 * Cada reporte creado se llama "PRUEBA_..." y se borra en @AfterEach.
 */
public class ReporteExtraviadoDAOTest {

    private ReporteExtraviadoDAO dao;
    private List<Integer> idsCreados;

    /** Usuario de prueba (existe el usuario 1 en la tabla usuarios). */
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

    /** Nombre único para no chocar con datos reales. */
    private String nombreUnico() {
        return "PRUEBA_" + System.nanoTime();
    }

    /** Crea y registra un reporte, verifica que salió bien y guarda su id. */
    private ReporteExtraviado registrar(TipoReporte tipo, EstadoReporte estado, String caracteristicas) {
        ReporteExtraviado r = new ReporteExtraviado(tipo, nombreUnico(), caracteristicas, estado, null, null);
        assertTrue(dao.registrarReporte(r, ID_USUARIO), "No se pudo registrar el reporte. ¿XAMPP está encendido?");
        idsCreados.add(r.getId());
        return r;
    }

    /** Dice si en la lista hay un reporte con ese id. */
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
}