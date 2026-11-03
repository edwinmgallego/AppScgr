/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.appscgr;

import controllers.MapaController;
import javax.swing.SwingUtilities;
import views.MapaView;

/**
 *
 * @author inged
 */
public class AppScgr {

    public static void main(String[] args) {
     SwingUtilities.invokeLater(() -> {
            MapaView vista = new MapaView();
            MapaController controlador = new MapaController(vista);
            controlador.iniciar();
        });
    }
}
