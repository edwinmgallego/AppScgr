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
public abstract class Mensaje {
    private int id;
    private int usuarioId;
    private LocalDateTime fechaCreacion;
    private boolean enviado;

    protected Mensaje(int id, int usuarioId, LocalDateTime fechaCreacion, boolean enviado) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.fechaCreacion = fechaCreacion;
        this.enviado = enviado;
    }

    public int getId() { 
        return id; }
    public void setId(int id) {
        this.id = id; }

    public int getUsuarioId() {
        return usuarioId; }
    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId; }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion; }

    public boolean isEnviado() {
        return enviado; }
    public void setEnviado(boolean enviado) {
        this.enviado = enviado; }
    public abstract String getResumen();

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{id=" + id + ", usuarioId=" + usuarioId
                + ", fechaCreacion=" + fechaCreacion + ", enviado=" + enviado
                + ", resumen=" + getResumen() + "}";
    }
}
