package models;

import java.util.List;

public class CentroAcopio extends PuntoAsistencia {

    public CentroAcopio(int id, String nombre, double latitud, double longitud,
             EstadoPunto estado, ServiciosPunto servicios) {
        super(id, nombre, latitud, longitud, estado, servicios);
    }

    /** Punto nuevo: sin id, abierto y sin servicios. */
    public CentroAcopio(String nombre, double latitud, double longitud) {
        this(0, nombre, latitud, longitud, EstadoPunto.ABIERTO, new ServiciosPunto());
    }

    @Override
    public String getTipoPunto() { return "ALBERGUE"; }

    @Override
    public String getEtiquetaTipo() { return "Centro de acopio"; }

    @Override
    public List<String> getServiciosSugeridos() {
        return List.of("AGUA", "ALIMENTACION", "INTERNET");
    }

    @Override
    protected void completarServicios(ServiciosPunto servicios) {
        servicios.agregar(ServiciosPunto.ACOPIO);
    }
}