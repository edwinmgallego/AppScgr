package models;
/**
 * Tipo de voto que un ciudadano puede emitir sobre un reporte de otro usuario.
 * Forma parte del RF-13 (validación comunitaria).
 *
 * @author santi
 */
public enum TipoVoto {
 CONFIRMA("Confirmar"),
 CUESTIONA("Cuestionar");
 private final String etiqueta;
 TipoVoto(String etiqueta) {
 this.etiqueta = etiqueta;
 }
 public String getEtiqueta() {
 return etiqueta;
 }
 public String getCodigo() {
 return name();
 }
}