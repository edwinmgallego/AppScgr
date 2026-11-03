# 🚨 AppScgr - Sistema Integrado de Asistencia Urbana ante Emergencias

Proyecto de Programación Orientada a Objetos (POO) desarrollado por los estudiantes de Ingeniería en Java 21, utilizando el patrón arquitectónico MVC, interfaces gráficas con Java Swing y persistencia de datos en MySQL.

Este repositorio contiene el **código base** de la aplicación, el cual incluye la conexión a la base de datos, el renderizado de OpenStreetMap y el CRUD inicial de reportes de incidentes. A partir de aquí, los 9 equipos desarrollarán sus módulos específicos.

---

## 🛠️ 1. Requisitos Previos e Instalación del Entorno

Para ejecutar este proyecto, necesitas tener instalados:
1. **Java Development Kit (JDK) 21**.
2. **Apache NetBeans IDE** (o IntelliJ IDEA).
3. **XAMPP** (Para el servidor MySQL).
4. **Git** (Para el control de versiones).

### 💽 Configuración de XAMPP y MySQL
El proyecto requiere una base de datos local para funcionar.
1. Descarga e instala XAMPP.
2. Inicia los módulos de **Apache** y **MySQL** desde el panel de control.
3. Ingresa a `http://localhost/phpmyadmin` en tu navegador.
4. Crea una base de datos llamada `emergencias_db`.
5. Ejecuta el script SQL proporcionado por el profesor para generar las tablas e insertar el usuario de prueba (`ID: 1`).

> 📺 **Video Tutorial Recomendado:** [Cómo instalar XAMPP y usar phpMyAdmin (YouTube)](#) *(Nota al profe: Inserta aquí tu link)*

---

## 🏗️ 2. Arquitectura del Código Base (Patrón MVC)

El proyecto está rigurosamente estructurado bajo el patrón **Modelo-Vista-Controlador**. Es obligatorio que los equipos respeten la separación de responsabilidades en estos paquetes:

### 📦 `models` (Modelos)
Contiene las clases puras de Java (POJOs) que representan las entidades del sistema (Ej. `Incidente.java`). Solo deben contener atributos, constructores y métodos *Getters* y *Setters*. **No deben contener lógica de base de datos ni de interfaz.**

### 📦 `models.dao` (Data Access Objects)
Aquí reside la persistencia. Clases como `IncidenteDAO.java` contienen las consultas SQL (`INSERT`, `SELECT`, `UPDATE`, `DELETE`). Usan la clase `config.ConexionDB` (Patrón Singleton) para hablar con MySQL. 

### 📦 `views` (Vistas)
Interfaces gráficas construidas con Java Swing (Ej. `MapaView.java`). **Regla estricta:** Una vista jamás debe importar `ConexionDB` ni hacer consultas SQL. Solo captura datos del usuario (mediante `JTextField`, clics de ratón, etc.) y expone métodos *Getters* para que el Controlador lea esos datos.

### 📦 `controllers` (Controladores)
El cerebro que une la Vista y el DAO. El `MapaController.java` escucha los eventos de la vista (ej. `actionPerformed` al hacer clic en "Guardar"), extrae la información, construye un objeto del `Modelo`, y se lo envía al `DAO` para guardarlo en la base de datos. Finalmente, ordena a la vista que se actualice (ej. pintando los *Waypoints* en OpenStreetMap).

### 📦 `main`
Contiene `AppScgr.java`, la clase principal que inicializa la Vista, el Controlador, y arranca la aplicación.

---

## 🗺️ 3. ¿Cómo funciona la integración con OpenStreetMap?

El mapa interactivo se renderiza utilizando la librería `JXMapViewer2` (importada vía Maven en el `pom.xml`).
* **Visualización:** El mapa descarga "tiles" (imágenes cuadradas) desde los servidores de OpenStreetMap. Para evitar bloqueos por seguridad, en `MapaView.java` modificamos el protocolo a `https://` y forzamos el *User-Agent* en Java.
* **Interactividad:** En `MapaController.java`, un `MouseListener` detecta los clics en la pantalla, convierte ese píxel específico a coordenadas GPS (Latitud/Longitud) y llena automáticamente los campos del formulario.
* **Marcadores (Waypoints):** Al iniciar, el Controlador llama a `incidenteDAO.obtenerTodosLosIncidentes()`, itera sobre la lista y pinta marcadores rojos (`DefaultWaypoint`) por cada registro encontrado en MySQL.

---

## 🧪 4. Pruebas Unitarias (JUnit 5)

El código base incluye un paquete de pruebas en `Test Packages -> models.dao -> IncidenteDAOTest.java`. 
Esta clase utiliza anotaciones como `@Test` y sentencias como `assertTrue()` para validar que las sentencias SQL funcionan correctamente sin necesidad de abrir la interfaz gráfica. **Cada equipo debe desarrollar pruebas unitarias para sus propios DAOs.**
