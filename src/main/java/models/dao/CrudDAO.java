/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models.dao;
import java.util.List;

/**
 *
 * @author alex
 */
public interface CrudDAO<T> {
 
    int insertar(T objeto);        // devuelve el id nuevo o -1 si falla
 
    T buscarPorId(int id);         // null si no existe
 
    List<T> listar();
 
    boolean actualizar(T objeto);
 
    boolean eliminar(int id);
}

