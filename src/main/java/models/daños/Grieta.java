/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models.daños;

/**
 *
 * @author juans
 */
public class Grieta extends DañoEstructural {
 
    public static final String[] ELEMENTOS = {"Muro", "Columna", "Viga", "Losa", "Piso"};
 
    private String elemento;
    private double anchoMm;
    private double largoCm;
 
    public Grieta(int usuarioId, String direccion, String barrio, double latitud, double longitud, String descripcion, String elemento, double anchoMm, double largoCm) {
        super(usuarioId, direccion, barrio, latitud, longitud, descripcion);
        this.elemento = elemento;
        setAnchoMm(anchoMm);
        setLargoCm(largoCm);
    }
 
    // estos son los que sostienen la casa
    public boolean esEstructural() {
        return elemento.equals("Columna") || elemento.equals("Viga") || elemento.equals("Losa");
    }
 
    @Override
    public NivelRiesgo calcularNivelRiesgo() {
        NivelRiesgo nivel;
        if (esEstructural()) {
            if (anchoMm >= 3) nivel = NivelRiesgo.CRITICO;
            else if (anchoMm >= 1) nivel = NivelRiesgo.ALTO;
            else nivel = NivelRiesgo.MEDIO;
        } else {
            if (anchoMm >= 5) nivel = NivelRiesgo.ALTO;
            else if (anchoMm >= 1) nivel = NivelRiesgo.MEDIO;
            else nivel = NivelRiesgo.BAJO;
        }
        if (largoCm >= 100) {
            nivel = nivel.subir();
        }
        return nivel;
    }
 
    @Override
    public TipoDaño getTipoDaño() {
        return TipoDaño.GRIETA;
    }
 
    @Override
    public String getDetalle() {
        return elemento + ", " + anchoMm + " mm de ancho, " + largoCm + " cm de largo";
    }
 
    public String getElemento() { return elemento; }
    public void setElemento(String elemento) { this.elemento = elemento; }
 
    public double getAnchoMm() { return anchoMm; }
    public void setAnchoMm(double anchoMm) {
        if (anchoMm <= 0) {
            throw new IllegalArgumentException("El ancho de la grieta debe ser mayor que 0");
        }
        this.anchoMm = anchoMm;
    }
 
    public double getLargoCm() { return largoCm; }
    public void setLargoCm(double largoCm) {
        if (largoCm <= 0) {
            throw new IllegalArgumentException("El largo de la grieta debe ser mayor que 0");
        }
        this.largoCm = largoCm;
    }
    
}

