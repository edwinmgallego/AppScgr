package models;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Pruebas unitarias de PuntoAsistencia y sus subclases: creación por tipo,
 * validaciones de los datos y relación con la interfaz Geolocalizable.
 *
 * @author Equipo 7 - Puntos de Asistencia y Negocios
 */
public class PuntoAsistenciaTest {

    private PuntoAsistencia crear(String tipo) {
        return PuntoAsistencia.crear(tipo, 1, "Punto de prueba", 3.4516, -76.532,
                EstadoPunto.ABIERTO, null);
    }

    @Test
    void crearEligeLaSubclaseSegunElTipo() {
        assertInstanceOf(Albergue.class, crear("ALBERGUE"));
        assertInstanceOf(Supermercado.class, crear("SUPERMERCADO"));
        assertInstanceOf(Mecanico.class, crear("MECANICO"));
        assertInstanceOf(Hospital.class, crear("HOSPITAL"));
    }

    @Test
    void crearIgnoraMayusculasYEspacios() {
        assertInstanceOf(Hospital.class, crear("  hospital "));
        assertInstanceOf(Albergue.class, crear("albergue"));
    }

    @Test
    void crearAlbergueConMarcaAcopioDevuelveCentroAcopio() {
        ServiciosPunto servicios = new ServiciosPunto(List.of("AGUA", ServiciosPunto.ACOPIO), 0);
        PuntoAsistencia punto = PuntoAsistencia.crear("ALBERGUE", 1, "Acopio Norte", 3.4, -76.5,
                EstadoPunto.ABIERTO, servicios);
        assertInstanceOf(CentroAcopio.class, punto);
    }

    @Test
    void centroAcopioSeGuardaComoAlbergueYLlevaLaMarca() {
        CentroAcopio acopio = new CentroAcopio("Acopio Norte", 3.4, -76.5);
        assertEquals("ALBERGUE", acopio.getTipoPunto());
        assertEquals("Centro de acopio", acopio.getEtiquetaTipo());
        assertTrue(acopio.getServicios().contiene(ServiciosPunto.ACOPIO));
    }

    @Test
    void albergueNormalNoTieneLaMarcaAcopio() {
        Albergue albergue = new Albergue("Refugio Sur", 3.4, -76.5);
        assertEquals("ALBERGUE", albergue.getTipoPunto());
        assertEquals("Albergue", albergue.getEtiquetaTipo());
        assertFalse(albergue.getServicios().contiene(ServiciosPunto.ACOPIO));
    }

    @Test
    void crearConTipoNuloVacioODesconocidoLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> crear(null));
        assertThrows(IllegalArgumentException.class, () -> crear("   "));
        assertThrows(IllegalArgumentException.class, () -> crear("BANCO"));
    }

    @Test
    void getTipoPuntoCoincideConLosValoresDeLaBase() {
        assertEquals("SUPERMERCADO", crear("SUPERMERCADO").getTipoPunto());
        assertEquals("MECANICO", crear("MECANICO").getTipoPunto());
        assertEquals("HOSPITAL", crear("HOSPITAL").getTipoPunto());
    }

    @Test
    void elNombreSeRecortaYNoPuedeSerVacio() {
        assertEquals("Refugio", new Albergue("  Refugio  ", 3.4, -76.5).getNombre());
        assertThrows(IllegalArgumentException.class, () -> new Albergue(null, 3.4, -76.5));
        assertThrows(IllegalArgumentException.class, () -> new Albergue("   ", 3.4, -76.5));
    }

    @Test
    void elNombreRespetaElLimiteDe100Caracteres() {
        assertEquals(100, new Albergue("x".repeat(100), 3.4, -76.5).getNombre().length());
        assertThrows(IllegalArgumentException.class,
                () -> new Albergue("x".repeat(101), 3.4, -76.5));
    }

    @Test
    void latitudRespetaLosLimites() {
        Albergue punto = new Albergue("Refugio", 3.4, -76.5);
        punto.setLatitud(90);
        punto.setLatitud(-90);
        assertThrows(IllegalArgumentException.class, () -> punto.setLatitud(91));
        assertThrows(IllegalArgumentException.class, () -> punto.setLatitud(-91));
        assertThrows(IllegalArgumentException.class, () -> punto.setLatitud(Double.NaN));
    }

    @Test
    void longitudRespetaLosLimites() {
        Albergue punto = new Albergue("Refugio", 3.4, -76.5);
        punto.setLongitud(180);
        punto.setLongitud(-180);
        assertThrows(IllegalArgumentException.class, () -> punto.setLongitud(181));
        assertThrows(IllegalArgumentException.class, () -> punto.setLongitud(-181));
        assertThrows(IllegalArgumentException.class, () -> punto.setLongitud(Double.NaN));
    }

    @Test
    void elEstadoNoPuedeSerNulo() {
        Albergue punto = new Albergue("Refugio", 3.4, -76.5);
        assertThrows(IllegalArgumentException.class, () -> punto.setEstado(null));
    }

    @Test
    void unPuntoNuevoEstaAbiertoYPuedeCerrarse() {
        Albergue punto = new Albergue("Refugio", 3.4, -76.5);
        assertTrue(punto.isAbierto());
        punto.setEstado(EstadoPunto.CERRADO);
        assertFalse(punto.isAbierto());
    }

    @Test
    void losServiciosNoPuedenSerNulos() {
        Albergue punto = new Albergue("Refugio", 3.4, -76.5);
        assertThrows(IllegalArgumentException.class, () -> punto.setServicios(null));
    }

    @Test
    void losVoluntariosSeLeenDeLosServicios() {
        ServiciosPunto servicios = new ServiciosPunto(List.of("AGUA"), 6);
        PuntoAsistencia punto = PuntoAsistencia.crear("ALBERGUE", 1, "Refugio", 3.4, -76.5,
                EstadoPunto.ABIERTO, servicios);
        assertEquals(6, punto.getVoluntariosNecesarios());
    }

    @Test
    void esGeolocalizableYDevuelveSusCoordenadas() {
        PuntoAsistencia punto = new Albergue("Refugio", 3.4516, -76.532);
        assertInstanceOf(Geolocalizable.class, punto);
        Geolocalizable ubicable = punto;
        assertEquals(3.4516, ubicable.getLatitud(), 0.00001);
        assertEquals(-76.532, ubicable.getLongitud(), 0.00001);
    }

    @Test
    void toStringMuestraTipoNombreYEstado() {
        Albergue punto = new Albergue("Refugio Sur", 3.4, -76.5);
        assertEquals("Albergue: Refugio Sur (Abierto)", punto.toString());
    }

    @Test
    void setIdCambiaElId() {
        Albergue punto = new Albergue("Refugio", 3.4, -76.5);
        assertEquals(0, punto.getId());
        punto.setId(42);
        assertEquals(42, punto.getId());
    }
}
