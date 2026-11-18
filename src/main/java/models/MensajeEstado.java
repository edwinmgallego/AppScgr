/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;
import java.time.LocalDateTime;

/**
 *
 * @author USER
 */
public class MensajeEstado extends Mensaje {

    private EstadoSupervivencia estado;

    private String nombreUsuario;
    private String telefonoUsuario;
    private Double latitud;   
    private Double longitud;

    
    public MensajeEstado(int usuarioId, EstadoSupervivencia estado) {
        this(0, usuarioId, estado, LocalDateTime.now(), false);
    }

    public MensajeEstado(int id, int usuarioId, EstadoSupervivencia estado,
                         LocalDateTime fechaCreacion, boolean enviado) {
        super(id, usuarioId, fechaCreacion, enviado);
        this.estado = estado;
    }

    public MensajeEstado(int usuarioId, String nombreUsuario, String telefonoUsuario,
                         EstadoSupervivencia estado, Double latitud, Double longitud) {
        this(0, usuarioId, estado, LocalDateTime.now(), false);
        this.nombreUsuario = nombreUsuario;
        this.telefonoUsuario = telefonoUsuario;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public EstadoSupervivencia getEstado() {
        return estado; }
    public void setEstado(EstadoSupervivencia estado) {
        this.estado = estado; }

    public String getNombreUsuario() {
        return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario; }

    public String getTelefonoUsuario() {
        return telefonoUsuario; }
    public void setTelefonoUsuario(String telefonoUsuario) {
        this.telefonoUsuario = telefonoUsuario; }

    public Double getLatitud() {
        return latitud; }
    public void setLatitud(Double latitud) {
        this.latitud = latitud; }
    public Double getLongitud() {
        return longitud; }
    public void setLongitud(Double longitud) { 
        this.longitud = longitud; }

    
    public boolean tieneUbicacion() { return latitud != null && longitud != null; }

 
    @Override
    public String getResumen() {
        String texto = estado == null ? "sin estado" : estado.getEtiqueta();
        return nombreUsuario == null ? texto : texto + " (" + nombreUsuario + ")";
    }
}
