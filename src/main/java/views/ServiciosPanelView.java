package views;

import java.awt.BorderLayout;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

/*
 * PROPOSITO GENERAL: Vista principal del Equipo 5 (RF-06 y RF-08) construida como JPanel
 * para integrarse dentro del Dashboard lateral del Equipo 9 sin abrir ventanas externas.
 *
 * ELEMENTOS CLAVE PARA SUSTENTACION:
 * - Separacion estricta MVC: Esta vista no importa ConexionDB ni clases DAO.
 * - JTabbedPane: Organiza en dos pestanas independientes el monitoreo por barrios (RF-06)
 *   y el reporte de cobertura movil por coordenadas GPS (RF-08).
 */
public class ServiciosPanelView extends JPanel {

    private JTabbedPane pestanasModulo;
    private JPanel panelServiciosBarrio;
    private JPanel panelCoberturaMovil;

    public ServiciosPanelView() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lblTitulo = new JLabel("Monitoreo de Servicios Públicos y Señal Móvil (Equipo 5)");
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 16));
        add(lblTitulo, BorderLayout.NORTH);

        pestanasModulo = new JTabbedPane();
        panelServiciosBarrio = new JPanel(new BorderLayout(10, 10));
        panelCoberturaMovil = new JPanel(new BorderLayout(10, 10));

        pestanasModulo.addTab("Servicios por Barrio (RF-06)", panelServiciosBarrio);
        pestanasModulo.addTab("Cobertura Móvil GPS (RF-08)", panelCoberturaMovil);

        add(pestanasModulo, BorderLayout.CENTER);
    }

    public JTabbedPane getPestanasModulo() { return pestanasModulo; }
}
