-- Script que crea la base de datos 
-- Crear la base de datos
CREATE DATABASE IF NOT EXISTS sistema_gestion;
USE sistema_gestion;

-- Tabla de empleados
CREATE TABLE IF NOT EXISTS empleados (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    documento VARCHAR(20) NOT NULL,
    telefono VARCHAR(20),
    email VARCHAR(100),
    direccion VARCHAR(200),
    cargo VARCHAR(100),
    fecha_contratacion DATE,
    salario DECIMAL(10,2)
);

-- Tabla de clientes
CREATE TABLE IF NOT EXISTS clientes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    documento VARCHAR(20),
    telefono VARCHAR(20),
    email VARCHAR(100),
    direccion VARCHAR(200),
    fecha_registro DATE
);

-- Tabla de proveedores
CREATE TABLE IF NOT EXISTS proveedores (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    contacto VARCHAR(100),
    telefono VARCHAR(20),
    email VARCHAR(100),
    direccion VARCHAR(200),
    productos VARCHAR(255)
);

-- Tabla de modelos de computadoras
CREATE TABLE IF NOT EXISTS modelos_computadoras (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    precio DECIMAL(10,2)
);

-- Tabla de componentes de computadoras
CREATE TABLE IF NOT EXISTS componentes_computadoras (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    precio DECIMAL(10,2),
    proveedor_id INT,
    FOREIGN KEY (proveedor_id) REFERENCES proveedores(id)
);

-- Tabla de inventario
CREATE TABLE IF NOT EXISTS inventario (
    id INT AUTO_INCREMENT PRIMARY KEY,
    modelo_id INT,
    componente_id INT,
    cantidad INT NOT NULL,
    ubicacion VARCHAR(100),
    fecha_actualizacion DATE,
    FOREIGN KEY (modelo_id) REFERENCES modelos_computadoras(id),
    FOREIGN KEY (componente_id) REFERENCES componentes_computadoras(id)
);

-- Tabla de ventas
CREATE TABLE IF NOT EXISTS ventas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    fecha DATE NOT NULL,
    cliente_id INT,
    empleado_id INT,
    total DECIMAL(10,2) NOT NULL,
    metodo_pago VARCHAR(50),
    FOREIGN KEY (cliente_id) REFERENCES clientes(id),
    FOREIGN KEY (empleado_id) REFERENCES empleados(id)
);

-- Tabla de detalles de venta
CREATE TABLE IF NOT EXISTS detalles_venta (
    id INT AUTO_INCREMENT PRIMARY KEY,
    venta_id INT,
    producto_id INT,
    cantidad INT NOT NULL,
    precio_unitario DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (venta_id) REFERENCES ventas(id),
    FOREIGN KEY (producto_id) REFERENCES inventario(id)
);

-- Tabla de entregas
CREATE TABLE IF NOT EXISTS entregas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    venta_id INT,
    fecha_entrega DATE,
    direccion_entrega VARCHAR(200) NOT NULL,
    estado VARCHAR(50) NOT NULL,
    empleado_id INT,
    notas TEXT,
    FOREIGN KEY (venta_id) REFERENCES ventas(id),
    FOREIGN KEY (empleado_id) REFERENCES empleados(id)
);
