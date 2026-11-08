package utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Convierte la contraseña en un hash SHA-256 para no guardarla en texto plano.
 *
 * @author Equipo 1 - Autenticación y Red Familiar
 */
public class PasswordUtil {

    // Devuelve el hash SHA-256 de la contraseña en hexadecimal
    public static String cifrar(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("No se pudo cifrar la contraseña", e);
        }
    }

    // Compara la contraseña escrita con la guardada en la base de datos.
    // También acepta contraseñas antiguas guardadas sin cifrar (ej. el usuario de prueba).
    public static boolean verificar(String password, String guardada) {
        if (password == null || guardada == null) {
            return false;
        }
        return guardada.equals(cifrar(password)) || guardada.equals(password);
    }
}
