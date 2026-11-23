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

        cargarReportes();
    }

    // Trae todos los reportes de la base de datos y los muestra en la tabla
    private void cargarReportes() {
        List<ReporteExtraviado> reportes = dao.obtenerTodos();
        vista.setReportes(reportes);
        vista.setTituloTabla("Listado de reportes");
    }

    // Id del usuario que inició sesión. Si nadie ha iniciado sesión (pruebas sin login) se usa el 1
    private int idUsuarioActual() {
        Usuario usuario = Sesion.getUsuarioActual();
        return (usuario != null) ? usuario.getId() : 1;
    }
}
