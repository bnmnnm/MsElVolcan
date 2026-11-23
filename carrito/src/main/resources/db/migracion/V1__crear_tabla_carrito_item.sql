-- Cada fila es un producto dentro del carrito de un usuario.
CREATE TABLE carrito_item (
    id BIGINT NOT NULL AUTO_INCREMENT,          -- Identificador único generado por MySQL.
    usuario_id BIGINT NOT NULL,                 -- Dueño del carrito (viene del microservicio de usuarios).
    producto_id BIGINT NOT NULL,                -- Producto (viene del microservicio de productos).
    nombre VARCHAR(150) NOT NULL,               -- Nombre del producto al agregarlo.
    precio INT NOT NULL,                        -- Precio unitario al agregarlo.
    imagen VARCHAR(255),                        -- Ruta de la imagen.
    cantidad INT NOT NULL,                      -- Unidades en el carrito.
    fecha_agregado DATETIME(6) NOT NULL,        -- Cuándo se agregó, asignada con @PrePersist.
    PRIMARY KEY (id),
    UNIQUE KEY uk_carrito_usuario_producto (usuario_id, producto_id)  -- Un producto aparece una sola vez por usuario.
);
