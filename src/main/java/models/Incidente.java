/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

/**
 *
 * @author inged
 */



public class Incidente {
    private int id;
    private String tipo;
    private double latitud;
    private double longitud;
    private String descripcion;

    public Incidente(int id, String tipo, double latitud, double longitud, String descripcion) {
        this.id = id;
        this.tipo = tipo;
        this.latitud = latitud;
        this.longitud = longitud;
        this.descripcion = descripcion;
    }

    // Getters y Setters para el ID (Faltaban estos)
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public double getLatitud() { return latitud; }
    public void setLatitud(double latitud) { this.latitud = latitud; }
    
    public double getLongitud() { return longitud; }
    public void setLongitud(double longitud) { this.longitud = longitud; }
    
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}