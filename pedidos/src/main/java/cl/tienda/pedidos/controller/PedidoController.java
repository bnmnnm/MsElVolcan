package cl.tienda.pedidos.controller;

import cl.tienda.pedidos.model.Pedido;
import cl.tienda.pedidos.service.PedidoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Expone endpoints HTTP y devuelve los datos como JSON.
@RestController
// Ruta base para las peticiones de pedidos.
@RequestMapping("/api/pedidos")
// Permite peticiones desde React.
// Ajustar si Vite utiliza otro puerto.
@CrossOrigin(origins = "http://localhost:5173")
public class PedidoController {

    private final PedidoService service;

    public PedidoController(PedidoService service) {
        this.service = service;
    }

    // POST: registra un pedido nuevo.
    @PostMapping
    public ResponseEntity<Pedido> guardar(@Valid @RequestBody Pedido pedido) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(pedido));
    }

    // GET: devuelve los pedidos. Con ?usuarioId=1 devuelve solo los de ese usuario.
    @GetMapping
    public List<Pedido> listar(@RequestParam(name = "usuarioId", required = false) Long usuarioId) {
        return service.listar(usuarioId);
    }

    // PUT: cambia el estado del pedido. Ejemplo: /api/pedidos/1/estado?estado=ENVIADO
    @PutMapping("/{id}/estado")
    public Pedido cambiarEstado(@PathVariable("id") Long id, @RequestParam("estado") String estado) {
        return service.cambiarEstado(id, estado);
    }
}
