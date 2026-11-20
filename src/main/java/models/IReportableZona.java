package models;

/*
 * PROPOSITO GENERAL: Define el contrato obligatorio para cualquier reporte geografico
 * del Equipo 5, permitiendo tratar distintos tipos de fallas de manera uniforme (Polimorfismo).
 */

public interface IReportableZona {
    String getZonaBarrio();
    String generarResumenEstado();
    boolean esCritico();
}