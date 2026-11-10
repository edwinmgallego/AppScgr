package models;

/**
 * Representa a una persona registrada en el sistema (tabla usuarios).
 * No guarda la contraseña: esa solo la maneja UsuarioDAO.
 *
 * @author Equipo 1 - Autenticación y Red Familiar
 */
public class Usuario {
    private int id;
    private String documento;
    private String nombreCompleto;
    private String telefono;

    public Usuario(int id, String documento, String nombreCompleto, String telefono) {
        this.id = id;
        this.documento = documento;
        this.nombreCompleto = nombreCompleto;
        this.telefono = telefono;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getDocumento() { return documento; }
    public void setDocumento(String documento) { this.documento = documento; }

    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
}
