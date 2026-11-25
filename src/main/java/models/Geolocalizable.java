/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

/**
 * Contrato de todo elemento que se puede ubicar en el mapa (incidentes, estaciones,
 * puntos de ayuda). Permite dibujar marcadores sin conocer la clase concreta.
 *
 * @author equipo3/samuellopez
 */
public interface Geolocalizable {

    double getLatitud();

    double getLongitud();
}