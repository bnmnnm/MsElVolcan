-- Crea la tabla de usuarios
CREATE TABLE usuario (
    id BIGINT NOT NULL AUTO_INCREMENT,          -- Identificador único generado por MySQL.
    nombre VARCHAR(100) NOT NULL,               -- Nombre visible del usuario.
    correo VARCHAR(150) NOT NULL,               -- Correo (no se puede repetir).
    password VARCHAR(100) NOT NULL,             -- Contraseña cifrada con BCrypt (nunca en texto plano).
    rol VARCHAR(20) NOT NULL,                   -- ADMIN o CLIENTE.
    fecha_creacion DATETIME(6) NOT NULL,        -- Fecha de registro, asignada con @PrePersist.
    PRIMARY KEY (id),
    UNIQUE KEY uk_usuario_correo (correo)
);
