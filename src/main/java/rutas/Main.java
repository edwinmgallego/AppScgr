package rutas;

import rutas.algoritmos.Dijkstra;
import rutas.algoritmos.RutaResultado;
import controllers.RutasController;
import rutas.modelo.Nodo;
import rutas.persistencia.LectorCSV;
import rutas.util.Constantes;
import java.io.IOException;
import java.util.List;

/**
 * Vista mínima por consola (solo depuración, sin interfaz gráfica).
 *
 * Uso: Main [nodos.csv] [tramos.csv] [cierres.csv|-] [origen] [destino] [salida.csv]
 * Sin argumentos usa los archivos de /data, origen 1 y destino 8.
 * Con "-" en cierres se omiten los cierres viales.
 */
public class Main {

    public static void main(String[] args) {
        String nodos = arg(args, 0, Constantes.NODOS_CSV);
        String tramos = arg(args, 1, Constantes.TRAMOS_CSV);
        String cierres = arg(args, 2, Constantes.CIERRES_CSV);
        String salida = arg(args, 5, Constantes.SALIDA_RUTA_CSV);

        try {
            int origen = Integer.parseInt(arg(args, 3, "1"));
            int destino = Integer.parseInt(arg(args, 4, "8"));

            RutasController controlador = new RutasController(new LectorCSV(), new Dijkstra());
            controlador.cargarDatos(nodos, tramos);
            System.out.println("Grafo cargado: " + controlador.getGrafo().numNodos() + " nodos.");
            if (!"-".equals(cierres)) {
                controlador.aplicarCierres(cierres);
                System.out.println("Cierres aplicados desde " + cierres);
            }

            List<RutaResultado> rutas =
                    controlador.calcularRutasAlternas(origen, destino, Constantes.NUM_RUTAS_ALTERNAS);
            if (rutas.isEmpty()) {
                System.out.println("No existe ruta entre " + origen + " y " + destino + ".");
                System.exit(2);
                return;
            }
            for (int i = 0; i < rutas.size(); i++) {
                System.out.println((i == 0 ? "Mejor ruta" : "Alterna " + i) + ": " + describir(rutas.get(i)));
            }
            controlador.exportarRuta(rutas.get(0), salida);
            System.out.println("Mejor ruta exportada a " + salida);
        } catch (NumberFormatException e) {
            System.err.println("Origen y destino deben ser números enteros.");
            System.err.println("Uso: Main [nodos.csv] [tramos.csv] [cierres.csv|-] [origen] [destino] [salida.csv]");
            System.exit(1);
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }

    private static String describir(RutaResultado r) {
        StringBuilder sb = new StringBuilder();
        for (Nodo n : r.getCamino()) {
            if (sb.length() > 0) sb.append(" -> ");
            sb.append(n.getId()).append(" (").append(n.getNombre()).append(")");
        }
        return sb + " | costo = " + r.getCosto() + " m";
    }

    private static String arg(String[] args, int i, String porDefecto) {
        return args.length > i ? args[i] : porDefecto;
    }
}
