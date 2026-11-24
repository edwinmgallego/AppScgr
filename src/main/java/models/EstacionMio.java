package models;

/**
 * Representa una estación del sistema de transporte masivo MIO con su ubicación
 * geográfica y su estado de operatividad (RF-03).
 *
 * @author equipo3
 */
public class EstacionMio implements Geolocalizable {

    private int id;
    private String nombre;
    private double latitud;
    private double longitud;
    private boolean operativa;

    public EstacionMio(int id, String nombre, double latitud, double longitud, boolean operativa) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la estación no puede estar vacío.");
        }
        if (latitud < -90.0 || latitud > 90.0 || longitud < -180.0 || longitud > 180.0) {
            throw new IllegalArgumentException("Las coordenadas geográficas son inválidas.");
        }
        this.id = id;
        this.nombre = nombre.trim();
        this.latitud = latitud;
        this.longitud = longitud;
        this.operativa = operativa;
    }

    public EstacionMio(String nombre, double latitud, double longitud, boolean operativa) {
        this(0, nombre, latitud, longitud, operativa);
    }

    public EstacionMio(String nombre, double latitud, double longitud) {
        this(0, nombre, latitud, longitud, true);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la estación no puede estar vacío.");
        }
        this.nombre = nombre.trim();
    }

    @Override
    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        if (latitud < -90.0 || latitud > 90.0) {
            throw new IllegalArgumentException("Latitud fuera de rango (-90 a 90).");
        }
        this.latitud = latitud;
    }

    @Override
    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        if (longitud < -180.0 || longitud > 180.0) {
            throw new IllegalArgumentException("Longitud fuera de rango (-180 a 180).");
        }
        this.longitud = longitud;
    }

    public boolean isOperativa() {
        return operativa;
    }

    public void setOperativa(boolean operativa) {
        this.operativa = operativa;
    }

    public String getEstadoTexto() {
        return operativa ? "Operativa" : "No operativa";
    }

    @Override
    public String toString() {
        return nombre + " (" + getEstadoTexto() + ")";
    }
}