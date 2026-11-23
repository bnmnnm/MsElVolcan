package cl.tienda.carrito.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "carrito_item")
public class CarritoItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(name = "producto_id", nullable = false)
    private Long productoId;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(nullable = false)
    private Integer precio;

    @Column(length = 255)
    private String imagen;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(name = "fecha_agregado", nullable = false, updatable = false)
    private LocalDateTime fechaAgregado;

    @PrePersist
    protected void asignarFecha() {
        this.fechaAgregado = LocalDateTime.now();
    }
}
