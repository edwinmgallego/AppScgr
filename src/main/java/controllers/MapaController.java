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
import views.MapaView;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;

public class MapaController {
    private MapaView vista;

    public MapaController(MapaView vista) {
        this.vista = vista;
        inicializarEventos();
    }

    private void inicializarEventos() {
        vista.getBtnReportar().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Aquí los estudiantes deben integrar la lógica para capturar lat/lng
                // e insertarlo en MySQL usando JDBC.
                JOptionPane.showMessageDialog(vista, "Funcionalidad de reporte lista para ser implementada con MySQL.");
            }
        });
    }

    public void iniciar() {
        vista.setVisible(true);
    }
}
