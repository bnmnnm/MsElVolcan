package cl.tienda.pedidos.dto;

// Lo que pedidos necesita del microservicio de productos (los demás campos se ignoran).
public record ProductoInfo(Long id, String nombre, Integer precio, Integer stock) {
}
