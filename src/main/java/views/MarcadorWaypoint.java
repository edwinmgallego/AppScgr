/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package views;

import models.MarcadorMapa;
import org.jxmapviewer.viewer.DefaultWaypoint;

/**
 * adapta un MarcadorMapa del modelo al tipo de punto que dibuja JXMapViewer2.
 * le permite a JXMapViewer2 saber que marcador se usa, para aplicar color luego
 *
 * @author equipo3/samuellopez
 */
public class MarcadorWaypoint extends DefaultWaypoint {

    private final MarcadorMapa marcador;

    public MarcadorWaypoint(MarcadorMapa marcador) {
        super(marcador.getLatitud(), marcador.getLongitud());
        this.marcador = marcador;
    }

    public MarcadorMapa getMarcador() { return marcador; }
}