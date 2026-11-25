package models;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas del estado de una vía: lectura desde la base de datos y cambio de estado.
 *
 * @author equipo3/samuellopez
 */
public class EstadoViaTest {

    @Test
    public void testOpuesto() {
        assertEquals(EstadoVia.CERRADA, EstadoVia.ABIERTA.opuesto());
        assertEquals(EstadoVia.ABIERTA, EstadoVia.CERRADA.opuesto());
    }

    @Test
    public void testFromStringReconoceNombreYEtiqueta() {
        assertEquals(EstadoVia.ABIERTA, EstadoVia.fromString("ABIERTA"));
        assertEquals(EstadoVia.ABIERTA, EstadoVia.fromString(" abierta "));
        assertEquals(EstadoVia.CERRADA, EstadoVia.fromString("Cerrada"));
    }

    @Test
    public void testValoresDeOtroEquipoSeLeenComoCerrada() {
        // El Equipo 5 guarda ACTIVO/RESUELTO en la misma columna 'estado'
        assertEquals(EstadoVia.CERRADA, EstadoVia.fromString("ACTIVO"));
        assertEquals(EstadoVia.CERRADA, EstadoVia.fromString("RESUELTO"));
        assertEquals(EstadoVia.CERRADA, EstadoVia.fromString(null));
    }

    @Test
    public void testEtiquetas() {
        assertEquals("Abierta", EstadoVia.ABIERTA.getEtiqueta());
        assertEquals("Cerrada", EstadoVia.CERRADA.getEtiqueta());
    }
}
