/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models.daños;

/**
 *
 * @author juans
 */
public abstract class DañoEstructural implements EvaluableRiesgo {
 
    private int id;
    private int usuarioId;
    private String direccion;
    private String barrio;
    private double latitud;
    private double longitud;
    private String descripcion;
 
    public DañoEstructural(int usuarioId, String direccion, String barrio, double latitud, double longitud, String descripcion) {
        this.usuarioId = usuarioId;
        this.direccion = direccion;
        this.barrio = barrio;
        this.latitud = latitud;
        this.longitud = longitud;
        this.descripcion = descripcion;
    }
 
    // cada hija implementa estos dos
    public abstract TipoDaño getTipoDaño();
 
    public abstract String getDetalle();
 
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
 
    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }
 
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
 
    public String getBarrio() { return barrio; }
    public void setBarrio(String barrio) { this.barrio = barrio; }
 
    public double getLatitud() { return latitud; }
    public void setLatitud(double latitud) { this.latitud = latitud; }
 
    public double getLongitud() { return longitud; }
    public void setLongitud(double longitud) { this.longitud = longitud; }
 
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}

