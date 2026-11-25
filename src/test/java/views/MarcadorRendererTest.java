package views;

import java.awt.Color;
import java.util.HashSet;
import java.util.Set;
import models.TipoMarcador;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas de los colores de los marcadores del mapa (no necesitan base de datos).
 *
 * @author equipo3/samuellopez
 */
public class MarcadorRendererTest {

    @Test
    public void testTodoTipoTieneColor() {
        for (TipoMarcador tipo : TipoMarcador.values()) {
            assertNotNull(MarcadorRenderer.colorDe(tipo), tipo.name());
        }
    }

    @Test
    public void testCadaTipoTieneUnColorDistinto() {
        Set<Color> colores = new HashSet<>();
        for (TipoMarcador tipo : TipoMarcador.values()) {
            assertTrue(colores.add(MarcadorRenderer.colorDe(tipo)), "Color repetido en " + tipo.name());
        }
    }

    @Test
    public void testColoresDeLasCategoriasPrincipales() {
        assertEquals(new Color(210, 40, 40), MarcadorRenderer.colorDe(TipoMarcador.VIA_CERRADA));
        assertEquals(new Color(40, 167, 69), MarcadorRenderer.colorDe(TipoMarcador.VIA_ABIERTA));
        assertEquals(new Color(0, 114, 188), MarcadorRenderer.colorDe(TipoMarcador.ESTACION_MIO));
        assertEquals(new Color(110, 110, 110), MarcadorRenderer.colorDe(TipoMarcador.OTRO));
    }

    @Test
    public void testEstacionNoOperativaSeDistingueDeLaOperativa() {
        assertNotEquals(MarcadorRenderer.colorDe(TipoMarcador.ESTACION_MIO),
                MarcadorRenderer.colorDe(TipoMarcador.ESTACION_MIO_NO_OPERATIVA));
    }
}
