package cl.volcan.Productos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository 
public interface ProductoRepository extends JpaRepository<Producto, Long>{

    List<Producto> findByCategoria(String categoria);

}
