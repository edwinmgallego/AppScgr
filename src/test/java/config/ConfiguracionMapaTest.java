/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package config;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas de la configuración del mapa (no necesitan base de datos).
 *
 * @author equipo3/samuellopez
 */
public class ConfiguracionMapaTest {

    @Test
    public void testAHttpsConvierteHttp() {
        assertEquals("https://tile.openstreetmap.org/7/20/50.png",
                ConfiguracionMapa.aHttps("http://tile.openstreetmap.org/7/20/50.png"));
    }

    @Test
    public void testAHttpsConservaHttps() {
        String url = "https://tile.openstreetmap.org/7/20/50.png";
        assertEquals(url, ConfiguracionMapa.aHttps(url));
    }

    @Test
    public void testAHttpsConUrlNula() {
        assertNull(ConfiguracionMapa.aHttps(null));
    }

    @Test
    public void testValoresPorDefecto() {
        assertTrue(ConfiguracionMapa.CALI_LATITUD > -90 && ConfiguracionMapa.CALI_LATITUD < 90);
        assertTrue(ConfiguracionMapa.CALI_LONGITUD > -180 && ConfiguracionMapa.CALI_LONGITUD < 180);
        assertFalse(ConfiguracionMapa.USER_AGENT.isBlank(), "OpenStreetMap exige un User-Agent.");
    }
}
