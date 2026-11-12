/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package views;

import models.TipoMarcador;

import java.awt.FlowLayout;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Leyenda que explica el color de cada tipo de marcador del mapa.
 *
 * @author equipo3/samuellopez
 */
public class LeyendaMapaPanel extends JPanel {

    public LeyendaMapaPanel() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 8, 4));
        setBorder(BorderFactory.createTitledBorder("Leyenda"));

        for (TipoMarcador tipo : TipoMarcador.values()) {
            JLabel punto = new JLabel("●"); // círculo relleno
            punto.setForeground(MarcadorRenderer.colorDe(tipo));
            add(punto);
            add(new JLabel(tipo.getEtiqueta()));
        }
    }
}