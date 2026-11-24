// PROVISIONAL: implementación mínima de referencia para poder ejecutar las pruebas del Miembro 4.
// Será reemplazada por la versión real del Miembro 1. Respeta el contrato acordado.
package rutas.modelo;

/** Arco dirigido con peso (distancia en metros). Puede estar bloqueado por un cierre vial. */
public class Arista {
    private final int origen;
    private final int destino;
    private final double peso;
    private boolean bloqueada;

    public Arista(int origen, int destino, double peso) {
        this.origen = origen;
        this.destino = destino;
        this.peso = peso;
    }

    public int getOrigen() { return origen; }
    public int getDestino() { return destino; }
    public double getPeso() { return peso; }
    public boolean isBloqueada() { return bloqueada; }
    public void setBloqueada(boolean bloqueada) { this.bloqueada = bloqueada; }

    @Override
    public String toString() {
        return origen + "->" + destino + " (" + peso + (bloqueada ? ", BLOQUEADA" : "") + ")";
    }
}
