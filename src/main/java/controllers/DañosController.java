/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controllers;

/**
 *
 * @author sebas
 */
import models.dao.DañoEstructuralDAO;
import models.daños.DañoEstructural;
import models.daños.DañoGenerador;
import models.daños.FallaTanque;
import models.daños.Grieta;
import models.daños.TipoDaño;
import org.jxmapviewer.viewer.DefaultWaypoint;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.Waypoint;
import org.jxmapviewer.viewer.WaypointPainter;
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
 
    // por ahora usamos el usuario 1 del script del profe
    public DañosController(DañosEstructuralesView vista) {
        this(vista, 1);
    }
 
    // este es el que va a usar el equipo 9 con el usuario logueado
    public DañosController(DañosEstructuralesView vista, int usuarioId) {
        this.vista = vista;
        this.usuarioId = usuarioId;
        this.dañoDAO = new DañoEstructuralDAO();
        inicializarEventos();
    }
 
    public void iniciar() {
        vista.setVisible(true);
    }
 
    private void inicializarEventos() {
        // EVENTO 1: clic en el mapa para sacar las coordenadas
        vista.getMapViewer().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    GeoPosition posicion = vista.getMapViewer().convertPointToGeoPosition(e.getPoint());
                    vista.getTxtLatitud().setText(String.valueOf(posicion.getLatitude()));
                    vista.getTxtLongitud().setText(String.valueOf(posicion.getLongitude()));
                }
            }
        });
 
        // EVENTO 2: cuando cambian el tipo se muestran sus campos
        vista.getCbxTipo().addActionListener(e -> vista.mostrarCampos(tipoElegido()));
 
        // EVENTO 3: botones
        vista.getBtnGuardar().addActionListener(e -> guardar());
        vista.getBtnLimpiar().addActionListener(e -> limpiarFormulario());
 
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
            } else {
                vista.mostrarMensaje("Error al guardar en la base de datos.");
            }
        } catch (IllegalArgumentException ex) {
            vista.mostrarMensaje(ex.getMessage());
        }
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
            case GRIETA -> new Grieta(usuarioId, direccion, barrio, lat, lon, descripcion,
                    (String) vista.getCbxElemento().getSelectedItem(),
                    leerNumero(vista.getTxtAncho().getText(), "el ancho"),
                    leerNumero(vista.getTxtLargo().getText(), "el largo"));
            case TANQUE_AGUA -> new FallaTanque(usuarioId, direccion, barrio, lat, lon, descripcion,
                    (String) vista.getCbxFallaTanque().getSelectedItem(),
                    (int) leerNumero(vista.getTxtCapacidad().getText(), "la capacidad"),
                    vista.getChkElevado().isSelected());
            case GENERADOR -> new DañoGenerador(usuarioId, direccion, barrio, lat, lon, descripcion,
                    (String) vista.getCbxFallaGenerador().getSelectedItem(),
                    leerNumero(vista.getTxtPotencia().getText(), "la potencia"),
                    vista.getChkServicioCritico().isSelected());
        };
    }
 
    private void limpiarFormulario() {
        vista.getTxtDireccion().setText("");
        vista.getTxtBarrio().setText("");
        vista.getTxtLatitud().setText("");
        vista.getTxtLongitud().setText("");
        vista.getTxtDescripcion().setText("");
        vista.getTxtAncho().setText("");
        vista.getTxtLargo().setText("");
        vista.getTxtCapacidad().setText("");
        vista.getTxtPotencia().setText("");
        vista.getChkElevado().setSelected(false);
        vista.getChkServicioCritico().setSelected(false);
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

