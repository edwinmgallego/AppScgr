package rutas.osm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import rutas.modelo.Arista;
import rutas.modelo.Grafo;
import rutas.modelo.Nodo;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/** Construcción del grafo de navegación a partir de OSM XML (Equipo 4, RF-04). */
class LectorOSMTest {

    private RedVialOSM red;
    private Grafo g;

    @BeforeEach
    void cargar() throws IOException {
        red = RedPrueba.red(null);
        g = red.getGrafo();
    }

    private Nodo nodoOsm(long idOsm) {
        for (Nodo n : g.getNodos()) if (Long.valueOf(idOsm).equals(red.getIdOsm(n.getId()))) return n;
        return null;
    }

    private boolean hayArco(long origenOsm, long destinoOsm) {
        Nodo o = nodoOsm(origenOsm);
        Nodo d = nodoOsm(destinoOsm);
        if (o == null || d == null) return false;
        for (Arista a : g.getAdyacentes(o.getId())) if (a.getDestino() == d.getId()) return true;
        return false;
    }

    @Test
    @DisplayName("Cada punto de forma de las vías es un nodo y los consecutivos quedan conectados")
    void conservaGeometria() {
        // 10 nodos de calles + 2 del pedazo suelto + 9 de la curva = 21 (el nodo del sendero no entra)
        assertEquals(21, g.numNodos());
        for (int k = 1; k < 9; k++) assertTrue(hayArco(50 + k, 51 + k), "curva " + k);
        assertTrue(hayArco(10, 51));
        assertTrue(hayArco(59, 14));
    }

    @Test
    @DisplayName("Los pesos son las distancias reales en metros")
    void pesosEnMetros() {
        Nodo s1 = nodoOsm(10);
        Nodo s2 = nodoOsm(11);
        for (Arista a : g.getAdyacentes(s1.getId())) {
            if (a.getDestino() == s2.getId()) {
                assertEquals(556, a.getPeso(), 3);   // 0,005° de longitud en el ecuador ~ 556 m
                return;
            }
        }
        fail("No existe el tramo S1-S2");
    }

    @Test
    @DisplayName("Se excluyen senderos peatonales y vías privadas")
    void excluyeViasNoTransitables() {
        assertNull(nodoOsm(30), "el nodo del sendero no debe existir");
        assertFalse(hayArco(12, 30));
        assertFalse(hayArco(11, 21), "la vía privada no debe conectar S2 con N2");
    }

    @Test
    @DisplayName("Intersecciones: nodos compartidos por dos o más vías")
    void detectaIntersecciones() {
        assertTrue(red.esInterseccion(nodoOsm(10).getId()));   // Calle 1 + Carrera A + Curva
        assertTrue(red.esInterseccion(nodoOsm(24).getId()));   // Calle 2 + Carrera B
        assertFalse(red.esInterseccion(nodoOsm(12).getId()));  // solo Calle 1 (el sendero no cuenta)
        assertEquals(4, red.getNumIntersecciones());
        assertEquals(6, red.getNumVias());
    }

    @Test
    @DisplayName("Doble sentido por defecto; oneway=-1 solo permite el sentido contrario al dibujo")
    void respetaSentidos() throws IOException {
        assertTrue(hayArco(20, 21));
        assertTrue(hayArco(21, 20));
        red = RedPrueba.red("-1");
        g = red.getGrafo();
        assertFalse(hayArco(20, 21));
        assertTrue(hayArco(21, 20));
        red = RedPrueba.red("yes");
        g = red.getGrafo();
        assertTrue(hayArco(20, 21));
        assertFalse(hayArco(21, 20));
    }

    @Test
    @DisplayName("Nombre y velocidad de cada tramo; tiempo y vías del recorrido")
    void infoDeTramos() {
        Nodo n1 = nodoOsm(20);
        Nodo n2 = nodoOsm(21);
        RedVialOSM.InfoTramo info = red.getInfoTramo(n1.getId(), n2.getId());
        assertEquals("Calle 2", info.nombreVia());
        assertEquals(30.0, info.velocidadKmh(), 1e-9);
        // 556 m a 30 km/h ~ 66,7 s
        assertEquals(66.7, red.tiempoSegundos(java.util.List.of(n1, n2)), 1.0);
        assertEquals(java.util.List.of("Calle 2"), red.nombresVias(java.util.List.of(n1, n2)));
    }

    @Test
    @DisplayName("El pedazo de vía aislado no pertenece a la red principal")
    void componentePrincipal() {
        assertTrue(red.esNodoPrincipal(nodoOsm(10)));
        assertFalse(red.esNodoPrincipal(nodoOsm(40)));
        assertFalse(red.esNodoPrincipal(null));
    }

    @Test
    @DisplayName("reabrirTramos deja todas las aristas abiertas")
    void reabrir() {
        g.cerrarArista(nodoOsm(11).getId(), nodoOsm(12).getId());
        assertEquals(1, red.reabrirTramos());
        for (Arista a : g.getTodasLasAristas()) assertFalse(a.isBloqueada());
    }

    @Test
    @DisplayName("XML inválido o respuesta de error de Overpass -> IOException clara")
    void errores() {
        assertThrows(IOException.class, () -> new LectorOSM().leer(
                new ByteArrayInputStream("<osm><node id='1'".getBytes(StandardCharsets.UTF_8)), null));
        IOException e = assertThrows(IOException.class, () -> new LectorOSM().leer(new ByteArrayInputStream(
                "<osm><remark>runtime error: Query timed out</remark></osm>".getBytes(StandardCharsets.UTF_8)), null));
        assertTrue(e.getMessage().contains("timed out"));
    }
}
