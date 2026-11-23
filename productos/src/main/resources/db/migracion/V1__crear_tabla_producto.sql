-- Crea la tabla
CREATE TABLE producto (
    id BIGINT NOT NULL AUTO_INCREMENT,          -- Identificador único generado automáticamente por MySQL.
    codigo VARCHAR(20) NOT NULL,                -- Código del producto (no se puede repetir).
    nombre VARCHAR(150) NOT NULL,               -- Nombre del producto.
    categoria VARCHAR(100) NOT NULL,            -- Categoría a la que pertenece.
    stock INT NOT NULL,                         -- Unidades disponibles.
    precio INT NOT NULL,                        -- Precio en pesos.
    descripcion VARCHAR(500),                   -- Descripción del producto.
    imagen VARCHAR(255),                        -- Ruta de la imagen.
    PRIMARY KEY (id),
    UNIQUE KEY uk_producto_codigo (codigo)
);
