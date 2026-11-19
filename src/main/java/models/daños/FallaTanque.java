/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models.daños;

/**
 *
 * @author juans
 */
public class FallaTanque extends DañoEstructural {
 
    public static final String[] FALLAS = {"Fisura", "Fuga", "Colapso", "Contaminación"};
 
    private String tipoFalla;
    private int capacidadLitros;
    private boolean elevado;
 
    public FallaTanque(int usuarioId, String direccion, String barrio, double latitud, double longitud, String descripcion, String tipoFalla, int capacidadLitros, boolean elevado) {
        super(usuarioId, direccion, barrio, latitud, longitud, descripcion);
        this.tipoFalla = tipoFalla;
        setCapacidadLitros(capacidadLitros);
        this.elevado = elevado;
    }
 
    @Override
    public NivelRiesgo calcularNivelRiesgo() {
        NivelRiesgo nivel = switch (tipoFalla) {
            case "Colapso" -> elevado ? NivelRiesgo.CRITICO : NivelRiesgo.ALTO;
            case "Contaminación" -> NivelRiesgo.ALTO;
            case "Fuga" -> NivelRiesgo.MEDIO;
            default -> elevado ? NivelRiesgo.MEDIO : NivelRiesgo.BAJO; // Fisura
        };
        if (capacidadLitros >= 5000) {
            nivel = nivel.subir();
        }
        return nivel;
    }
 
    // lo sobreescribimos porque solo toca evacuar si el tanque elevado se cae
    @Override
    public boolean requiereEvacuacion() {
        return tipoFalla.equals("Colapso") && elevado;
    }
 
    @Override
    public TipoDaño getTipoDaño() {
        return TipoDaño.TANQUE_AGUA;
    }
 
    @Override
    public String getDetalle() {
        return tipoFalla + ", " + capacidadLitros + " L" + (elevado ? ", elevado" : "");
    }
 
    public String getTipoFalla() { return tipoFalla; }
    public void setTipoFalla(String tipoFalla) { this.tipoFalla = tipoFalla; }
 
    public int getCapacidadLitros() { return capacidadLitros; }
    public void setCapacidadLitros(int capacidadLitros) {
        if (capacidadLitros <= 0) {
            throw new IllegalArgumentException("La capacidad del tanque debe ser mayor que 0");
        }
        this.capacidadLitros = capacidadLitros;
    }
 
    public boolean isElevado() { return elevado; }
    public void setElevado(boolean elevado) { this.elevado = elevado; }
}

