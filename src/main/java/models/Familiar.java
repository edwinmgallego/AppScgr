package models;

/**
 * Vínculo entre un usuario y uno de sus familiares (tabla familiares).
 * familiarUsuarioId apunta al usuario registrado que es el familiar.
 *
 * @author Equipo 1 - Autenticación y Red Familiar
 */
public class Familiar {

    /** Valores permitidos por la columna estado_emergencia. */
    public static final String[] ESTADOS = {"DESCONOCIDO", "SEGURO", "NECESITA AYUDA"};

    private int id;
    private int usuarioId;
    private Integer familiarUsuarioId;
    private String documento;
    private String nombreFamiliar;
    private String telefono;
    private String estadoEmergencia;

    public Familiar(int id, int usuarioId, Integer familiarUsuarioId, String documento,
                    String nombreFamiliar, String telefono, String estadoEmergencia) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.familiarUsuarioId = familiarUsuarioId;
        this.documento = documento;
        this.nombreFamiliar = nombreFamiliar;
        this.telefono = telefono;
        this.estadoEmergencia = estadoEmergencia;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }

    public Integer getFamiliarUsuarioId() { return familiarUsuarioId; }
    public void setFamiliarUsuarioId(Integer familiarUsuarioId) { this.familiarUsuarioId = familiarUsuarioId; }

    public String getDocumento() { return documento; }
    public void setDocumento(String documento) { this.documento = documento; }

    public String getNombreFamiliar() { return nombreFamiliar; }
    public void setNombreFamiliar(String nombreFamiliar) { this.nombreFamiliar = nombreFamiliar; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getEstadoEmergencia() { return estadoEmergencia; }
    public void setEstadoEmergencia(String estadoEmergencia) { this.estadoEmergencia = estadoEmergencia; }
}
