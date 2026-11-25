package controllers;

/**
 *
 * @author inged
 */
import config.Sesion;
import models.Incidente;
import models.MarcadorMapa;
import models.TipoMarcador;
import models.dao.IncidenteDAO;
import models.EstacionMio;
import models.EstadoVia;
import models.dao.EstacionMioDAO;
import views.EstadoView;
import views.FamiliaresView;
import views.LoginView;
import views.MapaView;
import views.PerfilView;
import views.MarcadorRenderer;
import views.MarcadorWaypoint;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.WaypointPainter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.swing.JOptionPane;

public class MapaController {

    private MapaView vista;
    private IncidenteDAO incidenteDAO;
    private final WaypointPainter<MarcadorWaypoint> pintorMarcadores = new WaypointPainter<>();
    private final List<MarcadorMapa> marcadoresIncidentes = new ArrayList<>();
    private MarcadorMapa puntoSeleccionado;
    private final List<SeleccionListener> oyentesSeleccion = new ArrayList<>();
    private final List<Incidente> incidentes = new ArrayList<>();
    private Incidente incidenteSeleccionado; // null = se está creando un reporte nuevo
    private static final double DISTANCIA_CLIC_PX = 12;
    private EstadoController estadoController; // Equipo 2: se crea la primera vez que se abre
    private EstacionMioDAO estacionMioDAO;
    private final List<MarcadorMapa> marcadoresEstaciones = new ArrayList<>();

    public MapaController(MapaView vista) {
        this.vista = vista;
        this.incidenteDAO = new IncidenteDAO();
        pintorMarcadores.setRenderer(new MarcadorRenderer());
        vista.getMapViewer().setOverlayPainter(pintorMarcadores);
        inicializarEventos();
        cargarIncidentesEnMapa();
        this.estacionMioDAO = new EstacionMioDAO();
        cargarEstacionesEnMapa();
    }

