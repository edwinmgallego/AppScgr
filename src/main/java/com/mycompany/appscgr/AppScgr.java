/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.appscgr;

import controllers.LoginController;
import javax.swing.SwingUtilities;
import views.LoginView;
import utils.ActualizadorBD; 
import utils.VerificadorBD;


/**
 *
 * @author inged
 */
public class AppScgr {

    public static void main(String[] args) {
        // Si MySQL está encendido, se revisa que la base tenga la columna de la red familiar
        if (VerificadorBD.mysqlEncendido()) {
            ActualizadorBD.actualizar();
        }
        // La aplicación arranca en el login; si es correcto, LoginController abre el mapa
        SwingUtilities.invokeLater(() -> new LoginController(new LoginView()).iniciar());
    }
}