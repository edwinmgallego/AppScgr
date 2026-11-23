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

    // Título que tiene la tabla ahora ("Listado de reportes", "Resultados del filtro"...).
    // Se guarda porque limpiarFormulario() de la vista lo regresa a "Listado de reportes".
    private String tituloTabla = "Listado de reportes";

    public ExtraviadosController(ExtraviadosView vista) {
        this.vista = vista;
        this.dao = new ReporteExtraviadoDAO();

        vista.addGuardarListener(e -> guardar());
        vista.addActualizarListener(e -> actualizar());
        vista.addEliminarListener(e -> eliminar());
        vista.addFiltrarListener(e -> filtrar());
        vista.addVerTodosListener(e -> verTodos());
        vista.addLimpiarListener(e -> limpiar());

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

    // Busca con los filtros de la vista. Si un combo está en "Todos", la vista devuelve null
    // y el DAO entiende que ese filtro no se aplica. Lo mismo con el texto vacío.
    private void filtrar() {
        List<ReporteExtraviado> resultados = dao.buscarPorFiltros(
                vista.getFiltroTipo(), vista.getFiltroEstado(), vista.getFiltroTexto());

        mostrarEnTabla(resultados, "Resultados del filtro (" + resultados.size() + ")");

        if (resultados.isEmpty()) {
            vista.mostrarMensaje("No hay reportes que cumplan esos filtros.\n"
                    + "Usa Ver todos para volver al listado completo.");
        }
    }

    // Quita los filtros y vuelve a mostrar todos los reportes
    private void verTodos() {
        vista.limpiarFiltros();
        cargarReportes();
    }

    // Deja el formulario vacío para escribir un reporte nuevo, sin tocar lo que muestra la tabla
    private void limpiar() {
        vista.limpiarFormulario();
        vista.setTituloTabla(tituloTabla); // limpiarFormulario() cambia el título; se deja el que estaba
    }

    // Trae todos los reportes de la base de datos y los muestra en la tabla
    private void cargarReportes() {
        mostrarEnTabla(dao.obtenerTodos(), "Listado de reportes");
    }

    // Pone una lista en la tabla con su título.
    // También limpia el formulario: al cambiar la tabla se pierde la fila seleccionada,
    // y si el formulario siguiera lleno quedaría apuntando a un reporte que ya no está marcado.
    private void mostrarEnTabla(List<ReporteExtraviado> reportes, String titulo) {
        vista.limpiarFormulario();
        vista.setReportes(reportes);
        tituloTabla = titulo;
        vista.setTituloTabla(titulo);
    }

    // Id del usuario que inició sesión. Si nadie ha iniciado sesión (pruebas sin login) se usa el 1
    private int idUsuarioActual() {
        Usuario usuario = Sesion.getUsuarioActual();
        return (usuario != null) ? usuario.getId() : 1;
    }
}
