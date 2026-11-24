package controllers;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import models.Albergue;
import models.CentroAcopio;
import models.EstadoPunto;
import models.Hospital;
import models.PuntoAsistencia;
import models.ServiciosPunto;
import org.junit.jupiter.api.Test;

/**
 * Pruebas unitarias de PuntoAsistenciaFormulario: lectura de coordenadas y servicios,
 * construcción del punto, filtro por tipo y servicios que se muestran.
 *
 * @author Equipo 7 - Puntos de Asistencia y Negocios
 */
public class PuntoAsistenciaFormularioTest {

    @Test
    void coordenadaAceptaPuntoYComaDecimal() {
        assertEquals(3.4516, PuntoAsistenciaFormulario.leerCoordenada("3.4516", "latitud"), 1e-9);
        assertEquals(3.4516, PuntoAsistenciaFormulario.leerCoordenada("3,4516", "latitud"), 1e-9);
        assertEquals(-76.532, PuntoAsistenciaFormulario.leerCoordenada("  -76.532 ", "longitud"), 1e-9);
    }

    @Test
    void coordenadaVaciaONoNumericaLanzaExcepcionConMensajeClaro() {
        IllegalArgumentException vacia = assertThrows(IllegalArgumentException.class,
                () -> PuntoAsistenciaFormulario.leerCoordenada("  ", "latitud"));
        assertTrue(vacia.getMessage().contains("latitud"));
        assertThrows(IllegalArgumentException.class,
                () -> PuntoAsistenciaFormulario.leerCoordenada(null, "latitud"));
        IllegalArgumentException texto = assertThrows(IllegalArgumentException.class,
                () -> PuntoAsistenciaFormulario.leerCoordenada("abc", "longitud"));
        assertTrue(texto.getMessage().contains("longitud"));
    }

    @Test
    void serviciosIgnoraVaciosYNormalizaLosNombres() {
        ServiciosPunto s = PuntoAsistenciaFormulario.leerServicios("agua, ,internet,", 2);
        assertEquals(List.of("AGUA", "INTERNET"), s.getServicios());
        assertEquals(2, s.getVoluntariosNecesarios());
    }

    @Test
    void serviciosNuloDevuelveObjetoVacio() {
        ServiciosPunto s = PuntoAsistenciaFormulario.leerServicios(null, 0);
        assertTrue(s.getServicios().isEmpty());
        assertEquals(0, s.getVoluntariosNecesarios());
    }

    @Test
    void serviciosQuitaLaMarcaAcopioEscritaAMano() {
        ServiciosPunto s = PuntoAsistenciaFormulario.leerServicios("AGUA,ACOPIO", 0);
        assertFalse(s.contiene(ServiciosPunto.ACOPIO));
        assertTrue(s.contiene("AGUA"));
    }

