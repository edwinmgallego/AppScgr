package models;

import java.sql.Timestamp;

/**
 * .
 *
 * @author User
 */
public class ReporteExtraviado {

    private int id;                       // id (AUTO_INCREMENT)
    private int usuarioId;                // usuario_id
    private TipoReporte tipo;             // tipo  ENUM('PERSONA','MASCOTA')
    private String nombre;                // nombre varchar(100)
    private String caracteristicas;       // caracteristicas text
    private EstadoReporte estado;         // estado ENUM('PERDIDO','ENCONTRADO')
    private Double latitudVisto;          // latitud_visto  (puede ser NULL)
    private Double longitudVisto;         // longitud_visto (puede ser NULL)
    private Timestamp fechaReporte;       // fecha_reporte (la pone MySQL)

    // Constructor vacío: útil para ir llenando el objeto con setters
    public ReporteExtraviado() {
    }

    // Constructor para un reporte NUEVO (desde el formulario).
    // No lleva id ni fecha porque los genera MySQL.
    public ReporteExtraviado(TipoReporte tipo, String nombre, String caracteristicas,
                             EstadoReporte estado, Double latitudVisto, Double longitudVisto) {
        this.tipo = tipo;
        this.nombre = nombre;
        this.caracteristicas = caracteristicas;
        this.estado = estado;
        this.latitudVisto = latitudVisto;
        this.longitudVisto = longitudVisto;
    }

    // Constructor COMPLETO: lo usa el DAO al leer una fila de la BD
    public ReporteExtraviado(int id, int usuarioId, TipoReporte tipo, String nombre,
                             String caracteristicas, EstadoReporte estado,
                             Double latitudVisto, Double longitudVisto, Timestamp fechaReporte) {
        this(tipo, nombre, caracteristicas, estado, latitudVisto, longitudVisto);
        this.id = id;
        this.usuarioId = usuarioId;
        this.fechaReporte = fechaReporte;
    }

    // ----- Getters y Setters -----
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }

    public TipoReporte getTipo() { return tipo; }
    public void setTipo(TipoReporte tipo) { this.tipo = tipo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCaracteristicas() { return caracteristicas; }
    public void setCaracteristicas(String caracteristicas) { this.caracteristicas = caracteristicas; }

    public EstadoReporte getEstado() { return estado; }
    public void setEstado(EstadoReporte estado) { this.estado = estado; }

    public Double getLatitudVisto() { return latitudVisto; }
    public void setLatitudVisto(Double latitudVisto) { this.latitudVisto = latitudVisto; }

    public Double getLongitudVisto() { return longitudVisto; }
    public void setLongitudVisto(Double longitudVisto) { this.longitudVisto = longitudVisto; }

    public Timestamp getFechaReporte() { return fechaReporte; }
    public void setFechaReporte(Timestamp fechaReporte) { this.fechaReporte = fechaReporte; }

    @Override
    public String toString() {
        return "#" + id + " " + tipo + " " + estado + ": " + nombre;
    }
}
