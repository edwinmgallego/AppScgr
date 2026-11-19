package models;

/*
 * PROPOSITO GENERAL: Modela los reportes de falta de senal celular en coordenadas GPS
 * especificas (RF-08) para integrarse con la tabla 'reportes_incidentes' (tipo SIN_SENAL)
 * y renderizarse como marcadores en OpenStreetMap.
 *
 * ELEMENTOS CLAVE PARA SUSTENTACION:
 * - Geolocalizacion: Incorpora latitud y longitud para posicionamiento exacto en JXMapViewer2.
 * - Polimorfismo: Responde a los mismos metodos de la interfaz que ReporteServicioZona, pero
 *   evaluando criticidad segun la perdida total de senal del operador movil.
 */
public class ReporteCoberturaMovil extends ReporteInfraestructura {
    private String operador;
    private String nivelFalla;
    private double latitud;
    private double longitud;

    public ReporteCoberturaMovil() {
        super();
    }

    public ReporteCoberturaMovil(int usuarioId, String zonaBarrio, String operador, String nivelFalla, double latitud, double longitud, String observaciones) {
        super(usuarioId, zonaBarrio, observaciones);
        this.operador = operador;
        this.nivelFalla = nivelFalla;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    @Override
    public String generarResumenEstado() {
        return "Operador: " + operador + " (" + nivelFalla + ") en " + zonaBarrio + " [" + latitud + ", " + longitud + "]";
    }

    @Override
    public boolean esCritico() {
        return "SIN_SENAL".equalsIgnoreCase(this.nivelFalla);
    }

    public String getOperador() { return operador; }
    public void setOperador(String operador) { this.operador = operador; }

    public String getNivelFalla() { return nivelFalla; }
    public void setNivelFalla(String nivelFalla) { this.nivelFalla = nivelFalla; }

    public double getLatitud() { return latitud; }
    public void setLatitud(double latitud) { this.latitud = latitud; }

    public double getLongitud() { return longitud; }
    public void setLongitud(double longitud) { this.longitud = longitud; }
}