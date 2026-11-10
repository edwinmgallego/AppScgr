package utils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Revisa si el servidor MySQL de XAMPP está encendido, intentando abrir
 * una conexión de red a su puerto (no usa usuario ni contraseña).
 *
 * @author (escribe aquí tu nombre)
 */
public final class VerificadorBD {

    // Mismos datos que la URL de config.ConexionDB (jdbc:mysql://localhost:3306/...)
    public static final String HOST = "localhost";
    public static final int PUERTO = 3306;
    public static final int TIEMPO_ESPERA_MS = 2000;

    private VerificadorBD() { }

    /** Devuelve true si hay un servidor escuchando en ese host y puerto. */
    public static boolean servidorEncendido(String host, int puerto, int tiempoEsperaMs) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, puerto), tiempoEsperaMs);
            return true;
        } catch (IOException e) {
            return false; // Nadie respondió: MySQL está apagado o no existe
        }
    }

    /** Revisa el MySQL de XAMPP con los datos por defecto. */
    public static boolean mysqlEncendido() {
        return servidorEncendido(HOST, PUERTO, TIEMPO_ESPERA_MS);
    }
}