    private void inicializarEventos() {
        // EVENTO 1: Capturar latitud y longitud al hacer clic izquierdo en el mapa
        vista.getMapViewer().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) { // Solo clic izquierdo
                    Incidente cercano = buscarIncidenteCerca(e.getPoint());
                    if (cercano != null) {
                        cargarEnFormulario(cercano);
                        return; // Se hizo clic en un reporte existente
                    }
                    // Convertir el punto del píxel de la pantalla a coordenadas GPS
                    GeoPosition posicion = vista.getMapViewer().convertPointToGeoPosition(e.getPoint());
                    // Al arrastrar el mapa, la longitud puede salirse de [-180, 180]
                    double longitud = MarcadorMapa.normalizarLongitud(posicion.getLongitude());
                    vista.getTxtLatitud().setText(String.valueOf(posicion.getLatitude()));
                    vista.getTxtLongitud().setText(String.valueOf(longitud));
                    seleccionarPunto(posicion.getLatitude(), longitud);
                }
            }
        });

        // EVENTO 2: Guardar en la base de datos (CREATE)
        vista.getBtnGuardar().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String error = validarFormulario(
                        vista.getTxtLatitud().getText(),
                        (String) vista.getCbxTipoIncidente().getSelectedItem(),
                        vista.getTxtDescripcion().getText());
                if (error != null) {
                    JOptionPane.showMessageDialog(vista, error, "Aviso", JOptionPane.WARNING_MESSAGE);
                    return; // Si hay error, no se guarda nada
                }
                // Recolectar datos del formulario
                String tipo = vista.getCbxTipoIncidente().getSelectedItem().toString();
                String descripcion = vista.getTxtDescripcion().getText().trim();
                double lat = Double.parseDouble(vista.getTxtLatitud().getText());
                double lon = Double.parseDouble(vista.getTxtLongitud().getText());

                // Crear objeto Modelo
                Incidente nuevoIncidente = new Incidente(0, tipo, lat, lon, descripcion);

                // Enviar al DAO con el id del usuario que inició sesión
                if (incidenteDAO.registrarIncidente(nuevoIncidente, Sesion.getUsuarioActual().getId())) {
                    JOptionPane.showMessageDialog(vista, "Incidente registrado exitosamente.");
                    limpiarFormulario();
                    cargarIncidentesEnMapa(); // Refrescar los pines
                } else {
                    JOptionPane.showMessageDialog(vista, "Error al guardar en la base de datos. ", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // EVENTO 3: Limpiar formulario
        vista.getBtnLimpiar().addActionListener(e -> limpiarFormulario());

        // EVENTO 3.1: Actualizar el reporte seleccionado (UPDATE) - Equipo 3
        vista.getBtnActualizar().addActionListener(e -> actualizarReporte());

        // EVENTO 3.2: Eliminar el reporte seleccionado (DELETE) - Equipo 3
        vista.getBtnEliminar().addActionListener(e -> eliminarReporte());

        // EVENTO 3.3: Reabrir o volver a cerrar la vía seleccionada (RF-03) - Equipo 3
        vista.getBtnCambiarEstadoVia().addActionListener(e -> alternarEstadoVia());

        // EVENTO 4: Abrir la red familiar (Equipo 1)
        vista.getItemRedFamiliar().addActionListener(e -> new FamiliaresController(new FamiliaresView(vista)).iniciar());

        // EVENTO 4.2 (Equipo 2): reportar mi estado de supervivencia.
        // Se crea una sola vez para que la cola de mensajes pendientes se conserve mientras el mapa esté abierto.
        vista.getItemEstado().addActionListener(e -> {
            if (estadoController == null) {
                estadoController = new EstadoController(new EstadoView(), this::ubicacionSeleccionada);
            }
            estadoController.iniciar();
        });

        // EVENTO 4.1: Abrir mi perfil (editar datos y cambiar contraseña)
        vista.getItemPerfil().addActionListener(e -> new PerfilController(new PerfilView(vista)).iniciar());

        // EVENTO 5: Cerrar sesión y volver al login (Equipo 1), preguntando antes
        vista.getItemCerrarSesion().addActionListener(e -> {
            int respuesta = JOptionPane.showConfirmDialog(vista, "¿Seguro que desea cerrar sesión?",
                    "Cerrar sesión", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (respuesta != JOptionPane.YES_OPTION) {
                return; // Dijo que no (o cerró la ventanita): se queda en el mapa
            }
            Sesion.cerrar();
            vista.dispose();
            new LoginController(new LoginView()).iniciar();
        });
    }

    // Equipo 2: coordenadas que el usuario marcó con clic en el mapa (null si todavía no hizo clic)
    private double[] ubicacionSeleccionada() {
        try {
            String lat = vista.getTxtLatitud().getText().trim();
            String lon = vista.getTxtLongitud().getText().trim();
            if (lat.isEmpty() || lon.isEmpty()) {
                return null;
            }
            return new double[]{Double.parseDouble(lat), Double.parseDouble(lon)};
        } catch (NumberFormatException e) {
            return null;
        }
    }

    // Método READ: Pinta en el mapa los incidentes de la BD, cada tipo con su color
    private void cargarIncidentesEnMapa() {
        marcadoresIncidentes.clear();
        incidentes.clear();
        for (Incidente inc : incidenteDAO.obtenerTodosLosIncidentes()) {
            try {
                marcadoresIncidentes.add(MarcadorMapa.desdeIncidente(inc));
                incidentes.add(inc); // Se guarda para poder encontrarlo al hacer clic
            } catch (IllegalArgumentException ex) {
                // Un registro con coordenadas inválidas no debe impedir pintar los demás
                System.err.println("Incidente " + inc.getId() + " omitido: " + ex.getMessage());
            }
        }
        pintarMarcadores();
    }

    private void pintarMarcadores() {
        Set<MarcadorWaypoint> waypoints = new HashSet<>();
        for (MarcadorMapa marcador : marcadoresIncidentes) {
            waypoints.add(new MarcadorWaypoint(marcador));
        }
        if (puntoSeleccionado != null) {
            waypoints.add(new MarcadorWaypoint(puntoSeleccionado));
        }
        for (MarcadorMapa marcador : marcadoresEstaciones) {
            waypoints.add(new MarcadorWaypoint(marcador));
        }
        pintorMarcadores.setWaypoints(waypoints);
        vista.getMapViewer().repaint();
        
    }

    /**
     * Marca el punto clicado en el mapa y avisa a quienes necesiten sus
     * coordenadas.
     */
    private void seleccionarPunto(double latitud, double longitud) {
        puntoSeleccionado = new MarcadorMapa(latitud, longitud, TipoMarcador.SELECCION);
        pintarMarcadores();
        for (SeleccionListener oyente : List.copyOf(oyentesSeleccion)) {
            oyente.alSeleccionar(latitud, longitud);
        }
    }

    private void limpiarSeleccion() {
        if (puntoSeleccionado != null) {
            puntoSeleccionado = null;
            pintarMarcadores();
            for (SeleccionListener oyente : List.copyOf(oyentesSeleccion)) {
                oyente.alLimpiarSeleccion();
            }
        }
    }

    /**
     * Punto de enganche para otros módulos que necesiten las coordenadas del
     * clic.
     */
    public void addSeleccionListener(SeleccionListener oyente) {
        oyentesSeleccion.add(Objects.requireNonNull(oyente, "El oyente es obligatorio."));
    }

    /** Borra el reporte seleccionado, preguntando antes para evitar errores. */
    private void eliminarReporte() {
        if (incidenteSeleccionado == null) {
            return; // No hay ningún reporte seleccionado
        }
        int respuesta = JOptionPane.showConfirmDialog(vista,
                "¿Seguro que desea eliminar este reporte? Esta acción no se puede deshacer.",
                "Eliminar reporte", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (respuesta != JOptionPane.YES_OPTION) {
            return; // Dijo que no: no se borra nada
        }

        if (incidenteDAO.eliminarIncidente(incidenteSeleccionado.getId())) {
            JOptionPane.showMessageDialog(vista, "Reporte eliminado.");
            limpiarFormulario();
            cargarIncidentesEnMapa(); // El marcador desaparece del mapa
        } else {
            JOptionPane.showMessageDialog(vista, "Error al eliminar en la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Guarda en la base de datos los cambios del reporte seleccionado. */
    private void actualizarReporte() {
        if (incidenteSeleccionado == null) {
            return; // No hay ningún reporte seleccionado
        }
        String tipo = (String) vista.getCbxTipoIncidente().getSelectedItem();
        String error = validarFormulario(vista.getTxtLatitud().getText(), tipo, vista.getTxtDescripcion().getText());
        if (error != null) {
            JOptionPane.showMessageDialog(vista, error, "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Se crea un objeto con el mismo id y los datos nuevos del formulario
        Incidente cambios = new Incidente(
                incidenteSeleccionado.getId(),
                tipo,
                Double.parseDouble(vista.getTxtLatitud().getText()),
                Double.parseDouble(vista.getTxtLongitud().getText()),
                vista.getTxtDescripcion().getText().trim());

        if (incidenteDAO.actualizarIncidente(cambios)) {
            JOptionPane.showMessageDialog(vista, "Reporte actualizado exitosamente.");
            limpiarFormulario();
            cargarIncidentesEnMapa(); // Refrescar los marcadores
        } else {
            JOptionPane.showMessageDialog(vista, "Error al actualizar en la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Reabre la vía seleccionada si está cerrada, o la vuelve a cerrar si está abierta (RF-03).
     * Solo aplica a vías cerradas y derrumbes: en los demás tipos la columna 'estado' la usa otro equipo.
     */
    private void alternarEstadoVia() {
        if (incidenteSeleccionado == null || !incidenteSeleccionado.afectaVia()) {
            return;
        }
        EstadoVia nuevoEstado = incidenteSeleccionado.getEstadoVia().opuesto();
        if (cambiarEstadoVia(incidenteSeleccionado.getId(), nuevoEstado)) {
            JOptionPane.showMessageDialog(vista, "La vía quedó marcada como " + nuevoEstado.getEtiqueta().toLowerCase() + ".");
            limpiarFormulario();
        } else {
            JOptionPane.showMessageDialog(vista, "Error al cambiar el estado de la vía.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Busca el reporte cuyo marcador está más cerca del clic, o null si no hay ninguno cerca. */
    private Incidente buscarIncidenteCerca(Point clic) {
        Incidente masCercano = null;
        double menorDistancia = DISTANCIA_CLIC_PX;
        for (Incidente inc : incidentes) {
            Point2D punto = vista.getMapViewer().convertGeoPositionToPoint(
                    new GeoPosition(inc.getLatitud(), inc.getLongitud()));
            double distancia = punto.distance(clic);
            if (distancia <= menorDistancia) {
                menorDistancia = distancia;
                masCercano = inc;
            }
        }
        return masCercano;
    }

    /** Llena el formulario con un reporte existente y activa Actualizar/Eliminar. */
    private void cargarEnFormulario(Incidente inc) {
        incidenteSeleccionado = inc;
        vista.getCbxTipoIncidente().setSelectedItem(inc.getTipo());
        vista.getTxtDescripcion().setText(inc.getDescripcion());
        vista.getTxtLatitud().setText(String.valueOf(inc.getLatitud()));
        vista.getTxtLongitud().setText(String.valueOf(inc.getLongitud()));
        vista.getBtnGuardar().setEnabled(false);
        vista.getBtnActualizar().setEnabled(true);
        vista.getBtnEliminar().setEnabled(true);
        vista.getBtnCambiarEstadoVia().setEnabled(inc.afectaVia());
        vista.getBtnCambiarEstadoVia().setText(textoBotonEstadoVia(inc));
        seleccionarPunto(inc.getLatitud(), inc.getLongitud());
    }

    /** Texto del botón de estado de vía según el estado actual del reporte. */
    static String textoBotonEstadoVia(Incidente inc) {
        return inc != null && inc.estaAbierta() ? MapaView.TEXTO_CERRAR_VIA : MapaView.TEXTO_REABRIR_VIA;
    }

    /** Devuelve el mensaje de error, o null si el formulario está bien. */
    static String validarFormulario(String latitud, String tipo, String descripcion) {
        if (latitud == null || latitud.isBlank()) {
            return "Haga clic en el mapa para seleccionar la ubicación.";
        }
        if (tipo == null || tipo.isBlank()) {
            return "Seleccione el tipo de incidente.";
        }
        if (descripcion == null || descripcion.isBlank()) {
            return "Escriba una descripción del incidente.";
        }
        return null;
    }

    private void limpiarFormulario() {
        vista.getCbxTipoIncidente().setSelectedIndex(0);
        vista.getTxtDescripcion().setText("");
        vista.getTxtLatitud().setText("");
        vista.getTxtLongitud().setText("");
        incidenteSeleccionado = null;
        vista.getBtnGuardar().setEnabled(true);
        vista.getBtnActualizar().setEnabled(false);
        vista.getBtnEliminar().setEnabled(false);
        vista.getBtnCambiarEstadoVia().setEnabled(false);
        vista.getBtnCambiarEstadoVia().setText(MapaView.TEXTO_REABRIR_VIA);
        limpiarSeleccion();
    }

    public void iniciar() {
        if (Sesion.hayUsuario()) {
            vista.setTitle(vista.getTitle() + " - " + Sesion.getUsuarioActual().getNombreCompleto());
        }
        vista.setVisible(true);
    }

    private void cargarEstacionesEnMapa() {
        marcadoresEstaciones.clear();
        for (EstacionMio est : estacionMioDAO.listarTodas()) {
            try {
                marcadoresEstaciones.add(MarcadorMapa.desdeEstacion(est));
            } catch (IllegalArgumentException ex) {
                System.err.println("Estación " + est.getId() + " omitida: " + ex.getMessage());
            }
        }
        pintarMarcadores();
    }

    public boolean cambiarEstadoVia(int idIncidente, EstadoVia nuevoEstado) {
        boolean exito = incidenteDAO.actualizarEstado(idIncidente, nuevoEstado);
        if (exito) {
            cargarIncidentesEnMapa();
        }
        return exito;
    }
}