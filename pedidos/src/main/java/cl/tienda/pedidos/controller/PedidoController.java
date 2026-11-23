package cl.tienda.pedidos.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import cl.tienda.pedidos.dto.CrearPedidoRequest;
import cl.tienda.pedidos.model.Pedido;
import cl.tienda.pedidos.service.PedidoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Expone endpoints HTTP y devuelve los datos como JSON.
@Tag(name = "Pedidos", description = "Compras de los clientes")
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
    // Body: {"usuarioId": 1, "direccionEntrega": "...", "metodoPago": "TARJETA",
    //        "items": [{"productoId": 2, "cantidad": 1}]}
    // El precio y el nombre los obtiene pedidos desde el microservicio de productos.
    @Operation(summary = "Crear pedido", description = "Recibe solo productos y cantidades. Pide el precio real a productos, descuenta el stock y guarda el pedido como PAGADO. Si falta stock devuelve lo descontado (409).")
    @PostMapping
    public ResponseEntity<Pedido> crear(@Valid @RequestBody CrearPedidoRequest datos) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(datos));
    }

    // GET: devuelve los pedidos. Con ?usuarioId=1 devuelve solo los de ese usuario.
    @Operation(summary = "Listar pedidos", description = "Sin parámetros devuelve todos los pedidos (admin). Con ?usuarioId= devuelve solo los de ese cliente.")
    @GetMapping
    public List<Pedido> listar(@RequestParam(name = "usuarioId", required = false) Long usuarioId) {
        return service.listar(usuarioId);
    }

    // PUT: cambia el estado del pedido. Ejemplo: /api/pedidos/1/estado?estado=ENVIADO
    @Operation(summary = "Cambiar estado", description = "El administrador cambia el estado: PAGADO, ENVIADO o ENTREGADO. No se puede cambiar un pedido cancelado.")
    @PutMapping("/{id}/estado")
    public Pedido cambiarEstado(@PathVariable("id") Long id, @RequestParam("estado") String estado) {
        return service.cambiarEstado(id, estado);
    }

    // PUT: cancela un pedido en estado PAGADO y devuelve el stock. Ejemplo: /api/pedidos/1/cancelar
    @Operation(summary = "Cancelar pedido", description = "Cancela un pedido en estado PAGADO y devuelve el stock de sus productos.")
    @PutMapping("/{id}/cancelar")
    public Pedido cancelar(@PathVariable("id") Long id) {
        return service.cancelar(id);
    }
}
