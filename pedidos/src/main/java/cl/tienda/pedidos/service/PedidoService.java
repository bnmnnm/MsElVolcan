package cl.tienda.pedidos.service;

import cl.tienda.pedidos.client.ProductoClient;
import cl.tienda.pedidos.dto.CrearPedidoRequest;
import cl.tienda.pedidos.dto.ProductoInfo;
import cl.tienda.pedidos.model.Pedido;
import cl.tienda.pedidos.repository.PedidoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

// Indica que esta clase contiene la lógica del negocio.
@Service
public class PedidoService {

    // Estados que el administrador puede asignar. CANCELADO solo se asigna con cancelar().
    private static final Set<String> ESTADOS_VALIDOS = Set.of("PAGADO", "ENVIADO", "ENTREGADO");
    private static final Set<String> METODOS_PAGO = Set.of("TARJETA", "TRANSFERENCIA", "EFECTIVO");

    private final PedidoRepository repository;
    private final ProductoClient productoClient;

    public PedidoService(PedidoRepository repository, ProductoClient productoClient) {
        this.repository = repository;
        this.productoClient = productoClient;
    }

    // Compra desde React:
    // 1) pide a productos el nombre y el PRECIO REAL de cada producto
    // 2) descuenta el stock en productos
    // 3) guarda el pedido (pago simulado: queda PAGADO)
    // Si algo falla, devuelve el stock que ya se había descontado.
    public Pedido crear(CrearPedidoRequest datos) {
        if (!METODOS_PAGO.contains(datos.metodoPago())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Método de pago no válido");
        }

        // Junta cantidades si el mismo producto viene repetido.
        Map<Long, Integer> cantidades = new LinkedHashMap<>();
        for (CrearPedidoRequest.Item item : datos.items()) {
            cantidades.merge(item.productoId(), item.cantidad(), Integer::sum);
        }

        Pedido pedido = new Pedido();
        pedido.setUsuarioId(datos.usuarioId());
        pedido.setDireccionEntrega(datos.direccionEntrega().trim());
        pedido.setMetodoPago(datos.metodoPago());

        Map<Long, Integer> descontados = new LinkedHashMap<>();
        try {
            for (Map.Entry<Long, Integer> entrada : cantidades.entrySet()) {
                ProductoInfo producto = productoClient.obtener(entrada.getKey());
                productoClient.descontar(producto.id(), entrada.getValue());
                descontados.put(producto.id(), entrada.getValue());
                pedido.getItems().add(item(producto, entrada.getValue()));
            }
            return guardar(pedido);
        } catch (RuntimeException e) {
            devolverStock(descontados);
            throw e;
        }
    }

    // Guarda un pedido con sus productos y calcula el total. Lo usa crear() y DatosIniciales.
    public Pedido guardar(Pedido pedido) {
        // Asegura que se cree un registro nuevo.
        pedido.setId(null);
        pedido.setEstado("PAGADO");

        long total = 0;
        for (Pedido.Item item : pedido.getItems()) {
            total += (long) item.getPrecioUnitario() * item.getCantidad();
        }
        pedido.setTotal(total);

        return repository.save(pedido);
    }

    // Sin usuarioId devuelve todos los pedidos; con usuarioId, solo los de ese usuario.
    public List<Pedido> listar(Long usuarioId) {
        if (usuarioId == null) {
            return repository.findAllByOrderByFechaCreacionDesc();
        }
        return repository.findByUsuarioIdOrderByFechaCreacionDesc(usuarioId);
    }

    // PUT: cambia el estado del pedido (por ejemplo, de PAGADO a ENVIADO).
    public Pedido cambiarEstado(Long id, String estado) {
        if (!ESTADOS_VALIDOS.contains(estado)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Estado no válido");
        }
        Pedido pedido = buscar(id);
        if ("CANCELADO".equals(pedido.getEstado())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "No se puede cambiar el estado de un pedido cancelado");
        }
        pedido.setEstado(estado);
        return repository.save(pedido);
    }

    // Cancela un pedido que todavía no se envía y devuelve el stock de sus productos.
    public Pedido cancelar(Long id) {
        Pedido pedido = buscar(id);
        if (!"PAGADO".equals(pedido.getEstado())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Solo se pueden cancelar pedidos en estado PAGADO (este está " + pedido.getEstado() + ")");
        }

        // Devuelve el stock. Si falla a la mitad, se vuelve a descontar lo devuelto
        // para que el stock quede igual que antes y el pedido siga PAGADO.
        List<Pedido.Item> repuestos = new ArrayList<>();
        try {
            for (Pedido.Item item : pedido.getItems()) {
                productoClient.reponer(item.getProductoId(), item.getCantidad());
                repuestos.add(item);
            }
        } catch (RuntimeException e) {
            for (Pedido.Item item : repuestos) {
                try {
                    productoClient.descontar(item.getProductoId(), item.getCantidad());
                } catch (RuntimeException ignorado) {
                    // se intenta lo mejor posible
                }
            }
            throw e;
        }

        pedido.setEstado("CANCELADO");
        return repository.save(pedido);
    }

    private Pedido buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido no encontrado"));
    }

    private void devolverStock(Map<Long, Integer> descontados) {
        descontados.forEach((productoId, cantidad) -> {
            try {
                productoClient.reponer(productoId, cantidad);
            } catch (RuntimeException ignorado) {
                // se intenta lo mejor posible
            }
        });
    }

    private Pedido.Item item(ProductoInfo producto, int cantidad) {
        Pedido.Item item = new Pedido.Item();
        item.setProductoId(producto.id());
        item.setNombre(producto.nombre());
        item.setPrecioUnitario(producto.precio());
        item.setCantidad(cantidad);
        return item;
    }
}
