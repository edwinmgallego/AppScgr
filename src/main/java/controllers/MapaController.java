/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controllers;

/**
 *
 * @author inged
 */


import models.Incidente;
import models.dao.IncidenteDAO;
import views.MapaView;

import org.jxmapviewer.viewer.DefaultWaypoint;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.Waypoint;
import org.jxmapviewer.viewer.WaypointPainter;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.JOptionPane;

public class MapaController {
    private MapaView vista;
    private IncidenteDAO incidenteDAO;

    public MapaController(MapaView vista) {
        this.vista = vista;
        this.incidenteDAO = new IncidenteDAO();
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

                // Enviar al DAO (El '1' es el idUsuarioLogueado de prueba que creamos)
                if (incidenteDAO.registrarIncidente(nuevoIncidente, 1)) {
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
    }

    // Método READ: Pinta los puntos de la BD en el mapa
    private void cargarIncidentesEnMapa() {
        List<Incidente> listaIncidentes = incidenteDAO.obtenerTodosLosIncidentes();
        Set<Waypoint> waypoints = new HashSet<>();
        
        for (Incidente inc : listaIncidentes) {
            waypoints.add(new DefaultWaypoint(new GeoPosition(inc.getLatitud(), inc.getLongitud())));
        }
        
        WaypointPainter<Waypoint> waypointPainter = new WaypointPainter<>();
        waypointPainter.setWaypoints(waypoints);
        vista.getMapViewer().setOverlayPainter(waypointPainter);
        vista.getMapViewer().repaint();
    }

    private void limpiarFormulario() {
        vista.getCbxTipoIncidente().setSelectedIndex(0);
        vista.getTxtDescripcion().setText("");
        vista.getTxtLatitud().setText("");
        vista.getTxtLongitud().setText("");
    }

    public void iniciar() {
        vista.setVisible(true);
    }
}