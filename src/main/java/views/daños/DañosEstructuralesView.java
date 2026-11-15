/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package views.daños;

/**
 *
 * @author sebas
 */
import models.daños.DañoGenerador;
import models.daños.FallaTanque;
import models.daños.Grieta;
import models.daños.TipoDaño;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.ZoomMouseWheelListenerCursor;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.TileFactoryInfo;

import javax.swing.*;
import java.awt.*;


public class DañosEstructuralesView extends JFrame {

    private JXMapViewer mapViewer;

    // Datos comunes
    private JComboBox<TipoDaño> cbxTipo;
    private JTextField txtDireccion;
    private JTextField txtBarrio;
    private JTextField txtLatitud;
    private JTextField txtLongitud;
    private JTextArea txtDescripcion;

    // Datos de una grieta
    private JPanel panelGrieta;
    private JComboBox<String> cbxElemento;
    private JTextField txtAncho;
    private JTextField txtLargo;

    // Datos de un tanque
    private JPanel panelTanque;
    private JComboBox<String> cbxFallaTanque;
    private JTextField txtCapacidad;
    private JCheckBox chkElevado;

    // Datos de un generador
    private JPanel panelGenerador;
    private JComboBox<String> cbxFallaGenerador;
    private JTextField txtPotencia;
    private JCheckBox chkServicioCritico;

    // Botones
    private JButton btnGuardar;
    private JButton btnActualizar;
    private JButton btnEliminar;
    private JButton btnLimpiar;

    public DañosEstructuralesView() {
        setTitle("AppScgr - Daños estructurales (Equipo 6)");
        setSize(1200, 750);
        setLocationRelativeTo(null);
        // para que al cerrar no se cierre todo el programa
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // 1. Mapa (lo mismo que en MapaView)
        System.setProperty("http.agent", "AppScgr-POO-Cali/1.0");
        mapViewer = new JXMapViewer();
        TileFactoryInfo info = new OSMTileFactoryInfo() {
            @Override
            public String getTileUrl(int x, int y, int zoom) {
                return super.getTileUrl(x, y, zoom).replace("http://", "https://");
            }
        };
        mapViewer.setTileFactory(new DefaultTileFactory(info));
        mapViewer.setZoom(6);
        mapViewer.setAddressLocation(new GeoPosition(3.4516, -76.5320)); // Cali
        // para mover el mapa y hacer zoom con la rueda
        PanMouseInputListener arrastre = new PanMouseInputListener(mapViewer);
        mapViewer.addMouseListener(arrastre);
        mapViewer.addMouseMotionListener(arrastre);
        mapViewer.addMouseWheelListener(new ZoomMouseWheelListenerCursor(mapViewer));

        // 2. Formulario: datos comunes
        cbxTipo = new JComboBox<>(TipoDaño.values());
        txtDireccion = new JTextField();
        txtBarrio = new JTextField();
        txtLatitud = new JTextField();
        txtLatitud.setEditable(false);
        txtLongitud = new JTextField();
        txtLongitud.setEditable(false);

        JPanel panelDatos = new JPanel(new GridLayout(0, 2, 5, 5));
        panelDatos.setBorder(BorderFactory.createTitledBorder("Datos del reporte"));
        panelDatos.add(new JLabel("Tipo de daño:"));
        panelDatos.add(cbxTipo);
        panelDatos.add(new JLabel("Dirección:"));
        panelDatos.add(txtDireccion);
        panelDatos.add(new JLabel("Barrio:"));
        panelDatos.add(txtBarrio);
        panelDatos.add(new JLabel("Latitud (clic en mapa):"));
        panelDatos.add(txtLatitud);
        panelDatos.add(new JLabel("Longitud (clic en mapa):"));
        panelDatos.add(txtLongitud);

        // 3. Un panel por cada tipo, solo se ve uno
        cbxElemento = new JComboBox<>(Grieta.ELEMENTOS);
        txtAncho = new JTextField();
        txtLargo = new JTextField();
        panelGrieta = new JPanel(new GridLayout(0, 2, 5, 5));
        panelGrieta.setBorder(BorderFactory.createTitledBorder("Datos de la grieta"));
        panelGrieta.add(new JLabel("Elemento:"));
        panelGrieta.add(cbxElemento);
        panelGrieta.add(new JLabel("Ancho (mm):"));
        panelGrieta.add(txtAncho);
        panelGrieta.add(new JLabel("Largo (cm):"));
        panelGrieta.add(txtLargo);

        cbxFallaTanque = new JComboBox<>(FallaTanque.FALLAS);
        txtCapacidad = new JTextField();
        chkElevado = new JCheckBox("Tanque elevado");
        panelTanque = new JPanel(new GridLayout(0, 2, 5, 5));
        panelTanque.setBorder(BorderFactory.createTitledBorder("Datos del tanque"));
        panelTanque.add(new JLabel("Falla:"));
        panelTanque.add(cbxFallaTanque);
        panelTanque.add(new JLabel("Capacidad (litros):"));
        panelTanque.add(txtCapacidad);
        panelTanque.add(new JLabel(""));
        panelTanque.add(chkElevado);

        cbxFallaGenerador = new JComboBox<>(DañoGenerador.FALLAS);
        txtPotencia = new JTextField();
        chkServicioCritico = new JCheckBox("Servicio crítico");
        panelGenerador = new JPanel(new GridLayout(0, 2, 5, 5));
        panelGenerador.setBorder(BorderFactory.createTitledBorder("Datos del generador"));
        panelGenerador.add(new JLabel("Falla:"));
        panelGenerador.add(cbxFallaGenerador);
        panelGenerador.add(new JLabel("Potencia (kVA):"));
        panelGenerador.add(txtPotencia);
        panelGenerador.add(new JLabel(""));
        panelGenerador.add(chkServicioCritico);

        // 4. Descripción y botones
        txtDescripcion = new JTextArea(3, 20);
        txtDescripcion.setLineWrap(true);
        JScrollPane scrollDescripcion = new JScrollPane(txtDescripcion);
        scrollDescripcion.setBorder(BorderFactory.createTitledBorder("Descripción"));

        btnGuardar = new JButton("Guardar");
        btnActualizar = new JButton("Actualizar");
        btnEliminar = new JButton("Eliminar");
        btnLimpiar = new JButton("Limpiar");
        JPanel panelBotones = new JPanel(new GridLayout(2, 2, 5, 5));
        panelBotones.add(btnGuardar);
        panelBotones.add(btnActualizar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnLimpiar);

        // todo el formulario en columna
        JPanel formulario = new JPanel();
        formulario.setLayout(new BoxLayout(formulario, BoxLayout.Y_AXIS));
        formulario.add(panelDatos);
        formulario.add(panelGrieta);
        formulario.add(panelTanque);
        formulario.add(panelGenerador);
        formulario.add(scrollDescripcion);
        formulario.add(panelBotones);

        JPanel panelIzquierdo = new JPanel(new BorderLayout());
        panelIzquierdo.add(formulario, BorderLayout.NORTH);
        panelIzquierdo.setPreferredSize(new Dimension(380, 0));

        // 5. Armar la ventana
        JPanel panelDerecho = new JPanel(new BorderLayout());
        panelDerecho.add(mapViewer, BorderLayout.CENTER);

        add(new JScrollPane(panelIzquierdo), BorderLayout.WEST);
        add(panelDerecho, BorderLayout.CENTER);

        mostrarCampos(TipoDaño.GRIETA);
    }

