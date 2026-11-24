/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

/**
 *
 * @author USER
 */
public enum EstadoSupervivencia {

    ESTOY_BIEN("Estoy bien") {
        @Override
        public boolean esEmergencia() {
            return false;
        }

        @Override
        public String getEstadoRed() {
            return "SEGURO";
        }
    },
    NECESITO_AYUDA("Necesito ayuda") {
        @Override
        public boolean esEmergencia() {
            return true;
        }

        @Override
        public String getEstadoRed() {
            return "NECESITA AYUDA";
        }
    };

    private final String etiqueta;

    EstadoSupervivencia(String etiqueta) {
        this.etiqueta = etiqueta;
    }
    public String getEtiqueta() {
        return etiqueta;
    }

    public abstract boolean esEmergencia();

    public abstract String getEstadoRed();

    @Override
    public String toString() {
        return etiqueta;
    }
}
