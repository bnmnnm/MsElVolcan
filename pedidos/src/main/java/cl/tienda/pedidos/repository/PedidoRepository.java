package cl.tienda.pedidos.repository;

import cl.tienda.pedidos.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// Proporciona las operaciones de acceso a la base de datos.
public interface PedidoRepository
        extends JpaRepository<Pedido, Long> {

    // Pedidos de un usuario, los más nuevos primero.
    List<Pedido> findByUsuarioIdOrderByFechaCreacionDesc(Long usuarioId);

    // Todos los pedidos, los más nuevos primero (para el administrador).
    List<Pedido> findAllByOrderByFechaCreacionDesc();
}