    // muestra solo el panel del tipo elegido
    public void mostrarCampos(TipoDaño tipo) {
        panelGrieta.setVisible(tipo == TipoDaño.GRIETA);
        panelTanque.setVisible(tipo == TipoDaño.TANQUE_AGUA);
        panelGenerador.setVisible(tipo == TipoDaño.GENERADOR);
        revalidate();
    }

    public void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje);
    }

    public boolean confirmar(String pregunta) {
        return JOptionPane.showConfirmDialog(this, pregunta, "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    // Getters para el controlador
    public JXMapViewer getMapViewer() {
        return mapViewer;
    }

    public JComboBox<TipoDaño> getCbxTipo() {
        return cbxTipo;
    }

    public JTextField getTxtDireccion() {
        return txtDireccion;
    }

    public JTextField getTxtBarrio() {
        return txtBarrio;
    }

    public JTextField getTxtLatitud() {
        return txtLatitud;
    }

    public JTextField getTxtLongitud() {
        return txtLongitud;
    }

    public JTextArea getTxtDescripcion() {
        return txtDescripcion;
    }

    public JComboBox<String> getCbxElemento() {
        return cbxElemento;
    }

    public JTextField getTxtAncho() {
        return txtAncho;
    }

    public JTextField getTxtLargo() {
        return txtLargo;
    }

    public JComboBox<String> getCbxFallaTanque() {
        return cbxFallaTanque;
    }

    public JTextField getTxtCapacidad() {
        return txtCapacidad;
    }

    public JCheckBox getChkElevado() {
        return chkElevado;
    }

    public JComboBox<String> getCbxFallaGenerador() {
        return cbxFallaGenerador;
    }

    public JTextField getTxtPotencia() {
        return txtPotencia;
    }

    public JCheckBox getChkServicioCritico() {
        return chkServicioCritico;
    }

    public JButton getBtnGuardar() {
        return btnGuardar;
    }

    public JButton getBtnActualizar() {
        return btnActualizar;
    }

    public JButton getBtnEliminar() {
        return btnEliminar;
    }

    public JButton getBtnLimpiar() {
        return btnLimpiar;
    }
}
