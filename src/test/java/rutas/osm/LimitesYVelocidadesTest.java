/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rutas.osm;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LimitesYVelocidadesTest {

    @Test
    void zonaContieneLosPuntosConMargenYCuadricula() {
        LimitesGeo z = LimitesGeo.alrededorDe(3.4516, -76.5320, 3.4200, -76.5400, 0.012, 0.25, 0.01);
        assertTrue(z.contieneConHolgura(3.4516, -76.5320, 0.012));
        assertTrue(z.contieneConHolgura(3.4200, -76.5400, 0.012));
        assertEquals(0, Math.round(z.getSur() * 1e6) % 10000, "borde en la cuadrícula");
        assertTrue(z.comoOverpass().matches("[-0-9.,]+"));
        assertFalse(z.clave().contains("-"));
    }

    @Test
    void limitesInvalidos() {
        assertThrows(IllegalArgumentException.class, () -> new LimitesGeo(3.5, -76, 3.4, -75));
        assertThrows(IllegalArgumentException.class, () -> new LimitesGeo(3.4, -76, 3.5, -200));
    }

    @Test
    void consultaOverpassPideViasYNodos() {
        String q = ClienteOverpass.construirConsulta(new LimitesGeo(3.40, -76.56, 3.48, -76.50));
        assertTrue(q.contains("way[\"highway\"~"));
        assertTrue(q.contains("(3.40000,-76.56000,3.48000,-76.50000)"));
        assertTrue(q.contains("(._;>;);"));
        assertTrue(q.contains("out body;"));
    }

    @Test
    void velocidades() {
        assertEquals(50, VelocidadesVia.velocidadKmh("primary", null), 1e-9);
        assertEquals(60, VelocidadesVia.velocidadKmh("residential", "60 km/h"), 1e-9);
        assertEquals(48.28, VelocidadesVia.velocidadKmh("residential", "30 mph"), 0.01);
        assertEquals(30, VelocidadesVia.velocidadKmh("residential", "signals"), 1e-9);
        assertTrue(VelocidadesVia.esTransitable("Residential"));
        assertFalse(VelocidadesVia.esTransitable("footway"));
        assertFalse(VelocidadesVia.esTransitable(null));
    }
}
