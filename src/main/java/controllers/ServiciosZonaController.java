package controllers;
import config.Sesion;
import models.ReporteServicioZona;
import models.dao.ServicioZonaDAO;
import views.ServiciosZonaView;
import javax.swing.JOptionPane;
import java.util.List;
/**
 * Controlador del módulo Servicios por zona (RF-06).
 *
 * @author santi
 */
public class ServiciosZonaController extends BaseController<ServiciosZonaView> {
 private final ServicioZonaDAO dao;
 private int seleccionadoId = -1;
 public ServiciosZonaController(ServiciosZonaView vista) {
 super(vista);
 this.dao = new ServicioZonaDAO();
 inicializarEventos();
 }
 @Override
 protected void inicializarEventos() {
 vista.getBtnGuardar().addActionListener(e -> guardar());
 vista.getBtnActualizar().addActionListener(e -> actualizar());
 vista.getBtnEliminar().addActionListener(e -> eliminar());
 vista.getBtnLimpiar().addActionListener(e -> limpiar());
 vista.getTabla().getSelectionModel().addListSelectionListener(e -> {
 if (!e.getValueIsAdjusting()) {
 cargarSeleccion();
 }
 });
 }
 @Override
 public void iniciar() {
 cargarTabla();
 super.iniciar();
 }
 private void cargarTabla() {
 vista.getModeloTabla().setRowCount(0);
 for (ReporteServicioZona r : dao.listar()) {
 vista.getModeloTabla().addRow(new Object[]{
 r.getId(),
 r.getZonaBarrio(),
 r.getServicio(),
 r.getEstado(),
 r.getVerificaciones(),
 r.getFechaReporte()
 });
 }
 }
 private void guardar() {
 try {
 ReporteServicioZona r = leerFormulario();
 int idUsuario = usuarioActual();
 r.setUsuarioId(idUsuario);
 int id = dao.insertar(r);
 if (id > 0) {
 vista.setEstado("Servicio guardado con ID " + id + ".");
 limpiar();
 cargarTabla();
 } else {
 vista.setEstado("No se pudo guardar el servicio.");
 }
 } catch (IllegalArgumentException ex) {
 vista.setEstado(ex.getMessage());
 }
 }
 private void actualizar() {
 if (seleccionadoId < 0) {
 vista.setEstado("Seleccione un registro de la tabla.");
 return;
 }
 try {
 ReporteServicioZona r = leerFormulario();
 r.setId(seleccionadoId);
 r.setUsuarioId(usuarioActual());
 if (dao.actualizar(r)) {
 vista.setEstado("Servicio actualizado.");
 limpiar();
 cargarTabla();
 } else {
 vista.setEstado("No se pudo actualizar.");
 }
 } catch (IllegalArgumentException ex) {
 vista.setEstado(ex.getMessage());
 }
 }
 private void eliminar() {
 if (seleccionadoId < 0) {
 vista.setEstado("Seleccione un registro de la tabla.");
 return;
 }
 if (JOptionPane.showConfirmDialog(vista, "¿Eliminar el registro #" + seleccionadoId + "?",
 "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
 dao.eliminar(seleccionadoId);
 limpiar();
 cargarTabla();
 }
 }
 private void limpiar() {
 seleccionadoId = -1;
 vista.getTabla().clearSelection();
 vista.getTxtZona().setText("");
 vista.getTxtObservaciones().setText("");
 vista.getTxtVerificaciones().setText("0");
 vista.getCbxServicio().setSelectedIndex(0);
 vista.getCbxEstado().setSelectedIndex(0);
 }
 private void cargarSeleccion() {
 int fila = vista.getTabla().getSelectedRow();
 if (fila < 0) {
 return;
 }
 int id = (Integer) vista.getModeloTabla().getValueAt(fila, 0);
 ReporteServicioZona r = dao.buscarPorId(id);
 if (r == null) {
 return;
 }
 seleccionadoId = r.getId();
 vista.getTxtZona().setText(r.getZonaBarrio());
 vista.getCbxServicio().setSelectedItem(r.getServicio());
 vista.getCbxEstado().setSelectedItem(r.getEstado());
 vista.getTxtObservaciones().setText(r.getObservaciones() == null ? "" : r.getObservaciones());
 vista.getTxtVerificaciones().setText(String.valueOf(r.getVerificaciones()));
 }
 private ReporteServicioZona leerFormulario() {
 String zona = vista.getTxtZona().getText().trim();
 if (zona.isEmpty()) {
 throw new IllegalArgumentException("La zona o barrio es obligatorio.");
 }
 int verificaciones;
 try {
 verificaciones = Integer.parseInt(vista.getTxtVerificaciones().getText().trim());
 } catch (NumberFormatException ex) {
 throw new IllegalArgumentException("Las verificaciones deben ser un número.");
 }
 ReporteServicioZona r = new ReporteServicioZona(
 0,
 zona,
 (String) vista.getCbxServicio().getSelectedItem(),
 (String) vista.getCbxEstado().getSelectedItem(),
 vista.getTxtObservaciones().getText().trim());
 r.setVerificaciones(verificaciones);
 return r;
 }
 private int usuarioActual() {
 return Sesion.hayUsuario() ? Sesion.getUsuarioActual().getId() : 1;
 }
}