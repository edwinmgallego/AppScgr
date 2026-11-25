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
public class Mecanico extends PuntoAsistencia {

    public Mecanico(int id, String nombre, double latitud, double longitud,
             EstadoPunto estado, ServiciosPunto servicios) {
        super(id, nombre, latitud, longitud, estado, servicios);
    }

    /** Punto nuevo: sin id, abierto y sin servicios. */
    public Mecanico(String nombre, double latitud, double longitud) {
        this(0, nombre, latitud, longitud, EstadoPunto.ABIERTO, new ServiciosPunto());
    }

    @Override
    public String getTipoPunto() { 
        return "MECANICO"; }

    @Override
    public String getEtiquetaTipo() { 
        return "Mecanico"; }

    @Override
    public List<String> getServiciosSugeridos() {
        return List.of("ENERGIA", "INTERNET");
    }
}
