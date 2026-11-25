package controllers;


import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Locale;
import models.MarcadorMapa;
import org.jxmapviewer.viewer.GeoPosition;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import models.EstadoPunto;
import models.PuntoAsistencia;
import models.dao.PuntoAsistenciaDAO;
import views.PuntoAsistenciaView;

/**
 * Controla la ventana de puntos de asistencia (RF-09 y RF-10): lista y filtra los
 * puntos, y permite crear, actualizar, abrir o cerrar y eliminar. Las reglas de
 * validación del formulario están en PuntoAsistenciaFormulario.
 *
 * @author Equipo 7 - Puntos de Asistencia y Negocios
 */
public class PuntoAsistenciaController {

    private static final String SIN_CONEXION =
            "No se pudo acceder a la base de datos. Verifique que MySQL esté encendido en XAMPP.";

    private final PuntoAsistenciaView vista;
    private final PuntoAsistenciaDAO dao;
    private List<PuntoAsistencia> puntos = new ArrayList<>();

    public PuntoAsistenciaController(PuntoAsistenciaView vista) {
        this(vista, new PuntoAsistenciaDAO());
    }

    // sobrecarga de constructor: permite entregar otro DAO (por ejemplo, para pruebas)
    public PuntoAsistenciaController(PuntoAsistenciaView vista, PuntoAsistenciaDAO dao) {
        this.vista = vista;
        this.dao = dao;

        vista.getBtnNuevo().addActionListener(e -> nuevo());
        vista.getBtnGuardar().addActionListener(e -> guardar());
        vista.getBtnCambiarEstado().addActionListener(e -> cambiarEstado());
        vista.getBtnEliminar().addActionListener(e -> eliminar());
        vista.getCbxFiltroTipo().addActionListener(e -> mostrarTabla());
        vista.getMapViewer().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    usarPuntoDelMapa(e);
                }
            }
        });
        vista.getTblPuntos().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                mostrarSeleccionado();
            }
        });
        cargarPuntos();
    }

    // Lee los puntos de la base de datos y vuelve a dibujar la tabla
    private void cargarPuntos() {
        try {
            puntos = dao.obtenerTodosLosPuntos();
        } catch (RuntimeException e) {
            puntos = new ArrayList<>();
            error(SIN_CONEXION);
        }
        mostrarTabla();
    }

    // Dibuja en la tabla los puntos que pasan el filtro de tipo
    private void mostrarTabla() {
        DefaultTableModel modelo = vista.getModeloPuntos();
        modelo.setRowCount(0);
        String filtro = (String) vista.getCbxFiltroTipo().getSelectedItem();
        for (PuntoAsistencia p : PuntoAsistenciaFormulario.filtrarPorTipo(puntos, filtro)) {
            modelo.addRow(new Object[]{
                p.getId(),
                PuntoAsistenciaFormulario.codigoTipo(p),
                p.getNombre(),
                p.getLatitud(),
                p.getLongitud(),
                p.getEstado().name(),
                PuntoAsistenciaFormulario.serviciosParaTabla(p),
                p.getVoluntariosNecesarios()
            });
        }
    }

    // Al seleccionar una fila, el formulario muestra los datos de ese punto
    private void mostrarSeleccionado() {
        Integer id = idSeleccionado();
        if (id == null) {
            return;
        }
        PuntoAsistencia p = buscar(id);
        if (p == null) {
            return;
        }
        vista.getTxtNombre().setText(p.getNombre());
        vista.getTxtLatitud().setText(String.valueOf(p.getLatitud()));
        vista.getTxtLongitud().setText(String.valueOf(p.getLongitud()));
        vista.getCbxTipo().setSelectedItem(PuntoAsistenciaFormulario.codigoTipo(p));
        vista.getCbxEstado().setSelectedItem(p.getEstado().name());
        vista.getTxtServicios().setText(PuntoAsistenciaFormulario.serviciosParaFormulario(p));
        vista.getSpnVoluntarios().setValue(p.getVoluntariosNecesarios());
    }

    // Deja el formulario vacío para registrar un punto nuevo
    private void nuevo() {
        vista.getTblPuntos().clearSelection();
        vista.getTxtNombre().setText("");
        vista.getTxtLatitud().setText("");
        vista.getTxtLongitud().setText("");
        vista.getTxtServicios().setText("");
        vista.getCbxTipo().setSelectedIndex(0);
        vista.getCbxEstado().setSelectedIndex(0);
        vista.getSpnVoluntarios().setValue(0);
        vista.getTxtNombre().requestFocusInWindow();
    }

    // CREATE o UPDATE: si hay una fila seleccionada se actualiza ese punto, si no se crea uno nuevo
    private void guardar() {
        Integer id = idSeleccionado();
        try {
            PuntoAsistencia punto = PuntoAsistenciaFormulario.construirPunto(
                    id == null ? 0 : id,
                    (String) vista.getCbxTipo().getSelectedItem(),
                    vista.getTxtNombre().getText(),
                    vista.getTxtLatitud().getText(),
                    vista.getTxtLongitud().getText(),
                    (String) vista.getCbxEstado().getSelectedItem(),
                    vista.getTxtServicios().getText(),
                    ((Number) vista.getSpnVoluntarios().getValue()).intValue());
            boolean guardado = (id == null) ? dao.registrarPunto(punto) : dao.actualizarPunto(punto);
            if (guardado) {
                JOptionPane.showMessageDialog(vista, "Punto guardado.");
                cargarPuntos();
                nuevo();
            } else {
                error("No se pudo guardar el punto.");
            }
        } catch (IllegalArgumentException e) {
            aviso(e.getMessage());
        } catch (RuntimeException e) {
            error(SIN_CONEXION);
        }
    }

        // Un clic izquierdo en el mapa llena latitud y longitud del formulario
    private void usarPuntoDelMapa(MouseEvent e) {
        GeoPosition posicion = vista.getMapViewer().convertPointToGeoPosition(e.getPoint());
        // Al arrastrar el mapa, la longitud puede salirse de [-180, 180]
        double longitud = MarcadorMapa.normalizarLongitud(posicion.getLongitude());
        vista.getTxtLatitud().setText(String.format(Locale.US, "%.6f", posicion.getLatitude()));
        vista.getTxtLongitud().setText(String.format(Locale.US, "%.6f", longitud));
    }
    
    // UPDATE: abre el punto si está cerrado y lo cierra si está abierto (RF-09)
    private void cambiarEstado() {
        PuntoAsistencia p = seleccionado();
        if (p == null) {
            return;
        }
        EstadoPunto nuevoEstado = p.isAbierto() ? EstadoPunto.CERRADO : EstadoPunto.ABIERTO;
        try {
            if (dao.cambiarEstado(p.getId(), nuevoEstado)) {
                cargarPuntos();
            } else {
                error("No se pudo cambiar el estado del punto.");
            }
        } catch (RuntimeException e) {
            error(SIN_CONEXION);
        }
    }

    // DELETE: pide confirmación antes de borrar el punto
    private void eliminar() {
        PuntoAsistencia p = seleccionado();
        if (p == null) {
            return;
        }
        int respuesta = JOptionPane.showConfirmDialog(vista,
                "¿Eliminar \"" + p.getNombre() + "\"? Esta acción no se puede deshacer.",
                "Confirmar", JOptionPane.YES_NO_OPTION);
        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            if (dao.eliminarPunto(p.getId())) {
                cargarPuntos();
                nuevo();
            } else {
                error("No se pudo eliminar el punto.");
            }
        } catch (RuntimeException e) {
            error(SIN_CONEXION);
        }
    }

    // Punto de la fila seleccionada; si no hay selección avisa al usuario y devuelve null
    private PuntoAsistencia seleccionado() {
        Integer id = idSeleccionado();
        PuntoAsistencia p = (id == null) ? null : buscar(id);
        if (p == null) {
            aviso("Seleccione un punto de la lista.");
        }
        return p;
    }

    // Lee el ID (columna oculta 0 del modelo) de la fila seleccionada, o null si no hay selección
    private Integer idSeleccionado() {
        int fila = vista.getTblPuntos().getSelectedRow();
        if (fila < 0) {
            return null;
        }
        return (Integer) vista.getModeloPuntos()
                .getValueAt(vista.getTblPuntos().convertRowIndexToModel(fila), 0);
    }

    private PuntoAsistencia buscar(int id) {
        for (PuntoAsistencia p : puntos) {
            if (p.getId() == id) {
                return p;
            }
        }
        return null;
    }

    private void aviso(String mensaje) {
        JOptionPane.showMessageDialog(vista, mensaje, "Aviso", JOptionPane.WARNING_MESSAGE);
    }

    private void error(String mensaje) {
        JOptionPane.showMessageDialog(vista, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void iniciar() {
        vista.setVisible(true);
    }
}