    @Test
    void serviciosConVoluntariosNegativosLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> PuntoAsistenciaFormulario.leerServicios("AGUA", -1));
    }

    @Test
    void construirPuntoCreaUnAlbergue() {
        PuntoAsistencia p = PuntoAsistenciaFormulario.construirPunto(0, "ALBERGUE", "Refugio Sur",
                "3.45", "-76.53", "ABIERTO", "AGUA,INTERNET", 3);
        assertInstanceOf(Albergue.class, p);
        assertEquals("Refugio Sur", p.getNombre());
        assertEquals("ALBERGUE", p.getTipoPunto());
        assertEquals(3, p.getVoluntariosNecesarios());
        assertTrue(p.isAbierto());
    }

    @Test
    void construirPuntoCentroAcopioSeGuardaComoAlbergueConLaMarca() {
        PuntoAsistencia p = PuntoAsistenciaFormulario.construirPunto(0, "CENTRO_ACOPIO", "Acopio Norte",
                "3.45", "-76.53", "ABIERTO", "AGUA", 0);
        assertInstanceOf(CentroAcopio.class, p);
        assertEquals("ALBERGUE", p.getTipoPunto());
        assertTrue(p.getServicios().contiene(ServiciosPunto.ACOPIO));
        assertEquals("CENTRO_ACOPIO", PuntoAsistenciaFormulario.codigoTipo(p));
    }

    @Test
    void construirPuntoAceptaMinusculasYEstadoCerrado() {
        PuntoAsistencia p = PuntoAsistenciaFormulario.construirPunto(5, "hospital", "Clínica Centro",
                "3.4", "-76.5", "cerrado", "", 0);
        assertInstanceOf(Hospital.class, p);
        assertEquals(5, p.getId());
        assertFalse(p.isAbierto());
    }

    @Test
    void construirPuntoSinTipoLanzaExcepcion() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> PuntoAsistenciaFormulario.construirPunto(0, null, "Refugio", "3", "-76", "ABIERTO", "", 0));
        assertTrue(e.getMessage().contains("tipo"));
        assertThrows(IllegalArgumentException.class,
                () -> PuntoAsistenciaFormulario.construirPunto(0, "  ", "Refugio", "3", "-76", "ABIERTO", "", 0));
    }

    @Test
    void construirPuntoConTipoDesconocidoLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> PuntoAsistenciaFormulario.construirPunto(0, "BANCO", "Refugio", "3", "-76", "ABIERTO", "", 0));
    }

    @Test
    void construirPuntoConNombreVacioLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> PuntoAsistenciaFormulario.construirPunto(0, "ALBERGUE", "  ", "3", "-76", "ABIERTO", "", 0));
    }

    @Test
    void construirPuntoConCoordenadaFueraDeRangoLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> PuntoAsistenciaFormulario.construirPunto(0, "ALBERGUE", "Refugio", "95", "-76", "ABIERTO", "", 0));
        assertThrows(IllegalArgumentException.class,
                () -> PuntoAsistenciaFormulario.construirPunto(0, "ALBERGUE", "Refugio", "3", "-200", "ABIERTO", "", 0));
    }

    @Test
    void construirPuntoConServiciosQueNoCabenEnLaColumnaLanzaExcepcion() {
        StringBuilder servicios = new StringBuilder();
        for (int i = 0; i < 40; i++) {
            servicios.append(i == 0 ? "" : ",").append("SERVICIO").append(i);
        }
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> PuntoAsistenciaFormulario.construirPunto(0, "ALBERGUE", "Refugio", "3", "-76",
                        "ABIERTO", servicios.toString(), 0));
        assertTrue(e.getMessage().contains("255"));
    }

    @Test
    void codigoTipoDistingueElCentroDeAcopioDelAlbergue() {
        assertEquals("CENTRO_ACOPIO", PuntoAsistenciaFormulario.codigoTipo(new CentroAcopio("A", 3.4, -76.5)));
        assertEquals("ALBERGUE", PuntoAsistenciaFormulario.codigoTipo(new Albergue("B", 3.4, -76.5)));
    }

    @Test
    void filtrarPorTipoDejaSoloLosPuntosDelTipoPedido() {
        List<PuntoAsistencia> puntos = List.of(
                new Albergue("A", 3.4, -76.5),
                new CentroAcopio("B", 3.4, -76.5),
                PuntoAsistencia.crear("HOSPITAL", 3, "C", 3.4, -76.5, EstadoPunto.ABIERTO, null));
        assertEquals(3, PuntoAsistenciaFormulario.filtrarPorTipo(puntos, "TODOS").size());
        assertEquals(3, PuntoAsistenciaFormulario.filtrarPorTipo(puntos, null).size());
        assertEquals(3, PuntoAsistenciaFormulario.filtrarPorTipo(puntos, "").size());
        assertEquals(1, PuntoAsistenciaFormulario.filtrarPorTipo(puntos, "ALBERGUE").size());
        assertEquals(1, PuntoAsistenciaFormulario.filtrarPorTipo(puntos, "CENTRO_ACOPIO").size());
        assertEquals(1, PuntoAsistenciaFormulario.filtrarPorTipo(puntos, "hospital").size());
        assertEquals(0, PuntoAsistenciaFormulario.filtrarPorTipo(puntos, "MECANICO").size());
    }

    @Test
    void filtrarPorTipoConListaNulaDevuelveListaVacia() {
        assertTrue(PuntoAsistenciaFormulario.filtrarPorTipo(null, "TODOS").isEmpty());
    }

    @Test
    void losServiciosQueSeMuestranOcultanLaMarcaAcopio() {
        CentroAcopio punto = new CentroAcopio("Acopio", 3.4, -76.5);
        punto.getServicios().agregar("AGUA");
        punto.getServicios().agregar("INTERNET");
        assertEquals("AGUA,INTERNET", PuntoAsistenciaFormulario.serviciosParaFormulario(punto));
        assertEquals("AGUA, INTERNET", PuntoAsistenciaFormulario.serviciosParaTabla(punto));
    }

    @Test
    void unPuntoSinServiciosSeMuestraVacio() {
        Albergue punto = new Albergue("Refugio", 3.4, -76.5);
        assertEquals("", PuntoAsistenciaFormulario.serviciosParaFormulario(punto));
        assertEquals("", PuntoAsistenciaFormulario.serviciosParaTabla(punto));
    }
}
