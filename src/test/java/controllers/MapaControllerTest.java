package controllers;

import models.EstacionMio;
import models.EstadoVia;
import models.Incidente;
import views.MapaView;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas de la lógica del formulario del mapa que no necesita ventana ni base de datos.
 *
 * @author equipo3/samuellopez
 */
public class MapaControllerTest {

    @Test
    public void testFormularioCompletoEsValido() {
        assertNull(MapaController.validarFormulario("3.45", "VIA_CERRADA", "Cierre por obras"));
    }

    @Test
    public void testFaltaLaUbicacion() {
        assertEquals("Haga clic en el mapa para seleccionar la ubicación.",
                MapaController.validarFormulario("", "VIA_CERRADA", "Cierre"));
        assertNotNull(MapaController.validarFormulario(null, "VIA_CERRADA", "Cierre"));
    }

    @Test
    public void testFaltaElTipo() {
        assertEquals("Seleccione el tipo de incidente.",
                MapaController.validarFormulario("3.45", " ", "Cierre"));
    }

    @Test
    public void testFaltaLaDescripcion() {
        assertEquals("Escriba una descripción del incidente.",
                MapaController.validarFormulario("3.45", "DERRUMBE", "   "));
    }

    @Test
    public void testTextoDelBotonDeEstadoDeVia() {
        Incidente cerrada = new Incidente(1, "VIA_CERRADA", 3.45, -76.53, "", EstadoVia.CERRADA);
        Incidente abierta = new Incidente(2, "VIA_CERRADA", 3.45, -76.53, "", EstadoVia.ABIERTA);
        assertEquals(MapaView.TEXTO_REABRIR_VIA, MapaController.textoBotonEstadoVia(cerrada));
        assertEquals(MapaView.TEXTO_CERRAR_VIA, MapaController.textoBotonEstadoVia(abierta));
        assertEquals(MapaView.TEXTO_REABRIR_VIA, MapaController.textoBotonEstadoVia(null));
    }

    @Test
    public void testTextoDelBotonDeEstacion() {
        EstacionMio operativa = new EstacionMio(1, "Estación Capri", 3.3958, -76.5449, true);
        EstacionMio cerrada = new EstacionMio(2, "Estación Estadio", 3.4285, -76.5401, false);
        assertEquals(MapaView.TEXTO_ESTACION_NO_OPERATIVA, MapaController.textoBotonEstadoEstacion(operativa));
        assertEquals(MapaView.TEXTO_ESTACION_OPERATIVA, MapaController.textoBotonEstadoEstacion(cerrada));
        assertEquals(MapaView.TEXTO_ESTACION_NO_OPERATIVA, MapaController.textoBotonEstadoEstacion(null));
    }
}
