/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas de la traducción de tipos de incidente a tipos de marcador (no necesitan base de datos).
 *
 * @author equipo3/samuellopez
 */
public class TipoMarcadorTest {

    @Test
    public void testReconoceViaCerradaYDerrumbe() {
        assertEquals(TipoMarcador.VIA_CERRADA, TipoMarcador.desdeTipoIncidente("VIA_CERRADA"));
        assertEquals(TipoMarcador.DERRUMBE, TipoMarcador.desdeTipoIncidente("DERRUMBE"));
    }

    @Test
    public void testIgnoraMayusculasYEspacios() {
        assertEquals(TipoMarcador.DERRUMBE, TipoMarcador.desdeTipoIncidente("  derrumbe "));
    }

    @Test
    public void testTiposDeOtrosEquiposSeMuestranComoOtro() {
        assertEquals(TipoMarcador.OTRO, TipoMarcador.desdeTipoIncidente("FALLA_SERVICIO"));
        assertEquals(TipoMarcador.OTRO, TipoMarcador.desdeTipoIncidente("SIN_SENAL"));
        assertEquals(TipoMarcador.OTRO, TipoMarcador.desdeTipoIncidente("DANO_ESTRUCTURAL"));
    }

    @Test
    public void testNuloOVacioEsOtro() {
        assertEquals(TipoMarcador.OTRO, TipoMarcador.desdeTipoIncidente(null));
        assertEquals(TipoMarcador.OTRO, TipoMarcador.desdeTipoIncidente(""));
    }

    @Test
    public void testSeleccionNoSeConfundeConUnIncidente() {
        assertEquals(TipoMarcador.OTRO, TipoMarcador.desdeTipoIncidente("SELECCION"));
    }

    @Test
    public void testEtiqueta() {
        assertEquals("Vía cerrada", TipoMarcador.VIA_CERRADA.getEtiqueta());
        assertEquals("Derrumbe", TipoMarcador.DERRUMBE.toString());
    }
}
