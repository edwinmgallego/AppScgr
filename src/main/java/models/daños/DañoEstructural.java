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
        setDireccion(direccion);
        setBarrio(barrio);
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
    public void setDireccion(String direccion) {
        if (direccion == null || direccion.isBlank()) {
            throw new IllegalArgumentException("La dirección es obligatoria");
        }
        this.direccion = direccion.trim();
    }
 
    public String getBarrio() { return barrio; }
    public void setBarrio(String barrio) {
        if (barrio == null || barrio.isBlank()) {
            throw new IllegalArgumentException("El barrio es obligatorio");
        }
        this.barrio = barrio.trim();
    }
 
    public double getLatitud() { return latitud; }
    public void setLatitud(double latitud) { this.latitud = latitud; }
 
    public double getLongitud() { return longitud; }
    public void setLongitud(double longitud) { this.longitud = longitud; }
 
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
 
    @Override
    public String toString() {
        return getTipoDaño() + " en " + direccion + " (" + barrio + ") - riesgo " + calcularNivelRiesgo();
    }
}

