package controllers;

/**
 *
 * @author sebas
 */
import models.MarcadorMapa;
import models.TipoMarcador;
import models.dao.DañoEstructuralDAO;
import models.daños.DañoEstructural;
import models.daños.DañoGenerador;
import models.daños.FallaTanque;
import models.daños.Grieta;
import models.daños.TipoDaño;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.painter.CompoundPainter;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.DefaultWaypoint;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.Waypoint;
import org.jxmapviewer.viewer.WaypointPainter;
import views.MarcadorRenderer;
import views.MarcadorWaypoint;
import views.daños.DañosEstructuralesView;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DañosController {

    private DañosEstructuralesView vista;
    private DañoEstructuralDAO dañoDAO;
    private int usuarioId;
    private List<DañoEstructural> dañosEnTabla = new ArrayList<>();
    private DañoEstructural seleccionado; // el que se selecciona en la tabla
    // Capas del mapa: los daños guardados (pines de siempre) y el punto que el usuario marca con clic.
    // El punto marcado se dibuja igual que en el módulo de incidentes (MarcadorMapa SELECCION + MarcadorRenderer).
    private final WaypointPainter<Waypoint> pintorDaños = new WaypointPainter<>();
    private final WaypointPainter<MarcadorWaypoint> pintorSeleccion = new WaypointPainter<>();
    // por ahora usamos el usuario 1 del script del profe

    public DañosController(DañosEstructuralesView vista) {
        this(vista, 1);
    }
    // este es el que va a usar el equipo 9 con el usuario logueado

    public DañosController(DañosEstructuralesView vista, int usuarioId) {
        this.vista = vista;
        this.usuarioId = usuarioId;
        this.dañoDAO = new DañoEstructuralDAO();
        configurarCapasMapa();
        inicializarEventos();
    }

    public void iniciar() {
        cargarDaños();
        vista.setVisible(true);
    }

    private void inicializarEventos() {
        // EVENTO 1: clic en el mapa para sacar las coordenadas
        vista.getMapViewer().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    // Igual que en el módulo de incidentes: píxel -> coordenadas GPS y,
                    // como el mapa se repite al arrastrarlo, la longitud se lleva a [-180, 180]
                    GeoPosition posicion = vista.getMapViewer().convertPointToGeoPosition(e.getPoint());
                    double longitud = MarcadorMapa.normalizarLongitud(posicion.getLongitude());
                    marcarUbicacion(posicion.getLatitude(), longitud);
                }
            }
        });
        // EVENTO 2: cuando cambian el tipo se muestran sus campos
        vista.getCbxTipo().addActionListener(e -> vista.mostrarCampos(tipoElegido()));
        // EVENTO 3: botones
        vista.getBtnGuardar().addActionListener(e -> guardar());
        vista.getBtnActualizar().addActionListener(e -> actualizar());
        vista.getBtnEliminar().addActionListener(e -> eliminar());
        vista.getBtnLimpiar().addActionListener(e -> limpiarFormulario());
        // EVENTO 4: filtro de la tabla
        vista.getCbxFiltro().addActionListener(e -> cargarDaños());
        // EVENTO 5: al dar clic en una fila se llena el formulario
        vista.getTabla().getSelectionModel().addListSelectionListener(e -> {
            int fila = vista.getTabla().getSelectedRow();
            if (!e.getValueIsAdjusting() && fila >= 0) {
                cargarEnFormulario(dañosEnTabla.get(fila));
            }
        });
    }
    // CREATE

    private void guardar() {
        try {
            DañoEstructural daño = leerFormulario();
            if (dañoDAO.insertar(daño) > 0) {
                String mensaje = "Daño guardado. Riesgo: " + daño.calcularNivelRiesgo();
                if (daño.requiereEvacuacion()) {
                    mensaje += "\nSe recomienda evacuar el inmueble.";
                }
                vista.mostrarMensaje(mensaje);
                limpiarFormulario();
                cargarDaños();
            } else {
                vista.mostrarMensaje("Error al guardar en la base de datos.");
            }
        } catch (IllegalArgumentException ex) {
            vista.mostrarMensaje(ex.getMessage());
        }
    }
    // UPDATE

    private void actualizar() {
        if (seleccionado == null) {
            vista.mostrarMensaje("Seleccione un daño de la tabla.");
            return;
        }
        try {
            DañoEstructural daño = leerFormulario();
            daño.setId(seleccionado.getId());
            if (dañoDAO.actualizar(daño)) {
                vista.mostrarMensaje("Daño actualizado.");
                limpiarFormulario();
                cargarDaños();
            } else {
                vista.mostrarMensaje("No se pudo actualizar (el tipo de daño no se puede cambiar).");
            }
        } catch (IllegalArgumentException ex) {
            vista.mostrarMensaje(ex.getMessage());
        }
    }
    // DELETE

    private void eliminar() {
        if (seleccionado == null) {
            vista.mostrarMensaje("Seleccione un daño de la tabla.");
            return;
        }
        if (vista.confirmar("¿Eliminar el reporte #" + seleccionado.getId() + "?")) {
            dañoDAO.eliminar(seleccionado.getId());
            limpiarFormulario();
            cargarDaños();
        }
    }
    // READ: llena la tabla y pone los puntos en el mapa

    private void cargarDaños() {
        Object filtro = vista.getCbxFiltro().getSelectedItem();
        if (filtro instanceof TipoDaño tipo) {
            dañosEnTabla = dañoDAO.listar(tipo);
        } else {
            dañosEnTabla = dañoDAO.listar();
        }
        vista.getModeloTabla().setRowCount(0);
        Set<Waypoint> puntos = new HashSet<>();
        for (DañoEstructural d : dañosEnTabla) {
            // aqui cada daño calcula su riesgo a su manera (polimorfismo)
            vista.getModeloTabla().addRow(new Object[]{
                d.getId(), d.getTipoDaño(), d.getBarrio(), d.getDireccion(), d.getDetalle(), d.calcularNivelRiesgo()
            });
            puntos.add(new DefaultWaypoint(new GeoPosition(d.getLatitud(), d.getLongitud())));
        }
        pintorDaños.setWaypoints(puntos);
        vista.getMapViewer().repaint();
    }
    // Lee el formulario y crea la grieta, el tanque o el generador

    private DañoEstructural leerFormulario() {
        if (vista.getTxtLatitud().getText().isEmpty()) {
            throw new IllegalArgumentException("Haga clic en el mapa para indicar la ubicación.");
        }
        String direccion = vista.getTxtDireccion().getText();
        String barrio = vista.getTxtBarrio().getText();
        double lat = Double.parseDouble(vista.getTxtLatitud().getText());
        double lon = Double.parseDouble(vista.getTxtLongitud().getText());
        String descripcion = vista.getTxtDescripcion().getText();
        return switch (tipoElegido()) {
            case GRIETA ->
                new Grieta(usuarioId, direccion, barrio, lat, lon, descripcion,
                (String) vista.getCbxElemento().getSelectedItem(),
                leerNumero(vista.getTxtAncho().getText(), "el ancho"),
                leerNumero(vista.getTxtLargo().getText(), "el largo"));
            case TANQUE_AGUA ->
                new FallaTanque(usuarioId, direccion, barrio, lat, lon, descripcion,
                (String) vista.getCbxFallaTanque().getSelectedItem(),
                (int) leerNumero(vista.getTxtCapacidad().getText(), "la capacidad"),
                vista.getChkElevado().isSelected());
            case GENERADOR ->
                new DañoGenerador(usuarioId, direccion, barrio, lat, lon, descripcion,
                (String) vista.getCbxFallaGenerador().getSelectedItem(),
                leerNumero(vista.getTxtPotencia().getText(), "la potencia"),
                vista.getChkServicioCritico().isSelected());
        };
    }
    // Pasa los datos de un daño al formulario para editarlo

    private void cargarEnFormulario(DañoEstructural d) {
        seleccionado = d;
        vista.getCbxTipo().setSelectedItem(d.getTipoDaño());
        vista.getTxtDireccion().setText(d.getDireccion());
        vista.getTxtBarrio().setText(d.getBarrio());
        marcarUbicacion(d.getLatitud(), d.getLongitud());
        vista.getTxtDescripcion().setText(d.getDescripcion());
        if (d instanceof Grieta g) {
            vista.getCbxElemento().setSelectedItem(g.getElemento());
            vista.getTxtAncho().setText(String.valueOf(g.getAnchoMm()));
            vista.getTxtLargo().setText(String.valueOf(g.getLargoCm()));
        } else if (d instanceof FallaTanque t) {
            vista.getCbxFallaTanque().setSelectedItem(t.getTipoFalla());
            vista.getTxtCapacidad().setText(String.valueOf(t.getCapacidadLitros()));
            vista.getChkElevado().setSelected(t.isElevado());
        } else if (d instanceof DañoGenerador gen) {
            vista.getCbxFallaGenerador().setSelectedItem(gen.getTipoFalla());
            vista.getTxtPotencia().setText(String.valueOf(gen.getPotenciaKva()));
            vista.getChkServicioCritico().setSelected(gen.isServicioCritico());
        }
    }

    private void limpiarFormulario() {
        seleccionado = null;
        vista.getTabla().clearSelection();
        vista.getTxtDireccion().setText("");
        vista.getTxtBarrio().setText("");
        vista.getTxtLatitud().setText("");
        vista.getTxtLongitud().setText("");
        quitarMarcador();
        vista.getTxtDescripcion().setText("");
        vista.getTxtAncho().setText("");
        vista.getTxtLargo().setText("");
        vista.getTxtCapacidad().setText("");
        vista.getTxtPotencia().setText("");
        vista.getChkElevado().setSelected(false);
        vista.getChkServicioCritico().setSelected(false);
    }
    // Las dos capas se pintan juntas: primero los daños guardados y encima el punto marcado

    private void configurarCapasMapa() {
        pintorSeleccion.setRenderer(new MarcadorRenderer());
        List<Painter<JXMapViewer>> capas = new ArrayList<>();
        capas.add(pintorDaños);
        capas.add(pintorSeleccion);
        vista.getMapViewer().setOverlayPainter(new CompoundPainter<>(capas));
    }

    /**
     * Señala en el mapa el lugar del daño y deja sus coordenadas en el
     * formulario (son las que lee leerFormulario() y se guardan en el reporte).
     */
    void marcarUbicacion(double latitud, double longitud) {
        MarcadorMapa punto;
        try {
            punto = new MarcadorMapa(latitud, longitud, TipoMarcador.SELECCION);
        } catch (IllegalArgumentException ex) {
            return; // coordenadas fuera de rango: no se marca nada
        }
        vista.getTxtLatitud().setText(String.valueOf(latitud));
        vista.getTxtLongitud().setText(String.valueOf(longitud));
        Set<MarcadorWaypoint> seleccion = new HashSet<>();
        seleccion.add(new MarcadorWaypoint(punto));
        pintorSeleccion.setWaypoints(seleccion);
        vista.getMapViewer().repaint();
    }

    private void quitarMarcador() {
        pintorSeleccion.setWaypoints(new HashSet<>());
        vista.getMapViewer().repaint();
    }

    private TipoDaño tipoElegido() {
        return (TipoDaño) vista.getCbxTipo().getSelectedItem();
    }
    // para que acepte 4,5 o 4.5

    static double leerNumero(String texto, String campo) {
        try {
            return Double.parseDouble(texto.trim().replace(',', '.'));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Escriba un número válido para " + campo + ".");
        }
    }
}