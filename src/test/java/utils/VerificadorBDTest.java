package utils;

import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.net.ServerSocket;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas de VerificadorBD. No necesitan XAMPP: la prueba abre su propio
 * "servidor" falso en un puerto libre de la computadora.
 *
 * @author (escribe aquí tu nombre)
 */
public class VerificadorBDTest {

    @Test
    public void testDetectaServidorEncendido() throws IOException {
        // Puerto 0 = el sistema escoge uno libre
        try (ServerSocket servidorFalso = new ServerSocket(0)) {
            int puerto = servidorFalso.getLocalPort();
            assertTrue(VerificadorBD.servidorEncendido("localhost", puerto, 1000));
        }
    }

    @Test
    public void testDetectaServidorApagado() throws IOException {
        int puerto;
        try (ServerSocket servidorFalso = new ServerSocket(0)) {
            puerto = servidorFalso.getLocalPort();
        } // Aquí el servidor falso ya se cerró: nadie escucha en ese puerto
        assertFalse(VerificadorBD.servidorEncendido("localhost", puerto, 1000));
    }
}
