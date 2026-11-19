package models;

import java.sql.Timestamp;
/*
 * PROPOSITO GENERAL: Superclase abstracta que centraliza los atributos comunes entre
 * los reportes de servicios por barrio (RF-06) y cobertura movil (RF-08), evitando
 * duplicacion de codigo en el modelo.
 *
 * ELEMENTOS CLAVE PARA SUSTENTACION:
 * - abstract class: Impide instanciar un reporte generico incompleto; obliga a usar clases hijas.
 * - protected: Permite acceso directo a las clases hijas manteniendo ocultamiento ante otras capas.
 * - Sobrecarga de constructores: Facilita instanciar objetos vacios o con datos iniciales desde el controlador.
 */

public abstract class ReporteInfraestructura implements IReportableZona {
    protected int id;
    protected int usuarioId;
    protected String zonaBarrio;
    protected String observaciones;
    protected int verificaciones;
    protected Timestamp fechaReporte;

    // Constructor vacío
    public ReporteInfraestructura() {
        this.usuarioId = 1;
        this.verificaciones = 0;
    }

    // Constructor 
    public ReporteInfraestructura(int usuarioId, String zonaBarrio, String observaciones) {
        this.usuarioId = usuarioId;
        this.zonaBarrio = zonaBarrio;
        this.observaciones = observaciones;
        this.verificaciones = 0;
    }

    // Método abstracto que obligará polimorfismo en las clases hijas
    @Override
    public abstract String generarResumenEstado();

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }

    @Override
    public String getZonaBarrio() { return zonaBarrio; }
    public void setZonaBarrio(String zonaBarrio) { this.zonaBarrio = zonaBarrio; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public int getVerificaciones() { return verificaciones; }
    public void setVerificaciones(int verificaciones) { this.verificaciones = verificaciones; }

    public Timestamp getFechaReporte() { return fechaReporte; }
    public void setFechaReporte(Timestamp fechaReporte) { this.fechaReporte = fechaReporte; }
}