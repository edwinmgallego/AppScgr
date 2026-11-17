/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;
import java.util.List;
import java.util.Locale;
/**
 *
 * @author Titoelgato
 */
public abstract class PuntoAsistencia implements Geolocalizable {

    /** Tamaño de la columna nombre en la base de datos. */
    public static final int MAX_NOMBRE = 100;

    private int id;
    private String nombre;
    private double latitud;
    private double longitud;
    private EstadoPunto estado;
    private ServiciosPunto servicios;

    public PuntoAsistencia(int id, String nombre, double latitud, double longitud, EstadoPunto estado, ServiciosPunto servicios) {
        this.id = id;
        this.nombre = nombre;
        this.latitud = latitud;
        this.longitud = longitud;
        this.estado = estado;
        this.servicios = servicios;
    }
}