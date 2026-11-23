package cl.tienda.productos.repository;

import cl.tienda.productos.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

// Proporciona las operaciones de acceso a la base de datos.
public interface ProductoRepository
        extends JpaRepository<Producto, Long> {
}
