/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controllers;

/**
 * Oyente del punto que el usuario selecciona con un clic en el mapa. Es el punto de
 * enganche para módulos que necesitan esas coordenadas.
 *
 * @author Equipo 3 - Core del mapa y cierres viales
 */
public interface SeleccionListener {

    void alSeleccionar(double latitud, double longitud);

    default void alLimpiarSeleccion() { }
}
