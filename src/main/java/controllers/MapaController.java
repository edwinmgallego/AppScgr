/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controllers;

/**
 *
 * @author inged
 */


import config.Sesion;
import models.Incidente;
import models.MarcadorMapa;
import models.dao.IncidenteDAO;
import views.FamiliaresView;
import views.LoginView;
import views.MapaView;
import views.MarcadorRenderer;
import views.MarcadorWaypoint;

import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.WaypointPainter;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.JOptionPane;

public class MapaController {
    private MapaView vista;
    private IncidenteDAO incidenteDAO;
    private final WaypointPainter<MarcadorWaypoint> pintorMarcadores = new WaypointPainter<>();
    private final List<MarcadorMapa> marcadoresIncidentes = new ArrayList<>();

    public MapaController(MapaView vista) {
        this.vista = vista;
        this.incidenteDAO = new IncidenteDAO();
        pintorMarcadores.setRenderer(new MarcadorRenderer());
        vista.getMapViewer().setOverlayPainter(pintorMarcadores);
        inicializarEventos();
        cargarIncidentesEnMapa();
    }

    private void inicializarEventos() {
        // EVENTO 1: Capturar latitud y longitud al hacer clic izquierdo en el mapa
        vista.getMapViewer().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) { // Solo clic izquierdo
                    // Convertir el punto del píxel de la pantalla a coordenadas GPS
                    GeoPosition posicion = vista.getMapViewer().convertPointToGeoPosition(e.getPoint());
                    vista.getTxtLatitud().setText(String.valueOf(posicion.getLatitude()));
                    vista.getTxtLongitud().setText(String.valueOf(posicion.getLongitude()));
                }
            }
        });

        // EVENTO 2: Guardar en la base de datos (CREATE)
        vista.getBtnGuardar().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (vista.getTxtLatitud().getText().isEmpty()) {
                    JOptionPane.showMessageDialog(vista, "Haga clic en el mapa para seleccionar la ubicación.", "Aviso", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Recolectar datos del formulario
                String tipo = vista.getCbxTipoIncidente().getSelectedItem().toString();
                String descripcion = vista.getTxtDescripcion().getText();
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
                    JOptionPane.showMessageDialog(vista, "Error al guardar en la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // EVENTO 3: Limpiar formulario
        vista.getBtnLimpiar().addActionListener(e -> limpiarFormulario());

        // EVENTO 4: Abrir la red familiar (Equipo 1)
        vista.getItemRedFamiliar().addActionListener(e -> new FamiliaresController(new FamiliaresView(vista)).iniciar());

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
        });}
    // Método READ: Pinta en el mapa los incidentes de la BD, cada tipo con su color
    private void cargarIncidentesEnMapa() {
        marcadoresIncidentes.clear();
        for (Incidente inc : incidenteDAO.obtenerTodosLosIncidentes()) {
            try {
                marcadoresIncidentes.add(MarcadorMapa.desdeIncidente(inc));
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
        pintorMarcadores.setWaypoints(waypoints);
        vista.getMapViewer().repaint();
    }

    private void limpiarFormulario() {
        vista.getCbxTipoIncidente().setSelectedIndex(0);
        vista.getTxtDescripcion().setText("");
        vista.getTxtLatitud().setText("");
        vista.getTxtLongitud().setText("");
    }

    public void iniciar() {
        if (Sesion.hayUsuario()) {
            vista.setTitle(vista.getTitle() + " - " + Sesion.getUsuarioActual().getNombreCompleto());
        }
        vista.setVisible(true);
    }
}