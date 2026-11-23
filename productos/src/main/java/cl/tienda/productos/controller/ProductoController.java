package cl.tienda.productos.controller;

import cl.tienda.productos.model.Producto;
import cl.tienda.productos.service.ProductoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Expone endpoints HTTP y devuelve los datos como JSON.
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
    @GetMapping
    public List<Producto> listar() {
        return service.listar();
    }

    // GET: devuelve un producto por su ID.
    @GetMapping("/{id}")
    public Producto obtener(@PathVariable("id") Long id) {
        return service.obtener(id);
    }

    // POST: crea un producto nuevo.
    @PostMapping
    public ResponseEntity<Producto> guardar(@RequestBody Producto producto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(producto));
    }

    // PUT: modifica un producto.
    @PutMapping("/{id}")
    public Producto actualizar(@PathVariable("id") Long id, @RequestBody Producto producto) {
        return service.actualizar(id, producto);
    }

    // DELETE: elimina un producto.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable("id") Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // PUT: descuenta stock (se usa al pagar).
    @PutMapping("/{id}/descontar")
    public Producto descontar(@PathVariable("id") Long id, @RequestParam("cantidad") int cantidad) {
        return service.descontar(id, cantidad);
    }

    // PUT: devuelve stock.
    @PutMapping("/{id}/reponer")
    public Producto reponer(@PathVariable("id") Long id, @RequestParam("cantidad") int cantidad) {
        return service.reponer(id, cantidad);
    }
}
