package cl.tienda.carrito.dto;

import cl.tienda.carrito.model.CarritoItem;

import java.util.List;

// Lo que se devuelve a React: los productos del carrito y el total.
public record CarritoRespuesta(Long usuarioId, List<Item> items, long total) {

    public record Item(Long productoId, String nombre, Integer precio, String imagen, Integer cantidad, long subtotal) {
    }

    public static CarritoRespuesta desde(Long usuarioId, List<CarritoItem> filas) {
        List<Item> items = filas.stream()
                .map(f -> new Item(f.getProductoId(), f.getNombre(), f.getPrecio(), f.getImagen(), f.getCantidad(),
                        (long) f.getPrecio() * f.getCantidad()))
                .toList();
        long total = items.stream().mapToLong(Item::subtotal).sum();
        return new CarritoRespuesta(usuarioId, items, total);
    }
}
