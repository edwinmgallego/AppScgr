/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models.dao;

/**
 *
 * @author juans
 */

import models.daños.DañoGenerador;
import models.daños.FallaTanque;
import models.daños.Grieta;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
 
public class FormatoDescripcionTest {
 
    @Test
    public void testCodificarGrieta() {
        Grieta g = new Grieta(1, "Cra 4 # 12-40", "San Antonio", 3.44, -76.53, "Grieta diagonal", "Columna", 4.5, 80);
        assertEquals("daño=GRIETA | direccion=Cra 4 # 12-40 | barrio=San Antonio | elemento=Columna"
                   + " | ancho=4.5 | largo=80.0 | nota=Grieta diagonal", FormatoDescripcion.codificar(g));
    }
 
    @Test
    public void testIdaYVueltaTanque() {
        FallaTanque t = new FallaTanque(1, "Av 6N", "Granada", 3.46, -76.53, "", "Colapso", 6000, true);
        FallaTanque leido = (FallaTanque) FormatoDescripcion.crear(7, 1, 3.46, -76.53, FormatoDescripcion.codificar(t));
        assertEquals(7, leido.getId());
        assertEquals("Colapso", leido.getTipoFalla());
        assertEquals(6000, leido.getCapacidadLitros());
        assertTrue(leido.isElevado());
    }
 
    @Test
    public void testIdaYVueltaGenerador() {
        DañoGenerador g = new DañoGenerador(1, "Cl 70", "Alfonso López", 3.47, -76.49, "Olor a ACPM", "Cortocircuito", 60, true);
        DañoGenerador leido = (DañoGenerador) FormatoDescripcion.crear(3, 1, 3.47, -76.49, FormatoDescripcion.codificar(g));
        assertEquals("Alfonso López", leido.getBarrio());
        assertEquals(60, leido.getPotenciaKva());
        assertEquals("Olor a ACPM", leido.getDescripcion());
    }
 
    @Test
    public void testBarraVerticalSeReemplaza() {
        Grieta g = new Grieta(1, "Cra 4 | Local 2", "Centro", 3, -76, "", "Muro", 1, 10);
        Grieta leida = (Grieta) FormatoDescripcion.crear(1, 1, 3, -76, FormatoDescripcion.codificar(g));
        assertEquals("Cra 4 / Local 2", leida.getDireccion());
    }
 
    @Test
    public void testTextoLibreLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> FormatoDescripcion.crear(1, 1, 3, -76, "Se cayó una pared"));
    }
}
