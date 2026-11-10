/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.appscgr;

import controllers.LoginController;
import javax.swing.SwingUtilities;
import views.LoginView;

/**
 *
 * @author inged
 */
public class AppScgr {

    public static void main(String[] args) {
        // La aplicación arranca en el login; si es correcto, LoginController abre el mapa
        SwingUtilities.invokeLater(() -> new LoginController(new LoginView()).iniciar());
    }
}
