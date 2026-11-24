/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

/**
 *
 * @author inged
 */



public class Incidente implements Geolocalizable {
    private int id;
    private String tipo;
    private double latitud;
    private double longitud;
    private String descripcion;
    private EstadoVia estadoVia;

    public Incidente(int id, String tipo, double latitud, double longitud, String descripcion) {
        this(id, tipo, latitud, longitud, descripcion, EstadoVia.CERRADA);
    }

    public Incidente(int id, String tipo, double latitud, double longitud, String descripcion, EstadoVia estadoVia) {
        this.id = id;
        this.tipo = tipo;
        this.latitud = latitud;
        this.longitud = longitud;
        this.descripcion = descripcion;
        this.estadoVia = (estadoVia != null) ? estadoVia : EstadoVia.CERRADA;
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

    public EstadoVia getEstadoVia() { return estadoVia; }

    public void setEstadoVia(EstadoVia estadoVia) {
        this.estadoVia = (estadoVia != null) ? estadoVia : EstadoVia.CERRADA;
    }

    public boolean estaAbierta() { return this.estadoVia == EstadoVia.ABIERTA; }

    public void marcarComoReabierta() { this.estadoVia = EstadoVia.ABIERTA; }

    public void marcarComoCerrada() { this.estadoVia = EstadoVia.CERRADA; }
}