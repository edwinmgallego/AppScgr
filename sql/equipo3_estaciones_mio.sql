USE emergencias_db;

CREATE TABLE IF NOT EXISTS estaciones_mio(
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    latitud DOUBLE NOT NULL,
    longitud DOUBLE NOT NULL,
    operativa BOOLEAN NOT NULL DEFAULT TRUE
);

INSERT INTO estaciones_mio (nombre, latitud, longitud, operativa) VALUES
('Estación Universidades', 3.3688, -76.5290, TRUE),
('Estación Capri', 3.3958, -76.5449, TRUE),
('Estación Unidad Deportiva', 3.4126, -76.5477, TRUE),
('Estación Estadio', 3.4285, -76.5401, FALSE),
('Estación Torre de Cali', 3.4557, -76.5338, TRUE);