package cl.tienda.carrito.controller;

import cl.tienda.carrito.dto.AgregarItemRequest;
import cl.tienda.carrito.dto.CarritoRespuesta;
import cl.tienda.carrito.service.CarritoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carrito")
@CrossOrigin(origins = "http://localhost:5173")
public class CarritoController {

    private final CarritoService service;

    public CarritoController(CarritoService service) {
        this.service = service;
    }

    // GET /api/carrito/1  -> carrito del usuario 1 (items + total)
    @GetMapping("/{usuarioId}")
    public CarritoRespuesta obtener(@PathVariable("usuarioId") Long usuarioId) {
        return service.obtener(usuarioId);
    }

    // POST /api/carrito/1/items  body: {"productoId": 3, "cantidad": 1}
    @PostMapping("/{usuarioId}/items")
    public CarritoRespuesta agregar(@PathVariable("usuarioId") Long usuarioId,
                                    @Valid @RequestBody AgregarItemRequest datos) {
        return service.agregar(usuarioId, datos);
    }

    // PUT /api/carrito/1/items/3?cantidad=2
    @PutMapping("/{usuarioId}/items/{productoId}")
    public CarritoRespuesta cambiarCantidad(@PathVariable("usuarioId") Long usuarioId,
                                            @PathVariable("productoId") Long productoId,
                                            @RequestParam("cantidad") int cantidad) {
        return service.cambiarCantidad(usuarioId, productoId, cantidad);
    }

    // DELETE /api/carrito/1/items/3  -> quita un producto
    @DeleteMapping("/{usuarioId}/items/{productoId}")
    public CarritoRespuesta quitar(@PathVariable("usuarioId") Long usuarioId,
                                   @PathVariable("productoId") Long productoId) {
        return service.quitar(usuarioId, productoId);
    }

    // DELETE /api/carrito/1  -> vacía el carrito (se usa después de pagar)
    @DeleteMapping("/{usuarioId}")
    public ResponseEntity<Void> vaciar(@PathVariable("usuarioId") Long usuarioId) {
        service.vaciar(usuarioId);
        return ResponseEntity.noContent().build();
    }
}
