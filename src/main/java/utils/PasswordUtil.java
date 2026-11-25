package utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Convierte la contraseña en un hash SHA-256 para no guardarla en texto plano.
 *
 * @author Equipo 1 - Autenticación y Red Familiar
 */
public class PasswordUtil {

    // Cantidad mínima de caracteres que debe tener una contraseña nueva
    public static final int MINIMO_CARACTERES = 8;

    // Revisa que la contraseña sea segura (se usa al registrar un usuario).
    // Devuelve el mensaje de error, o null si cumple todo.
    // Las expresiones regulares buscan, en cualquier posición de la contraseña:
    //   \p{Lu} = una letra mayúscula, \p{Ll} = una letra minúscula,
    //   [^\p{L}\p{N}\s] = algo que NO sea letra, número ni espacio (carácter especial, ej. @ # $ !)
    public static String validarFormato(String password) {
        if (password.length() < MINIMO_CARACTERES) {
            return "La contraseña debe tener al menos " + MINIMO_CARACTERES + " caracteres.";
        }
        if (!password.matches(".*\\p{Lu}.*")) {
            return "La contraseña debe tener al menos una letra mayúscula.";
        }
        if (!password.matches(".*\\p{Ll}.*")) {
            return "La contraseña debe tener al menos una letra minúscula.";
        }
        if (!password.matches(".*[^\\p{L}\\p{N}\\s].*")) {
            return "La contraseña debe tener al menos un carácter especial (ej. @ # $ % !).";
        }
        return null;
    }

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
