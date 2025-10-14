

DROP DATABASE IF EXISTS libronova;
CREATE DATABASE libronova CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE libronova;


CREATE TABLE libros (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    isbn VARCHAR(20) NOT NULL UNIQUE,
    titulo VARCHAR(255) NOT NULL,
    autor VARCHAR(255) NOT NULL,
    editorial VARCHAR(255),
    anio INT NOT NULL,
    stock_disponible INT NOT NULL DEFAULT 0,
    stock_total INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_isbn (isbn),
    INDEX idx_titulo (titulo)
) ENGINE=InnoDB;

-- ==========================================
-- Table: socios
-- ==========================================
CREATE TABLE socios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    numero_socio VARCHAR(50) NOT NULL UNIQUE,
    estado ENUM('ACTIVO', 'INACTIVO', 'SUSPENDIDO') NOT NULL DEFAULT 'ACTIVO',
    fecha_registro DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_numero_socio (numero_socio),
    INDEX idx_email (email),
    INDEX idx_estado (estado)
) ENGINE=InnoDB;

-- ==========================================
-- Table: prestamos
-- ==========================================
CREATE TABLE prestamos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    libro_id BIGINT NOT NULL,
    socio_id BIGINT NOT NULL,
    fecha_prestamo DATE NOT NULL,
    fecha_devolucion_prevista DATE NOT NULL,
    fecha_devolucion_real DATE NULL,
    estado ENUM('ACTIVO', 'DEVUELTO', 'VENCIDO') NOT NULL DEFAULT 'ACTIVO',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (libro_id) REFERENCES libros(id) ON DELETE RESTRICT,
    FOREIGN KEY (socio_id) REFERENCES socios(id) ON DELETE RESTRICT,
    INDEX idx_libro_id (libro_id),
    INDEX idx_socio_id (socio_id),
    INDEX idx_estado (estado),
    INDEX idx_fecha_devolucion_prevista (fecha_devolucion_prevista)
) ENGINE=InnoDB;

