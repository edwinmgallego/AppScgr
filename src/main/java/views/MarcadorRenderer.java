/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package views;

import models.MarcadorMapa;
import models.TipoMarcador;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.WaypointRenderer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;

/**
 * Dibuja cada marcador con el color de su categoría; el punto seleccionado lleva una mira.
 *
 * @author equipo3/
 */
public class MarcadorRenderer implements WaypointRenderer<MarcadorWaypoint> {

    private static final int RADIO = 8;
    private static final Font FUENTE_ETIQUETA = new Font(Font.SANS_SERIF, Font.BOLD, 11);

    @Override
    public void paintWaypoint(Graphics2D g, JXMapViewer mapa, MarcadorWaypoint waypoint) {
        Point2D punto = mapa.getTileFactory().geoToPixel(waypoint.getPosition(), mapa.getZoom());
        int x = (int) punto.getX();
        int y = (int) punto.getY();
        MarcadorMapa marcador = waypoint.getMarcador();

        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (marcador.getTipo() == TipoMarcador.SELECCION) {
                dibujarSeleccion(g2, x, y);
            } else {
                dibujarPunto(g2, x, y, colorDe(marcador.getTipo()));
                dibujarEtiqueta(g2, x + RADIO + 4, y + 4, marcador.getEtiqueta());
            }
        } finally {
            g2.dispose();
        }
    }

    /** Color de cada categoría; también lo usa la leyenda del mapa. */
    public static Color colorDe(TipoMarcador tipo) {
        return switch (tipo) {
            case VIA_CERRADA -> new Color(210, 40, 40);
            case DERRUMBE -> new Color(230, 126, 34);
            case SELECCION -> new Color(200, 0, 120);
            default -> new Color(110, 110, 110);
        };
    }

    private void dibujarPunto(Graphics2D g, int x, int y, Color color) {
        g.setColor(color);
        g.fillOval(x - RADIO, y - RADIO, RADIO * 2, RADIO * 2);
        g.setStroke(new BasicStroke(2f));
        g.setColor(Color.WHITE);
        g.drawOval(x - RADIO, y - RADIO, RADIO * 2, RADIO * 2);
    }

    private void dibujarSeleccion(Graphics2D g, int x, int y) {
        int r = RADIO + 3;
        g.setStroke(new BasicStroke(2.5f));
        g.setColor(colorDe(TipoMarcador.SELECCION));
        g.drawOval(x - r, y - r, r * 2, r * 2);
        g.drawLine(x - r - 4, y, x + r + 4, y);
        g.drawLine(x, y - r - 4, x, y + r + 4);
    }

    private void dibujarEtiqueta(Graphics2D g, int x, int y, String texto) {
        g.setFont(FUENTE_ETIQUETA);
        g.setColor(Color.WHITE);
        g.drawString(texto, x + 1, y + 1);
        g.setColor(new Color(30, 30, 30));
        g.drawString(texto, x, y);
    }
}
