package cl.tienda.carrito.dto;

// Lo que el carrito necesita del microservicio de productos (los demás campos se ignoran).
public record ProductoInfo(Long id, String nombre, Integer precio, String imagen, Integer stock) {
}
