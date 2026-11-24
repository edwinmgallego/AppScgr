/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controllers;

/**
 *
 * @author Jurbano
 */

import javax.swing.JFrame;

public abstract class BaseController<V extends JFrame> {
    
    protected final V vista;

    protected BaseController(V vista) {
        this.vista = vista;
    }

    protected abstract void inicializarEventos();

    // por si se cierra la ventana
    public void iniciar() {
        vista.setVisible(true);
        vista.toFront();
    }
}