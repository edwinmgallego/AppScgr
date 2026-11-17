/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 *
 * @author USER
 */
public abstract class ColaBase<T extends Mensaje> {

    private final List<T> pendientes = new ArrayList<>();

    
    public synchronized void encolar(T mensaje) {
        if (mensaje == null) {
            throw new IllegalArgumentException("El mensaje no puede ser nulo.");
        }
        pendientes.add(mensaje);
    }

    protected abstract void alEnviar(T mensaje);

    public synchronized int sincronizar(Predicate<T> envio) {
        return sincronizar(envio, Integer.MAX_VALUE);
    }

    public synchronized int sincronizar(Predicate<T> envio, int maximo) {
        int enviados = 0;
        while (enviados < maximo && !pendientes.isEmpty()) {
            T siguiente = pendientes.get(0);   
            if (!envio.test(siguiente)) {
                break; 
            }
            alEnviar(siguiente);
            pendientes.remove(0);              
            enviados++;
        }
        return enviados;
    }

    public synchronized int cantidadPendientes() {
        return pendientes.size();
    }

    public synchronized boolean estaVacia() {
        return pendientes.isEmpty();
    }

    public synchronized List<T> verPendientes() {
        return new ArrayList<>(pendientes);
    }
}
