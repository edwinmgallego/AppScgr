/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package views;
import javax.swing.*;
import java.awt.*;

/**
 *
 * @author USUARIO
 */
public class EstadoView extends JFrame {
    
      private JButton btnEstoyBien;
    private JButton btnNecesitoAyuda;
    private JButton btnSincronizar;
    private JCheckBox chkSinConexion;
    private JLabel lblConexion;
    private JLabel lblPendientes;

    public EstadoView() {
        setTitle("Alertas y Estado de Supervivencia");
        setSize(560, 520);
        setLocationRelativeTo(null);
     
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        
         JPanel panelEstados = new JPanel(new GridLayout(1, 2, 10, 0));
        panelEstados.setBorder(BorderFactory.createTitledBorder("¿Cómo estás?"));

        btnEstoyBien = new JButton("ESTOY BIEN");
        btnEstoyBien.setBackground(new Color(46, 160, 67));
        btnEstoyBien.setForeground(Color.WHITE);

        btnNecesitoAyuda = new JButton("NECESITO AYUDA");
        btnNecesitoAyuda.setBackground(new Color(200, 40, 40));
        btnNecesitoAyuda.setForeground(Color.WHITE);

        for (JButton boton : new JButton[]{btnEstoyBien, btnNecesitoAyuda}) {
            boton.setFont(boton.getFont().deriveFont(Font.BOLD, 16f));
            boton.setPreferredSize(new Dimension(200, 90));
            boton.setOpaque(true);
            boton.setBorderPainted(false);
            boton.setFocusPainted(false);
            panelEstados.add(boton);
        }

        JPanel panelConexion = new JPanel(new GridLayout(3, 1, 0, 4));
        panelConexion.setBorder(BorderFactory.createTitledBorder("Conexión"));

        chkSinConexion = new JCheckBox("Simular que no hay conexión");
        lblConexion = new JLabel();
        lblPendientes = new JLabel();

        JPanel filaPendientes = new JPanel(new BorderLayout(10, 0));
        btnSincronizar = new JButton("Sincronizar ahora");
        filaPendientes.add(lblPendientes, BorderLayout.CENTER);
        filaPendientes.add(btnSincronizar, BorderLayout.EAST);

        panelConexion.add(chkSinConexion);
        panelConexion.add(lblConexion);
        panelConexion.add(filaPendientes);

        JPanel panelNorte = new JPanel(new BorderLayout(0, 10));
        panelNorte.add(panelEstados, BorderLayout.CENTER);
        panelNorte.add(panelConexion, BorderLayout.SOUTH);

        add(panelNorte, BorderLayout.NORTH);
    }
    
    public JButton getBtnEstoyBien() { return btnEstoyBien; }
    public JButton getBtnNecesitoAyuda() { return btnNecesitoAyuda; }
    public JButton getBtnSincronizar() { return btnSincronizar; }
    public JCheckBox getChkSinConexion() { return chkSinConexion; }
}
        
