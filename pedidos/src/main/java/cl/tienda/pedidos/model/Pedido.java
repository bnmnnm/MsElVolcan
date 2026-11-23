package cl.tienda.pedidos.model;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "pedido")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Id del usuario que compró (viene del microservicio de usuarios).
    @NotNull
    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    // Lo calcula el servicio a partir de los productos.
    @Column(nullable = false)
    private Long total;

    // TARJETA, TRANSFERENCIA o EFECTIVO
    @NotBlank
    @Column(name = "metodo_pago", nullable = false, length = 20)
    private String metodoPago;

    @NotBlank
    @Column(name = "direccion_entrega", nullable = false, length = 255)
    private String direccionEntrega;

    // PAGADO, ENVIADO o ENTREGADO
    @Column(nullable = false, length = 20)
    private String estado;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    // Productos comprados. Se guardan en la tabla pedido_item junto con el pedido.
    @NotEmpty
    @Valid
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "pedido_item", joinColumns = @JoinColumn(name = "pedido_id"))
    private List<Item> items = new ArrayList<>();

    // Asigna automáticamente la fecha y hora antes de insertar el pedido.
    @PrePersist
    protected void asignarFechaCreacion() {
        this.fechaCreacion = LocalDateTime.now();
    }

    // Cada producto del pedido (no es una tabla aparte con su propio modelo).
    @Data
    @Embeddable
    public static class Item {

        @NotNull
        @Column(name = "producto_id", nullable = false)
        private Long productoId;

        @NotBlank
        @Column(nullable = false, length = 150)
        private String nombre;

        @NotNull
        @Min(0)
        @Column(name = "precio_unitario", nullable = false)
        private Integer precioUnitario;

        @NotNull
        @Min(1)
        @Column(nullable = false)
        private Integer cantidad;
    }
}
