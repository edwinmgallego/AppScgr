# Equipo 4: Algoritmo de Rutas Alternas (RF-04) - Integración con el código base

## Dónde queda cada cosa (convención del repo base)

| Ruta | Contenido | Dueño |
|---|---|---|
| `src/main/java/rutas/modelo/` | `Nodo`, `Arista`, `Grafo` | Miembro 1 |
| `src/main/java/rutas/algoritmos/` | `Dijkstra`, `BFS`, `RutasAlternas`, `RutaResultado`, **`ReglasCierre`**, `ResumenCierres`, `Localizador` | Miembro 2 |
| `src/main/java/rutas/persistencia/` | `LectorCSV`, `EscritorCSV`, `ValidadorDatos`, `RepositorioGrafo` | Miembro 3 |
| `src/main/java/rutas/util/` | `Constantes`, `Geo` | Miembros 2 y 4 |
| `src/main/java/controllers/RutasController.java` | orquesta todo el flujo | Miembro 4 |
| `src/main/java/rutas/Main.java` | prueba por consola (solo depuración) | Miembro 4 |
| `src/test/java/**` | pruebas JUnit 5 | Miembro 4 |
| `data/*.csv` | red vial de ejemplo (8 nodos): sustituir por la real | Miembro 3 |

> Los archivos de `rutas/modelo`, `rutas/algoritmos` y `rutas/persistencia` son **implementaciones
> provisionales** (tienen un comentario al inicio). Cada miembro las reemplaza por la suya; las
> pruebas dicen si respeta el contrato.

## Cómo se conecta con el Equipo 3 (incidentes)

Los incidentes llegan **en memoria** como `List<models.Incidente>` (la que devuelve
`IncidenteDAO.obtenerTodosLosIncidentes()`). No usamos `MarcadorMapa`: pierde el `id` y agrupa los tipos.


`RutasController` no abre la base de datos ni usa Swing: así se prueba sin MySQL.

## Reglas de negocio (decisiones que el equipo debe confirmar)

1. **Qué incidentes cierran la vía.** Por defecto `VIA_CERRADA` y `DERRUMBE`. `FALLA_SERVICIO`,
   `SIN_SENAL` y `DANO_ESTRUCTURAL` se ignoran. Para cambiarlo: `new ReglasCierre(Set.of(...))`
   y pasarlo como tercer argumento de `RutasController`.
2. **Un incidente es un punto, no un tramo.** Se cierra el tramo **más cercano** si está a menos de
   `Constantes.RADIO_INCIDENTE_METROS` (50 m). Si no hay ninguno, el incidente queda en
   `resumen.getSinTramoCercano()` y no se cierra nada.
3. **Cierre en ambos sentidos** (`Grafo.cerrarArista` bloquea el tramo completo).
4. Un incidente con coordenadas inválidas o `null` no rompe el proceso: queda en `resumen.getInvalidos()`.
5. Se asume que los nodos usan **latitud/longitud WGS84 en grados decimales**, igual que los incidentes.
6. Los incidentes **no tienen estado** (abierto/resuelto) en el esquema actual: todos los registros de la
   tabla se consideran vigentes. Si se quiere "reabrir" una vía, hay que agregar esa columna (acordar con el Equipo 3).



`Grafo.getTodasLasAristas()`: devuelve todas las aristas, **también las bloqueadas**. Lo necesita
`ReglasCierre` para que un incidente sobre una vía ya cerrada no cierre por error una vía vecina.

## Ejecutar

```bash
mvn test        # el repo base ya tiene JUnit 5; algunas pruebas del repo (DAOs) necesitan MySQL encendido
java -cp target/classes rutas.Main      # prueba por consola con data/
```

Uso de `rutas.Main`: `[nodos.csv] [tramos.csv] [cierres.csv|-] [origen] [destino] [salida.csv]`.

## Pruebas del Equipo 4 (129)

`GrafoTest`, `DijkstraTest`, `BFSTest`, `RutasAlternasTest`, `LocalizadorTest`, `GeoTest`,
`ReglasCierreTest`, `LectorCSVTest`, `RutasControllerTest` y `FlujoCompletoTest`
(esta última usa `data/*.csv` y debe ejecutarse desde la raíz del proyecto, como hace Maven).

## Sugerencias para el repositorio (no incluidas para no pisar a otros equipos)

- Añadir al `.gitignore`: `/nbproject/private/`, `/build/`, `/dist/`, `nbactions.xml`, `/salida/`.
- No activar CI con `mvn test` hasta que las pruebas de los DAO (que necesitan MySQL) se puedan omitir en CI.
