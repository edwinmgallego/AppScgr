package rutas.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class GeoTest {

    @Test
    @DisplayName("Un grado de latitud son unos 111.195 m")
    void gradoDeLatitud() {
        assertEquals(111194.93, Geo.distanciaMetros(0, 0, 1, 0), 1.0);
    }

    @Test
    void mismoPuntoDistanciaCero() {
        assertEquals(0.0, Geo.distanciaMetros(3.45, -76.53, 3.45, -76.53), 0.0001);
    }

    @Test
    void distanciaEsSimetrica() {
        assertEquals(Geo.distanciaMetros(3.45, -76.53, 3.41, -76.50),
                Geo.distanciaMetros(3.41, -76.50, 3.45, -76.53), 0.0001);
    }

    @Test
    void puntoQueSeProyectaDentroDelSegmento() {
        // segmento horizontal en el ecuador; el punto está 0.001° (111 m) al norte de su mitad
        double d = Geo.distanciaPuntoSegmentoMetros(0.001, 0.001, 0, 0, 0, 0.002);
        assertEquals(111.19, d, 0.5);
    }

    @Test
    void puntoMasAllaDelExtremoMideContraElExtremo() {
        double d = Geo.distanciaPuntoSegmentoMetros(0, 0.003, 0, 0, 0, 0.002);
        assertEquals(111.19, d, 0.5);
    }

    @Test
    void puntoSobreElSegmentoDistanciaCero() {
        assertEquals(0.0, Geo.distanciaPuntoSegmentoMetros(0, 0.001, 0, 0, 0, 0.002), 0.001);
    }

    @Test
    void segmentoDegeneradoSeComportaComoUnPunto() {
        assertEquals(111.19, Geo.distanciaPuntoSegmentoMetros(0.001, 0, 0, 0, 0, 0), 0.5);
    }

    @Test
    void validaRangosDeCoordenadas() {
        assertTrue(Geo.coordenadasValidas(3.45, -76.53));
        assertTrue(Geo.coordenadasValidas(-90, 180));
        assertFalse(Geo.coordenadasValidas(90.1, 0));
        assertFalse(Geo.coordenadasValidas(0, -180.1));
        assertFalse(Geo.coordenadasValidas(Double.NaN, 0));
        assertFalse(Geo.coordenadasValidas(0, Double.NaN));
    }
}
