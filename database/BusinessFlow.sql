DROP DATABASE IF EXISTS businessflow;

CREATE DATABASE businessflow
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE businessflow;

CREATE TABLE usuarios (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    apellidos VARCHAR(150) NULL,
    email VARCHAR(254) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    estado ENUM('PENDING', 'ACTIVE', 'BLOCKED', 'INACTIVE') NOT NULL DEFAULT 'PENDING',
    email_verified_at DATETIME(6) NULL,
    last_login_at DATETIME(6) NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    deleted_at DATETIME(6) NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_usuarios_email (email),
    INDEX indice_usuarios_estado (estado)
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_unicode_ci;

CREATE TABLE roles (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    codigo VARCHAR(50) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT NULL,
    estado ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE',
    is_system BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    deleted_at DATETIME(6) NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_roles_codigo (codigo),
    INDEX indice_roles_estado (estado)
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_unicode_ci;

CREATE TABLE usuarios_roles (
    usuario_id BIGINT UNSIGNED NOT NULL,
    rol_id BIGINT UNSIGNED NOT NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

    PRIMARY KEY (usuario_id, rol_id),

    INDEX indice_usuarios_roles_rol_id (rol_id),

    CONSTRAINT clave_foranea_usuarios_roles_usuario
        FOREIGN KEY (usuario_id)
        REFERENCES usuarios (id)
        ON DELETE CASCADE,

    CONSTRAINT clave_foranea_usuarios_roles_rol
        FOREIGN KEY (rol_id)
        REFERENCES roles (id)
        ON DELETE CASCADE
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_unicode_ci;

CREATE TABLE clientes (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    codigo VARCHAR(30) NOT NULL,
    tipo_cliente ENUM('EMPRESA', 'PARTICULAR') NOT NULL,
    nombre VARCHAR(200) NOT NULL,
    nombre_comercial VARCHAR(200) NULL COMMENT 'Nombre comercial cuando sea diferente de la razón social',
    identificacion_fiscal VARCHAR(50) NULL,
    email VARCHAR(254) NULL,
    telefono VARCHAR(20) NULL,
    direccion VARCHAR(255) NULL,
    codigo_postal VARCHAR(20) NULL,
    localidad VARCHAR(100) NULL,
    provincia VARCHAR(100) NULL,
    pais VARCHAR(100) NULL,
    estado ENUM('ACTIVO', 'INACTIVO') NOT NULL DEFAULT 'ACTIVO',
    observaciones TEXT NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    deleted_at DATETIME(6) NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_clientes_codigo (codigo),
    INDEX indice_clientes_identificacion_fiscal (identificacion_fiscal),
    INDEX indice_clientes_nombre (nombre),
    INDEX indice_clientes_email (email)
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_unicode_ci;

CREATE TABLE categorias_producto (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    codigo VARCHAR(30) NOT NULL,
    nombre VARCHAR(150) NOT NULL,
    descripcion TEXT NULL,
    estado ENUM('ACTIVO', 'INACTIVO') NOT NULL DEFAULT 'ACTIVO',
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    deleted_at DATETIME(6) NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_categorias_producto_codigo (codigo),
    INDEX indice_categorias_producto_nombre (nombre)
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_unicode_ci;

CREATE TABLE productos (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    codigo VARCHAR(30) NOT NULL,
    nombre VARCHAR(200) NOT NULL,
    descripcion TEXT NULL,
    categoria_producto_id BIGINT UNSIGNED NOT NULL,
    precio_venta DECIMAL(12,2) UNSIGNED NOT NULL,
    precio_coste DECIMAL(12,2) UNSIGNED NOT NULL,
    stock_minimo INT UNSIGNED NOT NULL DEFAULT 0,
    unidad_medida ENUM('UNIDAD', 'KG', 'LITRO', 'METRO', 'CAJA', 'PAQUETE') NOT NULL,
    imagen_url VARCHAR(500) NULL,
    estado ENUM('ACTIVO', 'INACTIVO') NOT NULL DEFAULT 'ACTIVO',
    observaciones TEXT NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    deleted_at DATETIME(6) NULL,

    PRIMARY KEY (id),
    UNIQUE KEY uq_productos_codigo (codigo),
    INDEX indice_productos_nombre (nombre),
    INDEX indice_productos_categoria_producto_id (categoria_producto_id),
    INDEX indice_productos_estado (estado),

    CONSTRAINT clave_foranea_productos_categoria_producto
        FOREIGN KEY (categoria_producto_id)
        REFERENCES categorias_producto (id)
        ON DELETE RESTRICT
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_unicode_ci;
