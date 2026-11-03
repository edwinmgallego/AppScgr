/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package views;

/**
 *
 * @author inged
 */


import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.TileFactoryInfo;

import javax.swing.*;
import java.awt.*;

public class MapaView extends JFrame {
    private JXMapViewer mapViewer;
    private JButton btnReportar;

 public MapaView() {
    setTitle("Sistema de Asistencia en Emergencias");
    
    // Forzar el User-Agent a nivel de protocolo HTTP en Java
    System.setProperty("http.agent", "AppScgr-POO-Cali/1.0");
    
    // Configuración para permitir conexiones HTTP a OpenStreetMap
    java.net.CookieHandler.setDefault(new java.net.CookieManager());
    
    setSize(800, 600);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLayout(new BorderLayout());

    // Configuración de OpenStreetMap
    mapViewer = new JXMapViewer();
    
    // Crear el proveedor de imágenes (TileFactoryInfo)
    TileFactoryInfo info = new OSMTileFactoryInfo() {
        @Override
        public String getTileUrl(int x, int y, int zoom) {
            // Reemplazamos http por https, OSM bloquea el tráfico no seguro a veces
            String url = super.getTileUrl(x, y, zoom);
            return url.replace("http://", "https://");
        }
    };
    
    DefaultTileFactory tileFactory = new DefaultTileFactory(info);
    mapViewer.setTileFactory(tileFactory);

    // Coordenadas de Cali
    GeoPosition cali = new GeoPosition(3.4516, -76.5320);
    mapViewer.setZoom(7);
    mapViewer.setAddressLocation(cali);

    btnReportar = new JButton("Reportar Incidente en esta zona");
    
    JPanel panelInferior = new JPanel();
    panelInferior.add(btnReportar);

    add(mapViewer, BorderLayout.CENTER);
    add(panelInferior, BorderLayout.SOUTH);
}

    public JXMapViewer getMapViewer() { return mapViewer; }
    public JButton getBtnReportar() { return btnReportar; }
}
