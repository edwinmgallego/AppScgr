/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;
import java.util.List;
/**
 *
 * @author Titoelgato
 */
public class Supermercado extends PuntoAsistencia {

    public Supermercado(int id, String nombre, double latitud, double longitud,
             EstadoPunto estado, ServiciosPunto servicios) {
        super(id, nombre, latitud, longitud, estado, servicios);
    }

    /** Punto nuevo: sin id, abierto y sin servicios. */
    public Supermercado(String nombre, double latitud, double longitud) {
        this(0, nombre, latitud, longitud, EstadoPunto.ABIERTO, new ServiciosPunto());
    }

    @Override
    public String getTipoPunto() { 
        return "Supermercado"; }

    @Override
    public String getEtiquetaTipo() { 
        return "Supermercado"; }

    @Override
    public List<String> getServiciosSugeridos() {
        return List.of("AGUA", "ALIMENTACION");
    }
}
