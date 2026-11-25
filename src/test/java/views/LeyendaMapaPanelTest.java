package views;

import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JPanel;
import models.TipoMarcador;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas de la leyenda del mapa: una entrada por tipo de marcador, con su color y su nombre.
 *
 * @author equipo3/samuellopez
 */
public class LeyendaMapaPanelTest {

    @Test
    public void testUnaEntradaPorTipo() {
        LeyendaMapaPanel leyenda = new LeyendaMapaPanel();
        assertEquals(TipoMarcador.values().length, leyenda.getComponentCount());
    }

    @Test
    public void testCadaEntradaTieneColorYNombre() {
        LeyendaMapaPanel leyenda = new LeyendaMapaPanel();
        TipoMarcador[] tipos = TipoMarcador.values();
        for (int i = 0; i < tipos.length; i++) {
            Component[] partes = ((JPanel) leyenda.getComponent(i)).getComponents();
            JLabel punto = (JLabel) partes[0];
            JLabel nombre = (JLabel) partes[1];
            assertEquals(MarcadorRenderer.colorDe(tipos[i]), punto.getForeground(), tipos[i].name());
            assertEquals(tipos[i].getEtiqueta(), nombre.getText());
        }
    }
}
