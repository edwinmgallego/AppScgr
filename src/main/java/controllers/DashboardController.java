package controllers;
import config.Sesion;
import models.Incidente;
import models.Usuario;
import models.dao.IncidenteDAO;
import models.dao.ValidacionDAO;
import views.CoberturaMovilView;
import views.DashboardView;
import views.EstadoView;
import views.FamiliaresView;
import views.LoginView;
import views.MapaView;
import views.PerfilView;
import views.ServiciosZonaView;
import views.ValidacionView;
import views.daños.DañosEstructuralesView;
import javax.swing.JOptionPane;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
/**
 * Controlador del Dashboard principal (RF-14).
 * El menú lateral abre los módulos ya existentes sin reemplazarlos ni duplicarlos.
 *
 * @author Grupo 9 - Validación Comunitaria y Dashboard
 */
public class DashboardController extends BaseController<DashboardView> {
 private final IncidenteDAO incidenteDAO;
 private final ValidacionDAO validacionDAO;
 public DashboardController(DashboardView vista) {
 super(vista);
 this.incidenteDAO = new IncidenteDAO();
 this.validacionDAO = new ValidacionDAO();
 inicializarEventos();
 }
 @Override
 protected void inicializarEventos() {
 vista.getMenuLista().addListSelectionListener(e -> {
 if (!e.getValueIsAdjusting()) {
 String seleccion = vista.getMenuLista().getSelectedValue();
 if (seleccion != null) {
 abrirModulo(seleccion);
 }
 }
 });
 }
 @Override
 public void iniciar() {
 Usuario usuario = Sesion.getUsuarioActual();
 if (usuario != null) {
 vista.getLblBienvenida().setText("Bienvenido, " + usuario.getNombreCompleto());
 vista.setRol("Sesión iniciada con documento " + usuario.getDocumento());
 }
 actualizarIndicadores();
 super.iniciar();
 }
 private void actualizarIndicadores() {
 List<Incidente> incidentes = incidenteDAO.obtenerTodosLosIncidentes();
 List<Integer> ids = new ArrayList<>();
 for (Incidente inc : incidentes) {
 ids.add(inc.getId());
 }
 Map<Integer, int[]> votos = validacionDAO.contarVotos(ids);
 int verificados = 0;
 int totalVotos = 0;
 for (Incidente inc : incidentes) {
 int[] contadores = votos.getOrDefault(inc.getId(), new int[]{0, 0});
 int confirmaciones = contadores[0];
 int cuestionamientos = contadores[1];
 totalVotos += confirmaciones + cuestionamientos;
 String origen = validacionDAO.obtenerOrigenReporte(inc.getId());
 boolean oficial = "OFICIAL".equalsIgnoreCase(origen);
 if (oficial || confirmaciones >= 3) {
 verificados++;
 }
 }
 vista.mostrarIndicadores(incidentes.size(), verificados, totalVotos);
 vista.getLblResumen().setText("<html><div style='text-align:center'>"
 + "Seleccione un módulo del menú lateral.<br>"
 + "<b>Incidentes:</b> " + incidentes.size()
 + " &nbsp;|&nbsp; <b>Verificados:</b> " + verificados
 + " &nbsp;|&nbsp; <b>Votos:</b> " + totalVotos
 + "</div></html>");
 }
 private void abrirModulo(String modulo) {
 switch (modulo) {
 case "Cerrar sesión":
 cerrarSesion();
 break;
 case "Resumen":
 actualizarIndicadores();
 break;
 case "Incidentes (Mapa)":
 new MapaController(new MapaView()).iniciar();
 break;
 case "Red familiar":
 new FamiliaresController(new FamiliaresView(vista)).iniciar();
 break;
 case "Estado de supervivencia":
 new EstadoController(new EstadoView(), () -> null).iniciar();
 break;
 case "Mi perfil":
 new PerfilController(new PerfilView(vista)).iniciar();
 break;
 case "Daños estructurales":
 new DañosController(new DañosEstructuralesView(), idUsuarioActual()).iniciar();
 break;
 case "Rutas alternas":
 new RutasModuloController(new views.RutasView()).iniciar();
 break;
 case "Validación comunitaria":
 new ValidacionController(new ValidacionView()).iniciar();
 break;
 case "Servicios por zona":
 new ServiciosZonaController(new ServiciosZonaView()).iniciar();
 break;
 case "Cobertura móvil":
 new CoberturaMovilController(new CoberturaMovilView()).iniciar();
 break;
 default:
 break;
 }
 }
 private int idUsuarioActual() {
 Usuario usuario = Sesion.getUsuarioActual();
 return usuario != null ? usuario.getId() : 1;
 }
 private void cerrarSesion() {
 int respuesta = JOptionPane.showConfirmDialog(vista,
 "¿Seguro que desea cerrar sesión?",
 "Cerrar sesión", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
 if (respuesta != JOptionPane.YES_OPTION) {
 return;
 }
 Sesion.cerrar();
 vista.dispose();
 new LoginController(new LoginView()).iniciar();
 }
}