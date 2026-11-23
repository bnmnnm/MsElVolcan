package cl.tienda.pedidos.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

// Lo que React manda para comprar: SOLO qué productos y cuántos.
// El nombre y el precio NO vienen de React: pedidos los pide al microservicio de productos
// (así nadie puede cambiar el precio desde el navegador).
public record CrearPedidoRequest(
        @NotNull(message = "El usuario es obligatorio")
        Long usuarioId,

        @NotBlank(message = "La dirección de entrega es obligatoria")
        @Size(max = 255, message = "La dirección no puede tener más de 255 caracteres")
        String direccionEntrega,

        @NotBlank(message = "El método de pago es obligatorio")
        String metodoPago,

        @NotEmpty(message = "El pedido debe tener al menos un producto")
        List<@Valid Item> items
) {
    public record Item(
            @NotNull(message = "El producto es obligatorio")
            Long productoId,

            @NotNull(message = "La cantidad es obligatoria")
            @Min(value = 1, message = "La cantidad debe ser mayor a 0")
            Integer cantidad
    ) {
    }
}
