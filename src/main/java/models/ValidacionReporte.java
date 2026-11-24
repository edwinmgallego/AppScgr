/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

import java.sql.Timestamp;

/**
 * Representa un voto ciudadano sobre un reporte de incidente.
 * Se guarda en la tabla validaciones_reporte.
 *
 * @author Grupo 9 Gabriela Moreno - Validación Comunitaria y Dashboard
 */
public class ValidacionReporte {

    private int id;
    private int reporteId;
    private int usuarioId;
    private TipoVoto voto;
    private Timestamp fecha;

    public ValidacionReporte() {
    }

    public ValidacionReporte(int id, int reporteId, int usuarioId, TipoVoto voto, Timestamp fecha) {
        this.id = id;
        this.reporteId = reporteId;
        this.usuarioId = usuarioId;
        this.voto = voto;
        this.fecha = fecha;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getReporteId() {
        return reporteId;
    }

    public void setReporteId(int reporteId) {
        this.reporteId = reporteId;
    }

    public int getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
    }

    public TipoVoto getVoto() {
        return voto;
    }

    public void setVoto(TipoVoto voto) {
        this.voto = voto;
    }

    public Timestamp getFecha() {
        return fecha;
    }

    public void setFecha(Timestamp fecha) {
        this.fecha = fecha;
    }
}
