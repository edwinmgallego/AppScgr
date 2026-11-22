/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
/**
 *
 * @author Titoelgato
 */
public class ServiciosPuntoTest {
    
    @Test
    public void testDesdeTextoLeeServiciosYVoluntarios() {
        ServiciosPunto s = ServiciosPunto.desdeTexto("AGUA,INTERNET,ALIMENTACION;VOLUNTARIOS=5");

        assertEquals(List.of("AGUA", "INTERNET", "ALIMENTACION"), s.getServicios());
        assertEquals(5, s.getVoluntariosNecesarios());
    }

}
