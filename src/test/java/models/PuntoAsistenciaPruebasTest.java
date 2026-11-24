/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Titoelgato
 */
public class PuntoAsistenciaPruebasTest {
    
    @Test
    public void testCadaSubclaseDefineSuTipo() {
        assertEquals("ALBERGUE", new Albergue("A", 3.45, -76.53).getTipoPunto());
        assertEquals("SUPERMERCADO", new Supermercado("S", 3.45, -76.53).getTipoPunto());
        assertEquals("MECANICO", new Mecanico("M", 3.45, -76.53).getTipoPunto());
        assertEquals("HOSPITAL", new Hospital("H", 3.45, -76.53).getTipoPunto());
        assertEquals("ALBERGUE", new CentroAcopio("C", 3.45, -76.53).getTipoPunto());
    }
    @Test
    public void testPolimorfismoDeEtiquetasYServiciosSugeridos() {
        PuntoAsistencia[] puntos = {
            new Albergue("A", 3.45, -76.53),
            new Supermercado("S", 3.45, -76.53),
            new Mecanico("M", 3.45, -76.53),
            new CentroAcopio("C", 3.45, -76.53)
        };
        for (PuntoAsistencia punto : puntos) {
            assertFalse(punto.getEtiquetaTipo().isBlank());
            assertFalse(punto.getServiciosSugeridos().isEmpty());
        }
        assertEquals("Centro de acopio", puntos[3].getEtiquetaTipo());
    }
    @Test
    public void testPuntoNuevoEmpiezaAbiertoYSinId() {
        PuntoAsistencia punto = new Albergue("Colegio Central", 3.4516, -76.5320);

        assertEquals(0, punto.getId());
        assertEquals(EstadoPunto.ABIERTO, punto.getEstado());
        assertTrue(punto.isAbierto());
        assertTrue(punto.getServicios().getServicios().isEmpty());
    }

    @Test
    public void testImplementaGeolocalizable() {
        Geolocalizable ubicable = new Supermercado("Mercado", 3.4516, -76.5320);

        assertEquals(3.4516, ubicable.getLatitud(), 0.0000001);
        assertEquals(-76.5320, ubicable.getLongitud(), 0.0000001);
    }

    @Test
    public void testValidacionesDelNombre() {
        assertThrows(IllegalArgumentException.class, () -> new Albergue(null, 3.4, -76.5));
        assertThrows(IllegalArgumentException.class, () -> new Albergue("   ", 3.4, -76.5));
        assertThrows(IllegalArgumentException.class,
                () -> new Albergue("x".repeat(PuntoAsistencia.MAX_NOMBRE + 1), 3.4, -76.5));
        assertEquals("Colegio", new Albergue("  Colegio  ", 3.4, -76.5).getNombre());
    }
    @Test
    public void testValidacionesDeCoordenadas() {
        assertThrows(IllegalArgumentException.class, () -> new Albergue("A", 91, -76.5));
        assertThrows(IllegalArgumentException.class, () -> new Albergue("A", -91, -76.5));
        assertThrows(IllegalArgumentException.class, () -> new Albergue("A", 3.4, 181));
        assertThrows(IllegalArgumentException.class, () -> new Albergue("A", Double.NaN, -76.5));
    }

    @Test
    public void testEstadoYServiciosSonObligatorios() {
        PuntoAsistencia punto = new Mecanico("Taller", 3.4, -76.5);

        assertThrows(IllegalArgumentException.class, () -> punto.setEstado(null));
        assertThrows(IllegalArgumentException.class, () -> punto.setServicios(null));
        punto.setEstado(EstadoPunto.CERRADO);
        assertFalse(punto.isAbierto());
    }

    @Test
    public void testCentroAcopioSiempreLlevaLaMarcaAcopio() {
        CentroAcopio centro = new CentroAcopio("Acopio Norte", 3.4, -76.5);
        assertTrue(centro.getServicios().contiene(ServiciosPunto.ACOPIO));

        // aunque se reemplacen los servicios (por ejemplo desde un formulario)
        centro.setServicios(new ServiciosPunto());
        assertTrue(centro.getServicios().contiene(ServiciosPunto.ACOPIO));
    }

    @Test
    public void testCrearElijeLaSubclaseSegunElTipoDeLaBD() {
        PuntoAsistencia albergue = PuntoAsistencia.crear("ALBERGUE", 1, "A", 3.4, -76.5,
                EstadoPunto.ABIERTO, new ServiciosPunto());
        PuntoAsistencia supermercado = PuntoAsistencia.crear("supermercado", 2, "S", 3.4, -76.5,
                EstadoPunto.CERRADO, null);
        PuntoAsistencia mecanico = PuntoAsistencia.crear("MECANICO", 3, "M", 3.4, -76.5,
                EstadoPunto.ABIERTO, null);
        PuntoAsistencia hospital = PuntoAsistencia.crear("HOSPITAL", 4, "H", 3.4, -76.5,
                EstadoPunto.ABIERTO, null);

        assertInstanceOf(Albergue.class, albergue);
        assertInstanceOf(Supermercado.class, supermercado);
        assertInstanceOf(Mecanico.class, mecanico);
        assertInstanceOf(Hospital.class, hospital);
        assertEquals(1, albergue.getId());
        assertEquals(EstadoPunto.CERRADO, supermercado.getEstado());
    }

    @Test
    public void testCrearReconoceUnCentroDeAcopioPorSuMarca() {
        PuntoAsistencia punto = PuntoAsistencia.crear("ALBERGUE", 5, "Acopio", 3.4, -76.5,
                EstadoPunto.ABIERTO, ServiciosPunto.desdeTexto("ACOPIO,AGUA;VOLUNTARIOS=4"));

        assertInstanceOf(CentroAcopio.class, punto);
        assertEquals(4, punto.getVoluntariosNecesarios());
    }

    @Test
    public void testCrearRechazaTiposDesconocidos() {
        assertThrows(IllegalArgumentException.class,
                () -> PuntoAsistencia.crear("FARMACIA", 1, "F", 3.4, -76.5, EstadoPunto.ABIERTO, null));
        assertThrows(IllegalArgumentException.class,
                () -> PuntoAsistencia.crear(null, 1, "F", 3.4, -76.5, EstadoPunto.ABIERTO, null));
    }

    @Test
    public void testEstadoDesdeTexto() {
        assertEquals(EstadoPunto.ABIERTO, EstadoPunto.desdeTexto(null));
        assertEquals(EstadoPunto.CERRADO, EstadoPunto.desdeTexto("cerrado"));
        assertThrows(IllegalArgumentException.class, () -> EstadoPunto.desdeTexto("OTRO"));
    }
}

