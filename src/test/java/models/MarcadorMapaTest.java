/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas del modelo MarcadorMapa y de su conversión desde Incidente (no necesitan base de datos).
 *
 * @author equipo3/samuellopez
 */
public class MarcadorMapaTest {

    @Test
    public void testMarcadorValido() {
        MarcadorMapa m = new MarcadorMapa("  Calle 5  ", 3.45, -76.53, TipoMarcador.VIA_CERRADA);
        assertEquals("Calle 5", m.getEtiqueta());
        assertEquals(3.45, m.getLatitud(), 0.0001);
        assertEquals(-76.53, m.getLongitud(), 0.0001);
        assertEquals(TipoMarcador.VIA_CERRADA, m.getTipo());
    }

    @Test
    public void testSobrecargaUsaLaEtiquetaDelTipo() {
        MarcadorMapa m = new MarcadorMapa(3.45, -76.53, TipoMarcador.DERRUMBE);
        assertEquals("Derrumbe", m.getEtiqueta());
    }

    @Test
    public void testLatitudFueraDeRango() {
        assertThrows(IllegalArgumentException.class, () -> new MarcadorMapa("A", 91, 0, TipoMarcador.OTRO));
        assertThrows(IllegalArgumentException.class, () -> new MarcadorMapa("A", -91, 0, TipoMarcador.OTRO));
        assertThrows(IllegalArgumentException.class, () -> new MarcadorMapa("A", Double.NaN, 0, TipoMarcador.OTRO));
    }

    @Test
    public void testLongitudFueraDeRango() {
        assertThrows(IllegalArgumentException.class, () -> new MarcadorMapa("A", 0, 181, TipoMarcador.OTRO));
        assertThrows(IllegalArgumentException.class, () -> new MarcadorMapa("A", 0, -181, TipoMarcador.OTRO));
    }

    @Test
    public void testEtiquetaObligatoria() {
        assertThrows(IllegalArgumentException.class, () -> new MarcadorMapa(null, 0, 0, TipoMarcador.OTRO));
        assertThrows(IllegalArgumentException.class, () -> new MarcadorMapa("   ", 0, 0, TipoMarcador.OTRO));
    }

    @Test
    public void testTipoObligatorio() {
        assertThrows(IllegalArgumentException.class, () -> new MarcadorMapa("A", 0, 0, null));
        assertThrows(IllegalArgumentException.class, () -> new MarcadorMapa(0, 0, null));
    }

    @Test
    public void testDesdeIncidente() {
        Incidente cierre = new Incidente(1, "VIA_CERRADA", 3.4516, -76.5320, "Cierre por obras");
        MarcadorMapa m = MarcadorMapa.desdeIncidente(cierre);
        assertEquals(TipoMarcador.VIA_CERRADA, m.getTipo());
        assertEquals(3.4516, m.getLatitud(), 0.0001);
        assertEquals(-76.5320, m.getLongitud(), 0.0001);

        Incidente otro = new Incidente(2, "SIN_SENAL", 3.40, -76.50, "");
        assertEquals(TipoMarcador.OTRO, MarcadorMapa.desdeIncidente(otro).getTipo());
    }

    @Test
    public void testDesdeIncidenteConCoordenadasInvalidas() {
        Incidente malo = new Incidente(3, "DERRUMBE", 500, -76.5, "");
        assertThrows(IllegalArgumentException.class, () -> MarcadorMapa.desdeIncidente(malo));
        assertThrows(IllegalArgumentException.class, () -> MarcadorMapa.desdeIncidente(null));
    }

    @Test
    public void testIncidenteYMarcadorSonGeolocalizables() {
        Geolocalizable[] elementos = {
            new Incidente(1, "DERRUMBE", 3.45, -76.53, ""),
            new MarcadorMapa(3.46, -76.54, TipoMarcador.SELECCION)
        };
        assertEquals(3.45, elementos[0].getLatitud(), 0.0001);
        assertEquals(-76.54, elementos[1].getLongitud(), 0.0001);
    }

    @Test
    public void testNormalizarLongitud() {
        assertEquals(-76.53, MarcadorMapa.normalizarLongitud(-76.53), 0.0001);
        assertEquals(-170, MarcadorMapa.normalizarLongitud(190), 0.0001);
        assertEquals(170, MarcadorMapa.normalizarLongitud(-190), 0.0001);
        assertEquals(0, MarcadorMapa.normalizarLongitud(360), 0.0001);
    }
}
