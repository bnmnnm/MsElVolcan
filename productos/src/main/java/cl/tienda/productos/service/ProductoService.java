package cl.tienda.productos.service;

import cl.tienda.productos.model.Producto;
import cl.tienda.productos.repository.ProductoRepository;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

// Indica que esta clase contiene la lógica del negocio.
@Service
public class ProductoService {

    private final ProductoRepository repository;

    public ProductoService(ProductoRepository repository) {
        this.repository = repository;
    }

    public List<Producto> listar() {
        return repository.findAll(Sort.by("id"));
    }

    public Producto obtener(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));
    }

    public Producto guardar(Producto producto) {
        // Asegura que se cree un registro nuevo.
        producto.setId(null);
        producto.setCodigo(producto.getCodigo().trim().toUpperCase());
        if (repository.existsByCodigoIgnoreCase(producto.getCodigo())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ya existe un producto con el código " + producto.getCodigo());
        }
        return repository.save(producto);
    }

    public Producto actualizar(Long id, Producto datos) {
        Producto producto = obtener(id);
        String codigo = datos.getCodigo().trim().toUpperCase();
        if (repository.existsByCodigoIgnoreCaseAndIdNot(codigo, id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ya existe un producto con el código " + codigo);
        }
        producto.setCodigo(codigo);
        producto.setNombre(datos.getNombre());
        producto.setCategoria(datos.getCategoria());
        producto.setStock(datos.getStock());
        producto.setPrecio(datos.getPrecio());
        producto.setDescripcion(datos.getDescripcion());
        producto.setImagen(datos.getImagen());
        return repository.save(producto);
    }

    public void eliminar(Long id) {
        repository.delete(obtener(id));
    }

    // Resta stock cuando se realiza una compra.
    @Transactional
    public Producto descontar(Long id, int cantidad) {
        validarCantidad(cantidad);
        Producto producto = obtener(id);
        if (producto.getStock() < cantidad) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Stock insuficiente para " + producto.getNombre());
        }
        producto.setStock(producto.getStock() - cantidad);
        return repository.save(producto);
    }

    // Devuelve stock (por ejemplo, si una compra no se pudo completar).
    @Transactional
    public Producto reponer(Long id, int cantidad) {
        validarCantidad(cantidad);
        Producto producto = obtener(id);
        producto.setStock(producto.getStock() + cantidad);
        return repository.save(producto);
    }

    private void validarCantidad(int cantidad) {
        if (cantidad <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La cantidad debe ser mayor a 0");
        }
    }
}
