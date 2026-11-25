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
    public void testTiposDeOtrosEquiposTienenMarcadorPropio() {
        assertEquals(TipoMarcador.FALLA_SERVICIO, TipoMarcador.desdeTipoIncidente("FALLA_SERVICIO"));
        assertEquals(TipoMarcador.SIN_SENAL, TipoMarcador.desdeTipoIncidente("SIN_SENAL"));
        assertEquals(TipoMarcador.DANO_ESTRUCTURAL, TipoMarcador.desdeTipoIncidente("DANO_ESTRUCTURAL"));
    }

    @Test
    public void testTipoDesconocidoEsOtro() {
        assertEquals(TipoMarcador.OTRO, TipoMarcador.desdeTipoIncidente("INUNDACION"));
    }

    @Test
    public void testSoloOtroUsaLaEtiquetaGenerica() {
        for (String tipo : new String[]{"VIA_CERRADA", "DERRUMBE", "FALLA_SERVICIO", "SIN_SENAL", "DANO_ESTRUCTURAL"}) {
            assertNotEquals(TipoMarcador.OTRO, TipoMarcador.desdeTipoIncidente(tipo), tipo);
        }
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
    public void testMarcadoresQueNoVienenDeLaTablaDeIncidentes() {
        // Estos tipos los arma el mapa; un texto igual en la BD no debe confundirse con ellos
        assertEquals(TipoMarcador.OTRO, TipoMarcador.desdeTipoIncidente("VIA_ABIERTA"));
        assertEquals(TipoMarcador.OTRO, TipoMarcador.desdeTipoIncidente("ESTACION_MIO"));
        assertEquals(TipoMarcador.OTRO, TipoMarcador.desdeTipoIncidente("ESTACION_MIO_NO_OPERATIVA"));
    }

    @Test
    public void testEtiqueta() {
        assertEquals("Vía cerrada", TipoMarcador.VIA_CERRADA.getEtiqueta());
        assertEquals("Derrumbe", TipoMarcador.DERRUMBE.toString());
        assertEquals("Falla de servicio", TipoMarcador.FALLA_SERVICIO.getEtiqueta());
        assertEquals("Sin señal", TipoMarcador.SIN_SENAL.getEtiqueta());
        assertEquals("Daño estructural", TipoMarcador.DANO_ESTRUCTURAL.getEtiqueta());
        assertEquals("MIO no operativa", TipoMarcador.ESTACION_MIO_NO_OPERATIVA.getEtiqueta());
    }
}
