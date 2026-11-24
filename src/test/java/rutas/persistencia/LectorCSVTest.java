package rutas.persistencia;

import static rutas.TestUtil.*;
import static org.junit.jupiter.api.Assertions.*;

import rutas.algoritmos.Dijkstra;
import rutas.algoritmos.RutaResultado;
import rutas.modelo.Grafo;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class LectorCSVTest {
    @TempDir
    Path tmp;

    private final LectorCSV lector = new LectorCSV();

    private String escribir(String nombre, String... lineas) throws IOException {
        Path p = tmp.resolve(nombre);
        Files.write(p, String.join("\n", lineas).getBytes(StandardCharsets.UTF_8));
        return p.toString();
    }

    private String nodosOk() throws IOException {
        return escribir("nodos.csv", "id,nombre,latitud,longitud",
                "1,Centro,3.45,-76.53", "2,Granada,3.46,-76.52", "3,Sur,3.40,-76.54");
    }

    @Test
    @DisplayName("Carga un grafo válido (nodos y tramos)")
    void archivoValido() throws IOException {
        String tramos = escribir("tramos.csv", "idOrigen,idDestino,distancia_m,bidireccional",
                "1,2,500,true", "2,3,700,false");
        Grafo g = lector.cargar(nodosOk(), tramos);
        assertEquals(3, g.numNodos());
        assertEquals("Granada", g.getNodo(2).getNombre());
        assertEquals(3.45, g.getNodo(1).getLatitud(), 0.0001);
        assertEquals(2, g.getAdyacentes(2).size());   // 2->1 (bidireccional) y 2->3
        assertTrue(g.getAdyacentes(3).isEmpty());      // 2->3 es de un solo sentido
    }

    @Test
    void ignoraLineasEnBlancoYComentarios() throws IOException {
        String nodos = escribir("nodos.csv", "id,nombre,latitud,longitud",
                "# nodos de prueba", "", "1,A,0,0", "   ", "2,B,0,0");
        String tramos = escribir("tramos.csv", "idOrigen,idDestino,distancia_m,bidireccional", "1,2,5,true");
        assertEquals(2, lector.cargar(nodos, tramos).numNodos());
    }

    @Test
    void soloEncabezadoDaGrafoVacio() throws IOException {
        String nodos = escribir("nodos.csv", "id,nombre,latitud,longitud");
        String tramos = escribir("tramos.csv", "idOrigen,idDestino,distancia_m,bidireccional");
        assertEquals(0, lector.cargar(nodos, tramos).numNodos());
    }

    @Test
    void archivoCompletamenteVacioDaGrafoVacio() throws IOException {
        String nodos = escribir("nodos.csv");
        String tramos = escribir("tramos.csv");
        assertEquals(0, lector.cargar(nodos, tramos).numNodos());
    }

    @Test
    void archivoInexistenteLanzaIOException() {
        assertThrows(IOException.class,
                () -> lector.cargar(tmp.resolve("no_existe.csv").toString(), tmp.resolve("x.csv").toString()));
    }

    @Test
    @DisplayName("Una línea con columnas faltantes reporta el número de línea")
    void lineaConColumnasFaltantes() throws IOException {
        String nodos = escribir("nodos.csv", "id,nombre,latitud,longitud",
                "1,Centro,3.45,-76.53", "2,SoloNombre");
        String tramos = escribir("tramos.csv", "idOrigen,idDestino,distancia_m,bidireccional");
        DatosInvalidosException e = assertThrows(DatosInvalidosException.class, () -> lector.cargar(nodos, tramos));
        assertTrue(e.getMessage().contains("línea 3"), e.getMessage());
    }

    @Test
    void numeroInvalidoReportaLinea() throws IOException {
        String nodos = escribir("nodos.csv", "id,nombre,latitud,longitud", "1,Centro,abc,-76.53");
        String tramos = escribir("tramos.csv", "idOrigen,idDestino,distancia_m,bidireccional");
        DatosInvalidosException e = assertThrows(DatosInvalidosException.class, () -> lector.cargar(nodos, tramos));
        assertTrue(e.getMessage().contains("línea 2"), e.getMessage());
    }

    @Test
    void idDuplicadoLanzaDatosInvalidos() throws IOException {
        String nodos = escribir("nodos.csv", "id,nombre,latitud,longitud", "1,A,0,0", "1,B,0,0");
        String tramos = escribir("tramos.csv", "idOrigen,idDestino,distancia_m,bidireccional");
        DatosInvalidosException e = assertThrows(DatosInvalidosException.class, () -> lector.cargar(nodos, tramos));
        assertTrue(e.getMessage().contains("línea 3"), e.getMessage());
    }

    @Test
    void tramoConNodoInexistenteLanzaDatosInvalidos() throws IOException {
        String tramos = escribir("tramos.csv", "idOrigen,idDestino,distancia_m,bidireccional", "1,99,500,true");
        DatosInvalidosException e = assertThrows(DatosInvalidosException.class,
                () -> lector.cargar(nodosOk(), tramos));
        assertTrue(e.getMessage().contains("línea 2"), e.getMessage());
    }

    @Test
    void distanciaNegativaLanzaDatosInvalidos() throws IOException {
        String tramos = escribir("tramos.csv", "idOrigen,idDestino,distancia_m,bidireccional", "1,2,-5,true");
        assertThrows(DatosInvalidosException.class, () -> lector.cargar(nodosOk(), tramos));
    }

    @Test
    void bidireccionalInvalidoLanzaDatosInvalidos() throws IOException {
        String tramos = escribir("tramos.csv", "idOrigen,idDestino,distancia_m,bidireccional", "1,2,5,quizas");
        assertThrows(DatosInvalidosException.class, () -> lector.cargar(nodosOk(), tramos));
    }

    @Test
    void aplicaCierresAlGrafo() throws IOException {
        String tramos = escribir("tramos.csv", "idOrigen,idDestino,distancia_m,bidireccional",
                "1,2,500,true", "2,3,700,true");
        Grafo g = lector.cargar(nodosOk(), tramos);
        String cierres = escribir("cierres.csv", "idOrigen,idDestino,motivo", "2,3,Derrumbe, via principal");
        lector.aplicarCierres(g, cierres);
        assertFalse(new Dijkstra().calcular(g, 1, 3).isRutaEncontrada());
        assertTrue(new Dijkstra().calcular(g, 1, 2).isRutaEncontrada());
    }

    @Test
    void cierreDeTramoInexistenteLanzaDatosInvalidos() throws IOException {
        String tramos = escribir("tramos.csv", "idOrigen,idDestino,distancia_m,bidireccional", "1,2,500,true");
        Grafo g = lector.cargar(nodosOk(), tramos);
        String cierres = escribir("cierres.csv", "idOrigen,idDestino,motivo", "1,3,Derrumbe");
        DatosInvalidosException e = assertThrows(DatosInvalidosException.class,
                () -> lector.aplicarCierres(g, cierres));
        assertTrue(e.getMessage().contains("línea 2"), e.getMessage());
    }

    @Test
    @DisplayName("Exportar una ruta escribe encabezado y un renglón por nodo, en orden")
    void exportaRutaACSV() throws IOException {
        RutaResultado r = new Dijkstra().calcular(grafoEjemplo(), 1, 8);
        Path salida = tmp.resolve("sub/carpeta/ruta.csv");   // la carpeta no existe aún
        lector.exportarRuta(r, salida.toString());
        List<String> lineas = Files.readAllLines(salida, StandardCharsets.UTF_8);
        assertEquals(6, lineas.size());                      // encabezado + 5 nodos
        assertEquals("orden,id,nombre,latitud,longitud", lineas.get(0));
        assertTrue(lineas.get(1).startsWith("1,1,"));
        assertTrue(lineas.get(5).startsWith("5,8,"));
    }

    @Test
    void exportarRutaNoEncontradaLanzaExcepcion() {
        RutaResultado vacia = RutaResultado.noEncontrada("sin ruta");
        assertThrows(IllegalArgumentException.class,
                () -> lector.exportarRuta(vacia, tmp.resolve("r.csv").toString()));
    }

    @Test
    void coordenadasFueraDeRangoEnNodosReportanLinea() throws IOException {
        String nodos = escribir("nodos.csv", "id,nombre,latitud,longitud", "1,A,0,0", "2,B,123.4,-76.5");
        String tramos = escribir("tramos.csv", "idOrigen,idDestino,distancia_m,bidireccional");
        DatosInvalidosException e = assertThrows(DatosInvalidosException.class, () -> lector.cargar(nodos, tramos));
        assertTrue(e.getMessage().contains("línea 3"), e.getMessage());
    }

    @Test
    void distanciaNaNLanzaDatosInvalidos() throws IOException {
        String tramos = escribir("tramos.csv", "idOrigen,idDestino,distancia_m,bidireccional", "1,2,NaN,true");
        assertThrows(DatosInvalidosException.class, () -> lector.cargar(nodosOk(), tramos));
    }

    @Test
    void exportarNodoSinNombreNoLanzaNullPointer() throws IOException {
        java.util.List<rutas.modelo.Nodo> camino = java.util.Arrays.asList(
                new rutas.modelo.Nodo(1, null, 3.45, -76.53), new rutas.modelo.Nodo(2, "B", 3.46, -76.52));
        Path salida = tmp.resolve("ruta.csv");
        lector.exportarRuta(RutaResultado.encontrada(camino, 10), salida.toString());
        assertEquals(3, Files.readAllLines(salida, StandardCharsets.UTF_8).size());
    }
}
