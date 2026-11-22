package controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import models.CentroAcopio;
import models.EstadoPunto;
import models.PuntoAsistencia;
import models.ServiciosPunto;

/**
 * Lógica del formulario de puntos de asistencia: convierte los textos que escribe el
 * usuario en un punto válido, filtra la lista por tipo y arma los servicios que se
 * muestran. No abre ventanas ni usa la base de datos, por eso se puede probar con JUnit.
 *
 * @author Equipo 7 - Puntos de Asistencia y Negocios
 */
public final class PuntoAsistenciaFormulario {

    public static final String TODOS = "TODOS";
    public static final String CENTRO_ACOPIO = "CENTRO_ACOPIO";

    private PuntoAsistenciaFormulario() { }

    /**
     * Crea el punto a partir de lo escrito en el formulario. Si algo está mal lanza
     * IllegalArgumentException con un mensaje que se le puede mostrar al usuario.
     * El CENTRO_ACOPIO se guarda como ALBERGUE con la marca ACOPIO en los servicios.
     */
    public static PuntoAsistencia construirPunto(int id, String tipo, String nombre,
            String latitud, String longitud, String estado, String servicios, int voluntarios) {
        if (tipo == null || tipo.isBlank()) {
            throw new IllegalArgumentException("Seleccione el tipo de punto.");
        }
        String codigo = tipo.trim().toUpperCase(Locale.ROOT);
        double lat = leerCoordenada(latitud, "latitud");
        double lon = leerCoordenada(longitud, "longitud");
        EstadoPunto estadoPunto = EstadoPunto.desdeTexto(estado);
        ServiciosPunto listaServicios = leerServicios(servicios, voluntarios);

        String tipoGuardado = codigo;
        if (CENTRO_ACOPIO.equals(codigo)) {
            listaServicios.agregar(ServiciosPunto.ACOPIO);
            tipoGuardado = "ALBERGUE";
        }
        if (!listaServicios.cabeEnColumna()) {
            throw new IllegalArgumentException("Los servicios superan los "
                    + ServiciosPunto.LIMITE_TEXTO + " caracteres permitidos.");
        }
        return PuntoAsistencia.crear(tipoGuardado, id, nombre, lat, lon, estadoPunto, listaServicios);
    }

    /** Convierte un texto en coordenada; acepta coma o punto decimal. */
    public static double leerCoordenada(String texto, String nombreCampo) {
        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException("La " + nombreCampo + " es obligatoria.");
        }
        try {
            return Double.parseDouble(texto.trim().replace(',', '.'));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("La " + nombreCampo
                    + " debe ser un número (ejemplo: 3.4516).");
        }
    }

    /** Convierte "AGUA, INTERNET" en servicios; ignora los vacíos y quita la marca ACOPIO. */
    public static ServiciosPunto leerServicios(String texto, int voluntarios) {
        List<String> lista = new ArrayList<>();
        if (texto != null) {
            for (String servicio : texto.split(",")) {
                if (!servicio.isBlank()) {
                    lista.add(servicio);
                }
            }
        }
        ServiciosPunto servicios = new ServiciosPunto(lista, voluntarios);
        servicios.quitar(ServiciosPunto.ACOPIO);
        return servicios;
    }

    /** Código del tipo tal como lo muestran los combos (separa CENTRO_ACOPIO de ALBERGUE). */
    public static String codigoTipo(PuntoAsistencia punto) {
        return punto instanceof CentroAcopio ? CENTRO_ACOPIO : punto.getTipoPunto();
    }

    /** Deja solo los puntos del tipo indicado; TODOS (o vacío) devuelve la lista completa. */
    public static List<PuntoAsistencia> filtrarPorTipo(List<PuntoAsistencia> puntos, String filtro) {
        List<PuntoAsistencia> resultado = new ArrayList<>();
        if (puntos == null) {
            return resultado;
        }
        boolean todos = filtro == null || filtro.isBlank() || TODOS.equalsIgnoreCase(filtro.trim());
        for (PuntoAsistencia punto : puntos) {
            if (todos || codigoTipo(punto).equalsIgnoreCase(filtro.trim())) {
                resultado.add(punto);
            }
        }
        return resultado;
    }

    /** Servicios para el campo de texto del formulario: AGUA,INTERNET (sin la marca ACOPIO). */
    public static String serviciosParaFormulario(PuntoAsistencia punto) {
        return String.join(",", serviciosSinMarca(punto));
    }

    /** Servicios para la tabla: AGUA, INTERNET (sin la marca ACOPIO). */
    public static String serviciosParaTabla(PuntoAsistencia punto) {
        return String.join(", ", serviciosSinMarca(punto));
    }

    private static List<String> serviciosSinMarca(PuntoAsistencia punto) {
        List<String> lista = new ArrayList<>();
        for (String servicio : punto.getServicios().getServicios()) {
            if (!ServiciosPunto.ACOPIO.equals(servicio)) {
                lista.add(servicio);
            }
        }
        return lista;
    }
}
