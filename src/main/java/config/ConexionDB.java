/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package config;

/**
 *
 * @author inged
 */


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class ConexionDB {
    
    // 1. Atributo estático que almacena la única instancia de la clase
    private static ConexionDB instancia;
    
    // Atributo para manejar la conexión real de Java a MySQL
    private Connection conexion;
    
    // Credenciales por defecto de XAMPP
    private final String URL = "jdbc:mysql://localhost:3306/emergencias_db?useSSL=false&serverTimezone=UTC";
    private final String USUARIO = "root";
    private final String CONTRASENA = ""; // XAMPP normalmente no tiene contraseña por defecto

    // 2. Constructor privado: Evita que otras clases hagan "new ConexionDB()"
    private ConexionDB() {
        try {
            // Se establece la conexión con la base de datos
            conexion = DriverManager.getConnection(URL, USUARIO, CONTRASENA);
            System.out.println("Conexión a MySQL (emergencias_db) establecida con éxito.");
        } catch (SQLException e) {
            System.err.println("Error al conectar con la base de datos: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Error de conexión a la base de datos. Verifique que XAMPP esté encendido.", "Error Crítico", JOptionPane.ERROR_MESSAGE);
        }
    }

    // 3. Método estático público: Es la única forma de obtener la instancia
    public static ConexionDB getInstance() {
        try {
            // Si la instancia no existe o la conexión se cerró inesperadamente, se crea una nueva
            // (Si la conexión anterior falló, getConnection() es null y también se reintenta)
            if (instancia == null || instancia.getConnection() == null || instancia.getConnection().isClosed()) {
                instancia = new ConexionDB();
            }
        } catch (SQLException e) {
            System.err.println("Error al verificar el estado de la conexión: " + e.getMessage());
        }
        return instancia;
    }

    // Método para exponer el objeto Connection a los Modelos/DAO
    public Connection getConnection() {
        return conexion;
    }
    
    // Método opcional para cerrar la conexión al salir del sistema
    public void desconectar() {
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
                System.out.println("Conexión a base de datos cerrada.");
            }
        } catch (SQLException e) {
            System.err.println("Error al cerrar la conexión: " + e.getMessage());
        }
    }
}
