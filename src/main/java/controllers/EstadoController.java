package controllers;

/**
 *
 * @author Jurbano
 */
import config.Sesion;
import models.ColaMensajes;
import models.MensajeEstado;
import models.Usuario;
import models.dao.MensajeEstadoDAO;
import views.EstadoView;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class EstadoController extends BaseController<EstadoView> {

    private final MensajeEstadoDAO dao;
    private final ColaMensajes cola;
    private final Supplier<double[]> ubicacionActual; // {latitud, longitud} o null

    // Todos los mensajes emitidos en esta sesión (el más reciente primero), para la tabla
    private final List<MensajeEstado> historialSesion = new ArrayList<>();

    public EstadoController(EstadoView vista, Supplier<double[]> ubicacionActual) {
        super(vista);
        this.ubicacionActual = ubicacionActual;
        this.dao = new MensajeEstadoDAO();
        this.cola = new ColaMensajes();

        Usuario usuario = Sesion.getUsuarioActual();
        if (usuario != null) {
            vista.setUsuario(usuario.getNombreCompleto());
        }
        inicializarEventos();
    }
}
