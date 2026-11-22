/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models.dao;
import models.Albergue;
import models.CentroAcopio;
import models.EstadoPunto;
import models.Mecanico;
import models.PuntoAsistencia;
import models.ServiciosPunto;
import models.Supermercado;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
/**
 *
 * @author Titoelgato
 */
public class PuntoAsistenciaDAOTest {

    private PuntoAsistenciaDAO dao;
    private final List<Integer> idsCreados = new ArrayList<>();
    private String sufijo;

    @BeforeEach
    public void setUp() {
        dao = new PuntoAsistenciaDAO();
        sufijo = String.valueOf(System.nanoTime() % 100_000_000L);
    }

    @AfterEach
    public void tearDown() {
        for (int id : idsCreados) {
            dao.eliminarPunto(id);
        }
        idsCreados.clear();
    }

    // Registra el punto, lo anota para borrarlo al final y comprueba que recibió id
    private PuntoAsistencia guardar(PuntoAsistencia punto) {
        assertTrue(dao.registrarPunto(punto), "El punto debería registrarse en la base de datos.");
        assertTrue(punto.getId() > 0, "MySQL debería asignar un id al punto.");
        idsCreados.add(punto.getId());
        return punto;
    }

    @Test
    public void testRegistrarYLeerPorId() {
        ServiciosPunto servicios = new ServiciosPunto(List.of("AGUA", "INTERNET"), 6);
        PuntoAsistencia nuevo = guardar(new Albergue(0, "Albergue JUnit " + sufijo,
                3.45160000, -76.53200000, EstadoPunto.ABIERTO, servicios));

        PuntoAsistencia leido = dao.obtenerPorId(nuevo.getId());

        assertNotNull(leido);
        assertInstanceOf(Albergue.class, leido);
        assertEquals("Albergue JUnit " + sufijo, leido.getNombre());
        assertEquals(3.4516, leido.getLatitud(), 0.000001);
        assertEquals(-76.532, leido.getLongitud(), 0.000001);
        assertEquals(EstadoPunto.ABIERTO, leido.getEstado());
        assertEquals(List.of("AGUA", "INTERNET"), leido.getServicios().getServicios());
        assertEquals(6, leido.getVoluntariosNecesarios());
    }

    @Test
    public void testPuntoSinServiciosSeGuardaYSeLee() {
        PuntoAsistencia nuevo = guardar(new Supermercado("Super JUnit " + sufijo, 3.40, -76.50));

        PuntoAsistencia leido = dao.obtenerPorId(nuevo.getId());

        assertInstanceOf(Supermercado.class, leido);
        assertTrue(leido.getServicios().getServicios().isEmpty());
        assertEquals(0, leido.getVoluntariosNecesarios());
    }

    @Test
    public void testCentroDeAcopioSeGuardaComoAlbergueYSeLeeComoCentroDeAcopio() {
        PuntoAsistencia nuevo = guardar(new CentroAcopio("Acopio JUnit " + sufijo, 3.42, -76.51));

        PuntoAsistencia leido = dao.obtenerPorId(nuevo.getId());

        assertInstanceOf(CentroAcopio.class, leido);
        assertEquals("ALBERGUE", leido.getTipoPunto());
        assertTrue(leido.getServicios().contiene(ServiciosPunto.ACOPIO));
    }

    @Test
    public void testObtenerTodosIncluyeLosPuntosRegistrados() {
        PuntoAsistencia a = guardar(new Albergue("Albergue JUnit " + sufijo, 3.41, -76.51));
        PuntoAsistencia m = guardar(new Mecanico("Taller JUnit " + sufijo, 3.43, -76.52));

        List<PuntoAsistencia> todos = dao.obtenerTodosLosPuntos();

        assertNotNull(todos);
        assertTrue(todos.stream().anyMatch(p -> p.getId() == a.getId()));
        assertTrue(todos.stream().anyMatch(p -> p.getId() == m.getId() && p instanceof Mecanico));
    }

    @Test
    public void testActualizarPunto() {
        PuntoAsistencia punto = guardar(new Albergue("Albergue JUnit " + sufijo, 3.41, -76.51));

        punto.setNombre("Albergue JUnit editado " + sufijo);
        punto.setLatitud(3.50);
        punto.setServicios(new ServiciosPunto(List.of("ALIMENTACION"), 2));
        assertTrue(dao.actualizarPunto(punto));

        PuntoAsistencia leido = dao.obtenerPorId(punto.getId());
        assertEquals("Albergue JUnit editado " + sufijo, leido.getNombre());
        assertEquals(3.50, leido.getLatitud(), 0.000001);
        assertEquals(List.of("ALIMENTACION"), leido.getServicios().getServicios());
        assertEquals(2, leido.getVoluntariosNecesarios());
    }

    @Test
    public void testCambiarEstado() {
        PuntoAsistencia punto = guardar(new Supermercado("Super JUnit " + sufijo, 3.40, -76.50));

        assertTrue(dao.cambiarEstado(punto.getId(), EstadoPunto.CERRADO));
        assertEquals(EstadoPunto.CERRADO, dao.obtenerPorId(punto.getId()).getEstado());

        assertTrue(dao.cambiarEstado(punto.getId(), EstadoPunto.ABIERTO));
        assertEquals(EstadoPunto.ABIERTO, dao.obtenerPorId(punto.getId()).getEstado());
    }

    @Test
    public void testEliminarPunto() {
        PuntoAsistencia punto = guardar(new Mecanico("Taller JUnit " + sufijo, 3.43, -76.52));

        assertTrue(dao.eliminarPunto(punto.getId()));

        assertNull(dao.obtenerPorId(punto.getId()));
        assertFalse(dao.eliminarPunto(punto.getId()), "Borrar un punto que ya no existe devuelve false.");
    }

    @Test
    public void testObtenerPorIdInexistenteDevuelveNull() {
        assertNull(dao.obtenerPorId(-1));
    }

    @Test
    public void testNoGuardaServiciosQueNoCabenEnLaColumna() {
        ServiciosPunto demasiados = new ServiciosPunto();
        for (int i = 0; i < 40; i++) {
            demasiados.agregar("SERVICIO_" + i);
        }
        PuntoAsistencia punto = new Albergue(0, "Albergue JUnit " + sufijo, 3.41, -76.51,
                EstadoPunto.ABIERTO, demasiados);

        assertFalse(dao.registrarPunto(punto));
        assertEquals(0, punto.getId(), "No debería haberse guardado nada.");
    }

    @Test
    public void testRegistrarPuntoNuloDevuelveFalse() {
        assertFalse(dao.registrarPunto(null));
        assertFalse(dao.actualizarPunto(null));
        assertFalse(dao.cambiarEstado(1, null));
    }
}
