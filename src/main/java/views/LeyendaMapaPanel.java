/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package views;

import models.TipoMarcador;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Leyenda que explica el color de cada tipo de marcador del mapa.
 *
 * @author equipo3/samuellopez
 */
public class LeyendaMapaPanel extends JPanel {

    /** Entradas por fila: con todos los tipos en una sola fila, las últimas no caben en la ventana. */
    private static final int COLUMNAS = 5;

    public LeyendaMapaPanel() {
        setLayout(new GridLayout(0, COLUMNAS, 8, 2));
        setBorder(BorderFactory.createTitledBorder("Leyenda"));

        for (TipoMarcador tipo : TipoMarcador.values()) {
            JPanel entrada = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
            JLabel punto = new JLabel("●"); // círculo relleno
            punto.setForeground(MarcadorRenderer.colorDe(tipo));
            entrada.add(punto);
            entrada.add(new JLabel(tipo.getEtiqueta()));
            add(entrada);
        }
    }
}