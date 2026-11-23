package cl.tienda.carrito.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

// React solo manda qué producto y cuántos. Nombre, precio e imagen se piden al microservicio de productos.
public record AgregarItemRequest(
        @NotNull(message = "El producto es obligatorio")
        Long productoId,

        @NotNull(message = "La cantidad es obligatoria")
        @Min(value = 1, message = "La cantidad debe ser mayor a 0")
        Integer cantidad
) {
}
