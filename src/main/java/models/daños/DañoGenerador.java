/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models.daños;

/**
 *
 * @author juans
 */
public class DañoGenerador extends DañoEstructural {
 
    public static final String[] FALLAS = {"No enciende", "Fuga de combustible", "Cortocircuito", "Daño físico"};
 
    private String tipoFalla;
    private double potenciaKva;
    private boolean servicioCritico;
 
    public DañoGenerador(int usuarioId, String direccion, String barrio, double latitud, double longitud, String descripcion, String tipoFalla, double potenciaKva, boolean servicioCritico) {
        super(usuarioId, direccion, barrio, latitud, longitud, descripcion);
        this.tipoFalla = tipoFalla;
        setPotenciaKva(potenciaKva);
        this.servicioCritico = servicioCritico;
    }
 
    @Override
    public NivelRiesgo calcularNivelRiesgo() {
        NivelRiesgo nivel = switch (tipoFalla) {
            case "Fuga de combustible" -> NivelRiesgo.CRITICO;
            case "Cortocircuito" -> NivelRiesgo.ALTO;
            case "Daño físico" -> NivelRiesgo.MEDIO;
            default -> NivelRiesgo.BAJO; // No enciende
        };
        return servicioCritico ? nivel.subir() : nivel;
    }
 
    @Override
    public TipoDaño getTipoDaño() {
        return TipoDaño.GENERADOR;
    }
 
    @Override
    public String getDetalle() {
        return tipoFalla + ", " + potenciaKva + " kVA" + (servicioCritico ? ", servicio crítico" : "");
    }
 
    public String getTipoFalla() { return tipoFalla; }
    public void setTipoFalla(String tipoFalla) { this.tipoFalla = tipoFalla; }
 
    public double getPotenciaKva() { return potenciaKva; }
    public void setPotenciaKva(double potenciaKva) {
        if (potenciaKva <= 0) {
            throw new IllegalArgumentException("La potencia debe ser mayor que 0");
        }
        this.potenciaKva = potenciaKva;
    }
 
    public boolean isServicioCritico() { return servicioCritico; }
    public void setServicioCritico(boolean servicioCritico) { this.servicioCritico = servicioCritico; }
}

