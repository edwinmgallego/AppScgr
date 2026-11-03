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
    private JComboBox<String> cbxTipoIncidente;
    private JTextArea txtDescripcion;
    private JTextField txtLatitud;
    private JTextField txtLongitud;
    private JButton btnGuardar;
    private JButton btnLimpiar;

    public MapaView() {
        setTitle("Sistema de Asistencia en Emergencias");
        System.setProperty("http.agent", "AppScgr-POO-Cali/1.0");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // --- 1. CONFIGURACIÓN DEL MAPA ---
        mapViewer = new JXMapViewer();
        TileFactoryInfo info = new OSMTileFactoryInfo() {
            @Override
            public String getTileUrl(int x, int y, int zoom) {
                return super.getTileUrl(x, y, zoom).replace("http://", "https://");
            }
        };
        mapViewer.setTileFactory(new DefaultTileFactory(info));
        mapViewer.setZoom(7);
        mapViewer.setAddressLocation(new GeoPosition(3.4516, -76.5320)); // Cali

        // --- 2. CONFIGURACIÓN DEL FORMULARIO LATERAL (CRUD) ---
        JPanel panelCrud = new JPanel(new GridBagLayout());
        panelCrud.setBorder(BorderFactory.createTitledBorder("Gestión de Incidente"));
        panelCrud.setPreferredSize(new Dimension(300, 0));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0;

        // Tipo de Incidente
        gbc.gridy = 0; panelCrud.add(new JLabel("Tipo de Incidente:"), gbc);
        cbxTipoIncidente = new JComboBox<>(new String[]{"VIA_CERRADA", "DERRUMBE", "FALLA_SERVICIO", "SIN_SENAL", "DANO_ESTRUCTURAL"});
        gbc.gridy = 1; panelCrud.add(cbxTipoIncidente, gbc);

        // Coordenadas (Capturadas por el mapa)
        gbc.gridy = 2; panelCrud.add(new JLabel("Latitud (Clic en el mapa):"), gbc);
        txtLatitud = new JTextField(); txtLatitud.setEditable(false);
        gbc.gridy = 3; panelCrud.add(txtLatitud, gbc);

        gbc.gridy = 4; panelCrud.add(new JLabel("Longitud (Clic en el mapa):"), gbc);
        txtLongitud = new JTextField(); txtLongitud.setEditable(false);
        gbc.gridy = 5; panelCrud.add(txtLongitud, gbc);

        // Descripción
        gbc.gridy = 6; panelCrud.add(new JLabel("Descripción:"), gbc);
        txtDescripcion = new JTextArea(4, 20);
        txtDescripcion.setLineWrap(true);
        gbc.gridy = 7; panelCrud.add(new JScrollPane(txtDescripcion), gbc);

        // Botones
        JPanel panelBotones = new JPanel(new GridLayout(1, 2, 5, 5));
        btnGuardar = new JButton("Guardar");
        btnLimpiar = new JButton("Limpiar");
        panelBotones.add(btnGuardar);
        panelBotones.add(btnLimpiar);
        gbc.gridy = 8; panelCrud.add(panelBotones, gbc);

        // --- 3. ENSAMBLAJE DE LA VENTANA ---
        add(mapViewer, BorderLayout.CENTER);
        add(panelCrud, BorderLayout.EAST);
    }

    // Getters para que el Controlador pueda acceder a los datos de la vista
    public JXMapViewer getMapViewer() { return mapViewer; }
    public JComboBox<String> getCbxTipoIncidente() { return cbxTipoIncidente; }
    public JTextArea getTxtDescripcion() { return txtDescripcion; }
    public JTextField getTxtLatitud() { return txtLatitud; }
    public JTextField getTxtLongitud() { return txtLongitud; }
    public JButton getBtnGuardar() { return btnGuardar; }
    public JButton getBtnLimpiar() { return btnLimpiar; }
}