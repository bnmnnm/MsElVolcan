-- Crea la tabla de pedidos
CREATE TABLE pedido (
    id BIGINT NOT NULL AUTO_INCREMENT,          -- Identificador único generado automáticamente por MySQL.
    usuario_id BIGINT NOT NULL,                 -- Usuario que realizó la compra.
    total BIGINT NOT NULL,                      -- Total pagado.
    metodo_pago VARCHAR(20) NOT NULL,           -- TARJETA, TRANSFERENCIA o EFECTIVO.
    direccion_entrega VARCHAR(255) NOT NULL,    -- Dirección donde se entrega el pedido.
    estado VARCHAR(20) NOT NULL,                -- PAGADO, ENVIADO o ENTREGADO.
    fecha_creacion DATETIME(6) NOT NULL,        -- Fecha y hora de creación, asignadas desde el modelo con @PrePersist.
    PRIMARY KEY (id)
);

-- Crea la tabla con los productos de cada pedido
CREATE TABLE pedido_item (
    pedido_id BIGINT NOT NULL,                  -- Pedido al que pertenece.
    producto_id BIGINT NOT NULL,                -- Producto comprado.
    nombre VARCHAR(150) NOT NULL,               -- Nombre del producto al momento de la compra.
    precio_unitario INT NOT NULL,               -- Precio de una unidad al momento de la compra.
    cantidad INT NOT NULL,                      -- Unidades compradas.
    CONSTRAINT fk_pedido_item_pedido FOREIGN KEY (pedido_id) REFERENCES pedido (id)
);
