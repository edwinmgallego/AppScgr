package config;

import models.Usuario;

/**
 * Guarda el usuario que inició sesión para que cualquier controlador lo consulte
 * (ej. MapaController lo usa para saber quién registra un incidente).
 *
 * @author Equipo 1 - Autenticación y Red Familiar
 */
public final class Sesion {

    private static Usuario usuarioActual;

    private Sesion() { }

    public static void iniciar(Usuario usuario) { usuarioActual = usuario; }

    public static void cerrar() { usuarioActual = null; }

    public static Usuario getUsuarioActual() { return usuarioActual; }

    public static boolean hayUsuario() { return usuarioActual != null; }
}
