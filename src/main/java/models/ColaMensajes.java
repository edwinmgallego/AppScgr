/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

/**
 *
 * @author USER
 */
public class ColaMensajes extends ColaBase<MensajeEstado> {

    @Override
    protected void alEnviar(MensajeEstado mensaje) {
        mensaje.setEnviado(true);
    }
    
    @Override
    public String toString() {
        return "ColaMensajes{pendientes=" + cantidadPendientes() + "}";
    }
}
