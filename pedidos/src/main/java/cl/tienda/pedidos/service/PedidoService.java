package cl.tienda.pedidos.service;

import cl.tienda.pedidos.model.Pedido;
import cl.tienda.pedidos.repository.PedidoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;

// Indica que esta clase contiene la lógica del negocio.
@Service
public class PedidoService {

    private static final Set<String> ESTADOS_VALIDOS = Set.of("PAGADO", "ENVIADO", "ENTREGADO");

    private final PedidoRepository repository;

    public PedidoService(PedidoRepository repository) {
        this.repository = repository;
    }

    // Guarda un pedido con sus productos. El pago es simulado, por eso queda PAGADO.
    public Pedido guardar(Pedido pedido) {
        // Asegura que se cree un registro nuevo.
        pedido.setId(null);
        pedido.setEstado("PAGADO");

        // El total se calcula aquí, no se confía en el que envíe React.
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
        Pedido pedido = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido no encontrado"));
        pedido.setEstado(estado);
        return repository.save(pedido);
    }
}
