/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

/**
 * Punto con etiqueta, ubicación y categoría que se dibuja sobre el mapa.
 *
 * @author equipo3/samuellopez
 */
public class MarcadorMapa implements Geolocalizable {

    private String etiqueta;
    private double latitud;
    private double longitud;
    private TipoMarcador tipo;

    public MarcadorMapa(String etiqueta, double latitud, double longitud, TipoMarcador tipo) {
        setTipo(tipo);
        setEtiqueta(etiqueta);
        setLatitud(latitud);
        setLongitud(longitud);
    }

    /** sobrecarga de constructor: la etiqueta es el nombre de la categoría. */
    public MarcadorMapa(double latitud, double longitud, TipoMarcador tipo) {
        this(tipo == null ? null : tipo.getEtiqueta(), latitud, longitud, tipo);
    }

    /** Crea el marcador de un incidente guardado en la base de datos. */
    public static MarcadorMapa desdeIncidente(Incidente incidente) {
        if (incidente == null) {
            throw new IllegalArgumentException("El incidente es obligatorio.");
        }
        TipoMarcador tipo = TipoMarcador.desdeTipoIncidente(incidente.getTipo());
        return new MarcadorMapa(tipo.getEtiqueta(), incidente.getLatitud(), incidente.getLongitud(), tipo);
    }

    /**
     * El mapa se repite horizontalmente: al arrastrarlo, un clic puede dar longitudes
     * fuera de [-180, 180]. Este método las lleva de vuelta a ese rango.
     */
    public static double normalizarLongitud(double longitud) {
        return ((longitud + 180) % 360 + 360) % 360 - 180;
    }

    public String getEtiqueta() { return etiqueta; }

    public final void setEtiqueta(String etiqueta) {
        if (etiqueta == null || etiqueta.isBlank()) {
            throw new IllegalArgumentException("La etiqueta del marcador es obligatoria.");
        }
        this.etiqueta = etiqueta.trim();
    }

    @Override
    public double getLatitud() { return latitud; }

    public final void setLatitud(double latitud) {
        if (Double.isNaN(latitud) || latitud < -90 || latitud > 90) {
            throw new IllegalArgumentException("La latitud debe estar entre -90 y 90.");
        }
        this.latitud = latitud;
    }

    @Override
    public double getLongitud() { return longitud; }

    public final void setLongitud(double longitud) {
        if (Double.isNaN(longitud) || longitud < -180 || longitud > 180) {
            throw new IllegalArgumentException("La longitud debe estar entre -180 y 180.");
        }
        this.longitud = longitud;
    }

    public TipoMarcador getTipo() { return tipo; }

    public final void setTipo(TipoMarcador tipo) {
        if (tipo == null) {
            throw new IllegalArgumentException("El tipo de marcador es obligatorio.");
        }
        this.tipo = tipo;
    }

    @Override
    public String toString() {
        return tipo + ": " + etiqueta + " (" + latitud + ", " + longitud + ")";
    }
}
