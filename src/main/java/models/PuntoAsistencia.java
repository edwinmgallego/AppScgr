package models;

import java.util.List;
import java.util.Locale;

public abstract class PuntoAsistencia implements Geolocalizable {

    /** Tamaño de la columna nombre en la base de datos. */
    public static final int MAX_NOMBRE = 100;

    private int id;
    private String nombre;
    private double latitud;
    private double longitud;
    private EstadoPunto estado;
    private ServiciosPunto servicios;

    protected PuntoAsistencia(int id, String nombre, double latitud, double longitud,
                              EstadoPunto estado, ServiciosPunto servicios) {
        this.id = id;
        setNombre(nombre);
        setLatitud(latitud);
        setLongitud(longitud);
        setEstado(estado);
        setServicios(servicios);
    }

    public static PuntoAsistencia crear(String tipoPunto, int id, String nombre, double latitud,
                                        double longitud, EstadoPunto estado, ServiciosPunto servicios) {
        if (tipoPunto == null || tipoPunto.isBlank()) {
            throw new IllegalArgumentException("El tipo de punto es obligatorio.");
        }
        ServiciosPunto lista = servicios == null ? new ServiciosPunto() : servicios;
        return switch (tipoPunto.trim().toUpperCase(Locale.ROOT)) {
            case "ALBERGUE" -> lista.contiene(ServiciosPunto.ACOPIO)
                    ? new CentroAcopio(id, nombre, latitud, longitud, estado, lista)
                    : new Albergue(id, nombre, latitud, longitud, estado, lista);
            case "SUPERMERCADO" -> new Supermercado(id, nombre, latitud, longitud, estado, lista);
            case "MECANICO" -> new Mecanico(id, nombre, latitud, longitud, estado, lista);
            case "HOSPITAL" -> new Hospital(id, nombre, latitud, longitud, estado, lista);
            default -> throw new IllegalArgumentException("Tipo de punto no reconocido: " + tipoPunto);
        };
    }

    /** Valor que se guarda en la columna tipo_punto (ALBERGUE, SUPERMERCADO, MECANICO, HOSPITAL). */
    public abstract String getTipoPunto();

    /** Nombre del tipo para mostrar en pantalla. */
    public abstract String getEtiquetaTipo();

    /** Servicios que normalmente ofrece este tipo de punto (para armar el formulario). */
    public abstract List<String> getServiciosSugeridos();

    /**
     * Gancho para que una subclase ajuste los servicios antes de guardarlos
     * (CentroAcopio lo usa para asegurar la marca ACOPIO). Por defecto no hace nada.
     */
    protected void completarServicios(ServiciosPunto servicios) { }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }

    public final void setNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del punto es obligatorio.");
        }
        String limpio = nombre.trim();
        if (limpio.length() > MAX_NOMBRE) {
            throw new IllegalArgumentException("El nombre no puede superar " + MAX_NOMBRE + " caracteres.");
        }
        this.nombre = limpio;
    }

    @Override
    public double getLatitud() { return latitud; }

    public final void setLatitud(double latitud) {
        if (Double.isNaN(latitud) || latitud < -90 || latitud > 90) {
            throw new IllegalArgumentException("La latitud debe estar entre -90 y 90.");
        }
        this.latitud = latitud;
    }

    @Override
    public double getLongitud() { return longitud; }

    public final void setLongitud(double longitud) {
        if (Double.isNaN(longitud) || longitud < -180 || longitud > 180) {
            throw new IllegalArgumentException("La longitud debe estar entre -180 y 180.");
        }
        this.longitud = longitud;
    }

    public EstadoPunto getEstado() { return estado; }

    public final void setEstado(EstadoPunto estado) {
        if (estado == null) {
            throw new IllegalArgumentException("El estado del punto es obligatorio.");
        }
        this.estado = estado;
    }

    public boolean isAbierto() { return estado == EstadoPunto.ABIERTO; }

    public ServiciosPunto getServicios() { return servicios; }

    public final void setServicios(ServiciosPunto servicios) {
        if (servicios == null) {
            throw new IllegalArgumentException("Los servicios son obligatorios (pueden estar vacíos).");
        }
        completarServicios(servicios);
        this.servicios = servicios;
    }

    public int getVoluntariosNecesarios() { return servicios.getVoluntariosNecesarios(); }

    @Override
    public String toString() {
        return getEtiquetaTipo() + ": " + nombre + " (" + estado + ")";
    }
}