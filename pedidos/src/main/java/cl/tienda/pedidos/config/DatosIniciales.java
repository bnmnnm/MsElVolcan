package cl.tienda.pedidos.config;

import cl.tienda.pedidos.model.Pedido;
import cl.tienda.pedidos.repository.PedidoRepository;
import cl.tienda.pedidos.service.PedidoService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Carga los datos iniciales al arrancar el microservicio (solo si la tabla está vacía).
@Configuration
public class DatosIniciales {

    @Bean
    CommandLineRunner cargarPedidos(PedidoRepository repository, PedidoService service) {
        return args -> {
            if (repository.count() > 0) {
                return;
            }

            // Pedido de ejemplo del usuario 1 (el administrador).
            Pedido pedido = new Pedido();
            pedido.setUsuarioId(1L);
            pedido.setDireccionEntrega("Av. Providencia 1234, Santiago");
            pedido.setMetodoPago("TARJETA");
            pedido.getItems().add(item(2L, "Cilindro GLP 11 kg", 12000, 1));
            pedido.getItems().add(item(5L, "Regulador domestico estándar", 8990, 1));

            // El servicio calcula el total y deja el pedido como PAGADO.
            service.guardar(pedido);
        };
    }

    private Pedido.Item item(Long productoId, String nombre, int precioUnitario, int cantidad) {
        Pedido.Item item = new Pedido.Item();
        item.setProductoId(productoId);
        item.setNombre(nombre);
        item.setPrecioUnitario(precioUnitario);
        item.setCantidad(cantidad);
        return item;
    }
}
