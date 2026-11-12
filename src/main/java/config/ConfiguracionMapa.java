/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package config;

/**
 * Valores de configuración del mapa interactivo (OpenStreetMap con JXMapViewer2).
 * Centraliza lo que antes estaba escrito a mano dentro de MapaView.
 *
 * @author equipo3/samuellopez
 */
public final class ConfiguracionMapa {

    /** Centro inicial del mapa: Santiago de Cali. */
    public static final double CALI_LATITUD = 3.4516;
    public static final double CALI_LONGITUD = -76.5320;

    /** Zoom inicial de JXMapViewer2 (0 es el más cercano; los valores mayores alejan la vista). */
    public static final int ZOOM_INICIAL = 7;

    /** OpenStreetMap bloquea las peticiones que no se identifican con un User-Agent. */
    public static final String USER_AGENT = "AppScgr-POO-Cali/1.0";

    private ConfiguracionMapa() { }

    /** Fuerza HTTPS: OpenStreetMap rechaza las teselas pedidas por HTTP plano. */
    public static String aHttps(String url) {
        return url == null ? null : url.replace("http://", "https://");
    }
}
