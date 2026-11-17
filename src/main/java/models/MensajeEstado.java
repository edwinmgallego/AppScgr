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

    
    public MensajeEstado(int usuarioId, EstadoSupervivencia estado) {
        this(0, usuarioId, estado, LocalDateTime.now(), false);
    }

    public MensajeEstado(int id, int usuarioId, EstadoSupervivencia estado,
                         LocalDateTime fechaCreacion, boolean enviado) {
        super(id, usuarioId, fechaCreacion, enviado);
        this.estado = estado;
    }
    public EstadoSupervivencia getEstado() { return estado; }
    public void setEstado(EstadoSupervivencia estado) { this.estado = estado; }

    @Override
    public String getResumen() {
        return estado == null ? "sin estado" : estado.getEtiqueta();
    }
}
