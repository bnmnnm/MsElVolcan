package cl.tienda.productos.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import cl.tienda.productos.model.Producto;
import cl.tienda.productos.service.ProductoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Expone endpoints HTTP y devuelve los datos como JSON.
@Tag(name = "Productos", description = "Catálogo de productos y control de stock")
@RestController
// Ruta base para las peticiones de productos.
@RequestMapping("/api/productos")
// Permite peticiones desde React.
// Ajustar si Vite utiliza otro puerto.
@CrossOrigin(origins = "http://localhost:5173")
public class ProductoController {

    private final ProductoService service;

    public ProductoController(ProductoService service) {
        this.service = service;
    }

    // GET: devuelve todos los productos.
    @Operation(summary = "Listar productos", description = "Devuelve todos los productos del catálogo.")
    @GetMapping
    public List<Producto> listar() {
        return service.listar();
    }

    // GET: devuelve un producto por su ID.
    @Operation(summary = "Obtener producto", description = "Devuelve un producto por su id. 404 si no existe.")
    @GetMapping("/{id}")
    public Producto obtener(@PathVariable("id") Long id) {
        return service.obtener(id);
    }

    // POST: crea un producto nuevo (valida los datos con @Valid).
    @Operation(summary = "Crear producto", description = "Crea un producto. Valida los datos y que el código no esté repetido (409).")
    @PostMapping
    public ResponseEntity<Producto> guardar(@Valid @RequestBody Producto producto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(producto));
    }

    // PUT: modifica un producto (valida los datos con @Valid).
    @Operation(summary = "Modificar producto", description = "Modifica un producto existente. Valida los datos y que el código no lo use otro producto (409).")
    @PutMapping("/{id}")
    public Producto actualizar(@PathVariable("id") Long id, @Valid @RequestBody Producto producto) {
        return service.actualizar(id, producto);
    }

    // DELETE: elimina un producto.
    @Operation(summary = "Eliminar producto", description = "Elimina un producto por su id.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable("id") Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // PUT: descuenta stock (se usa al pagar).
    @Operation(summary = "Descontar stock", description = "Resta stock al comprar (lo usan pedidos). 409 si no hay stock suficiente.")
    @PutMapping("/{id}/descontar")
    public Producto descontar(@PathVariable("id") Long id, @RequestParam("cantidad") int cantidad) {
        return service.descontar(id, cantidad);
    }

    // PUT: devuelve stock.
    @Operation(summary = "Reponer stock", description = "Devuelve stock (por ejemplo, al cancelar un pedido).")
    @PutMapping("/{id}/reponer")
    public Producto reponer(@PathVariable("id") Long id, @RequestParam("cantidad") int cantidad) {
        return service.reponer(id, cantidad);
    }
}
