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
import models.EstadoSupervivencia;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import javax.swing.JOptionPane;

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
    
    private boolean hayConexion() {
        return !vista.getChkSinConexion().isSelected();
    }
    
    private void emitirEstado(EstadoSupervivencia estado) {
        Usuario usuario = Sesion.getUsuarioActual();
        if (usuario == null) {
            vista.setMensaje("Debes iniciar sesión para reportar tu estado.");
            return;
        }

        emitirEstado(usuario, estado, ubicacionActual.get()); // ubicación null si no hizo clic en el mapa
    }
    
    // sobrecarga
    private void emitirEstado(Usuario usuario, EstadoSupervivencia estado, double[] ubicacion) {
        if (estado == EstadoSupervivencia.NECESITO_AYUDA && ubicacion == null) {
            int respuesta = JOptionPane.showConfirmDialog(vista,
                    "No has marcado tu ubicación en el mapa (haz clic en el mapa).\n¿Enviar la alerta de todos modos?",
                    "Sin ubicación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (respuesta != JOptionPane.YES_OPTION) {
                return;
            }
        }
        Double lat = (ubicacion == null) ? null : ubicacion[0];
        Double lon = (ubicacion == null) ? null : ubicacion[1];

        MensajeEstado mensaje = new MensajeEstado(usuario.getId(), usuario.getNombreCompleto(),
                usuario.getTelefono(), estado, lat, lon);
        historialSesion.add(0, mensaje);
        cola.encolar(mensaje);

        if (hayConexion()) {
            sincronizar();
        } else {
            vista.setMensaje("Sin conexión: \"" + estado.getEtiqueta() + "\" quedó en la cola ("
                    + cola.cantidadPendientes() + " pendiente(s)).");
            actualizarVista();
        }
    }
    
    private void sincronizar() {
        int enviados = cola.sincronizar(dao::registrarEstado);
        int pendientes = cola.cantidadPendientes();

        if (pendientes == 0 && enviados > 0) {
            vista.setMensaje(enviados == 1
                    ? "Mensaje enviado correctamente."
                    : "Conexión recuperada: se enviaron " + enviados + " mensajes pendientes.");
        } else if (pendientes > 0) {
            vista.setMensaje("No se pudo enviar. Quedan " + pendientes
                    + " mensaje(s) en la cola; se reintentará al sincronizar.");
        }
        actualizarVista();
    }
    
    private void actualizarVista() {
        vista.mostrarMensajes(historialSesion);
        vista.setPendientes(cola.cantidadPendientes());
    }
    
}
