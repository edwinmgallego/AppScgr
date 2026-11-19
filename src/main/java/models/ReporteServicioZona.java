package models;

/*
 * PROPOSITO GENERAL: Representa el estado de un servicio publico (agua, energia o
 * telecomunicaciones) en un barrio especifico (RF-06). Mapea con la tabla 'estado_servicios_zona'.
 *
 * ELEMENTOS CLAVE PARA SUSTENTACION:
 * - extends + super(): Reutiliza los atributos y logica de inicializacion de ReporteInfraestructura.
 * - @Override: Aplica polimorfismo al adaptar generarResumenEstado() y esCritico() a servicios domiciliarios.
 */
public class ReporteServicioZona extends ReporteInfraestructura {
    private String servicio;
    private String estado;

    public ReporteServicioZona() {
        super();
    }

    public ReporteServicioZona(int usuarioId, String zonaBarrio, String servicio, String estado, String observaciones) {
        super(usuarioId, zonaBarrio, observaciones);
        this.servicio = servicio;
        this.estado = estado;
    }

    @Override
    public String generarResumenEstado() {
        return "Barrio: " + zonaBarrio + " | Servicio: " + servicio + " | Estado: " + estado;
    }

    @Override
    public boolean esCritico() {
        return "CAIDO".equalsIgnoreCase(this.estado);
    }

    public String getServicio() { return servicio; }
    public void setServicio(String servicio) { this.servicio = servicio; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}