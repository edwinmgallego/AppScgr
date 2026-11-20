# Revisión de la integración (Equipo 4 en AppScgr, rama develop)

## Qué base se usó
`AppScgr_1.zip` es idéntico a `AppScgr.zip` y trae activa la rama **Equipo6**, que NO tiene
`Geolocalizable`, `MarcadorMapa` ni `TipoMarcador`. Se tomó la rama **develop** (commit `c6e53c0`)
guardada dentro del mismo ZIP.

## Errores encontrados y corregidos
1. **pom.xml sin versiones de plugins.** Con Maven 3.8 (el que traen algunos NetBeans) se usa
   surefire 2.12.4, que no detecta JUnit 5 ("No tests to run"), y compiler 3.1, que ignora
   `maven.compiler.release`. Se fijaron `maven-compiler-plugin 3.11.0` y `maven-surefire-plugin 3.2.5`.
2. **Grafo aceptaba datos inválidos:** pesos `NaN`/infinito y nodos con coordenadas `NaN` o fuera de
   rango. Ahora lanza `IllegalArgumentException`; `LectorCSV` lo reporta con el número de línea.
3. **EscritorCSV lanzaba NullPointerException** al exportar un nodo con nombre `null`.
4. **Cierres acumulativos sin documentar:** aplicar incidentes dos veces no reabre vías. Se documentó
   en `RutasController` que, si un incidente se resuelve, hay que recargar y volver a aplicar la lista.
5. **.gitignore** del repo solo ignoraba `/target/`; se agregaron las carpetas de NetBeans y `/salida/`.
6. 9 pruebas nuevas para las correcciones 2 y 3.

## Verificado sin cambios
- `Grafo.getTodasLasAristas()` existe y está probado (incluye aristas bloqueadas).
- Firmas del código y de las pruebas consistentes (compilan juntas).
- `ReglasCierre`: `VIA_CERRADA` y `DERRUMBE` cierran el tramo más cercano en ambos sentidos; los otros
  tres tipos se ignoran; incidentes lejanos, inválidos o sobre vías ya cerradas se manejan sin errores.
- Sin conflictos de nombres con el resto del proyecto (paquetes `rutas.*` y `controllers.RutasController`
  no existían en develop).

## Pruebas
145 pruebas en verde (129 del Equipo 4 + `MarcadorMapaTest` y `TipoMarcadorTest` del Equipo 3).
Ejecutadas con un JUnit simulado; falta confirmarlas con `mvn test` real.

## Importante al ejecutar `mvn test`
Siete clases de prueba del repo base (`IncidenteDAOTest`, `FamiliarDAOTest`, `UsuarioDAOTest`,
`PerfilUsuarioDAOTest`, `ContactoPrincipalDAOTest`, `ActualizadorBDTest`, `VerificadorBDTest`) usan MySQL
y fallan si XAMPP está apagado. Para correr solo las del Equipo 4:

    mvn test -Dtest="rutas/**/*Test,RutasControllerTest"
