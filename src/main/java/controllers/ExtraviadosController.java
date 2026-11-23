package controllers;

import config.Sesion;
import java.util.List;
import models.ReporteExtraviado;
import models.Usuario;
import models.dao.ReporteExtraviadoDAO;
import views.ExtraviadosView;

/**
 * Controlador del módulo de personas y mascotas extraviadas (RF-11 y RF-12).
 * Une la vista ExtraviadosView con el DAO ReporteExtraviadoDAO:
 * escucha los botones, le pide los datos a la vista, llama al DAO
 * y refresca la tabla con el resultado.
 *
 * @author Equipo 8 - Juan Manuel Bedoya
 */
public class ExtraviadosController {

    private final ExtraviadosView vista;
    private final ReporteExtraviadoDAO dao;

    public ExtraviadosController(ExtraviadosView vista) {
        this.vista = vista;
        this.dao = new ReporteExtraviadoDAO();

        vista.addGuardarListener(e -> guardar());
        vista.addActualizarListener(e -> actualizar());
        vista.addEliminarListener(e -> eliminar());

        cargarReportes();
    }

    // Registra un reporte nuevo con los datos del formulario
    private void guardar() {
        // Si hay una fila seleccionada, el formulario muestra un reporte que ya existe:
        // guardarlo crearía un duplicado. Para cambiarlo se usa Actualizar.
        if (vista.getIdSeleccionado() != null) {
            vista.mostrarError("Tienes un reporte seleccionado de la tabla.\n"
                    + "Usa Actualizar para modificarlo, o Limpiar para crear uno nuevo.");
            return;
        }

        // La vista valida nombre, características y coordenadas.
        // Si algo está mal, ella misma muestra el error y devuelve null.
        ReporteExtraviado reporte = vista.construirReporteDesdeFormulario();
        if (reporte == null) {
            return;
        }

        if (dao.registrarReporte(reporte, idUsuarioActual())) {
            // El DAO le pone al objeto el id que generó MySQL
            vista.mostrarMensaje("Reporte guardado correctamente (id " + reporte.getId() + ").");
            cargarReportes();
        } else {
            vista.mostrarError("No se pudo guardar el reporte.\n"
                    + "Revisa que el nombre no pase de 100 caracteres y que XAMPP esté encendido.");
        }
    }

    // Guarda los cambios del reporte seleccionado (por ejemplo, pasarlo de Perdido a Encontrado)
    private void actualizar() {
        if (vista.getIdSeleccionado() == null) {
            vista.mostrarError("Selecciona en la tabla el reporte que quieres actualizar.");
            return;
        }

        // Como hay una fila seleccionada, la vista le pone al reporte el id de esa fila
        ReporteExtraviado reporte = vista.construirReporteDesdeFormulario();
        if (reporte == null) {
            return;
        }

        if (dao.actualizarReporte(reporte)) {
            vista.mostrarMensaje("Reporte actualizado correctamente.");
            cargarReportes();
        } else {
            vista.mostrarError("No se pudo actualizar el reporte.\n"
                    + "Puede que otro usuario lo haya eliminado. Se recargará la tabla.");
            cargarReportes();
        }
    }

    // Elimina el reporte seleccionado, después de pedir confirmación
    private void eliminar() {
        Integer id = vista.getIdSeleccionado();
        if (id == null) {
            vista.mostrarError("Selecciona en la tabla el reporte que quieres eliminar.");
            return;
        }

        // Se busca el reporte en la BD para mostrar su nombre en la confirmación
        ReporteExtraviado reporte = dao.obtenerPorId(id);
        String nombre = (reporte != null) ? reporte.getNombre() : null;

        if (!vista.confirmarEliminacion(nombre)) {
            return; // el usuario dijo que No
        }

        if (dao.eliminarReporte(id)) {
            vista.mostrarMensaje("Reporte eliminado correctamente.");
        } else {
            vista.mostrarError("No se pudo eliminar el reporte. Puede que ya no exista.");
        }
        cargarReportes();
    }

    // Trae todos los reportes de la base de datos y los muestra en la tabla.
    // También limpia el formulario: al recargar la tabla se pierde la fila seleccionada,
    // y si el formulario siguiera lleno quedaría apuntando a un reporte que ya no está marcado.
    private void cargarReportes() {
        List<ReporteExtraviado> reportes = dao.obtenerTodos();
        vista.limpiarFormulario();
        vista.setReportes(reportes);
        vista.setTituloTabla("Listado de reportes");
    }

    // Id del usuario que inició sesión. Si nadie ha iniciado sesión (pruebas sin login) se usa el 1
    private int idUsuarioActual() {
        Usuario usuario = Sesion.getUsuarioActual();
        return (usuario != null) ? usuario.getId() : 1;
    }
}
