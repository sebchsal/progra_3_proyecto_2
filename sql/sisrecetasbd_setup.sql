CREATE DATABASE IF NOT EXISTS sisrecetasbd;
USE sisrecetasbd;

CREATE TABLE receta (
    id INT AUTO_INCREMENT PRIMARY KEY,
    estado VARCHAR(50) NOT NULL,
    cantidad INT NOT NULL,
    detalle VARCHAR(255),

    -- Datos del medicamento
    codigo_medicamento VARCHAR(50),
    nombre_medicamento VARCHAR(100),
    tipo_presentacion VARCHAR(100),

    -- Datos del paciente
    identificacion_paciente VARCHAR(50),
    nombre_paciente VARCHAR(100),
    fecha_nacimiento_paciente DATE,
    telefono_paciente VARCHAR(50),

    -- Fechas
    fecha_confeccion DATE NOT NULL,
    fecha_entrega DATE
);


CREATE TABLE paciente (
    id INT AUTO_INCREMENT PRIMARY KEY,
    identificacion VARCHAR(50) UNIQUE NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    fecha_nacimiento DATE,
    telefono VARCHAR(50)
);

CREATE TABLE medicamento (
     id INT AUTO_INCREMENT PRIMARY KEY,
     codigo VARCHAR(50) UNIQUE NOT NULL,
     nombre VARCHAR(100) NOT NULL,
     tipo_presentacion VARCHAR(100)
);

CREATE TABLE medico (
    id INT AUTO_INCREMENT PRIMARY KEY,
    identificacion VARCHAR(50) NOT NULL UNIQUE,
    nombre VARCHAR(100) NOT NULL,
    clave VARCHAR(100) NOT NULL,
    especialidad VARCHAR(100)
)

CREATE TABLE farmaceuta (
    id INT AUTO_INCREMENT PRIMARY KEY,
    identificacion VARCHAR(50) NOT NULL UNIQUE,
    nombre VARCHAR(100) NOT NULL,
    clave VARCHAR(100) NOT NULL
);
