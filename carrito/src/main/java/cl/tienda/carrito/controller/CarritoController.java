package cl.tienda.carrito.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import cl.tienda.carrito.dto.AgregarItemRequest;
import cl.tienda.carrito.dto.CarritoRespuesta;
import cl.tienda.carrito.service.CarritoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Carrito", description = "Carrito de compras guardado en MySQL por usuario")
@RestController
@RequestMapping("/api/carrito")
@CrossOrigin(origins = "http://localhost:5173")
public class CarritoController {

    private final CarritoService service;

    public CarritoController(CarritoService service) {
        this.service = service;
    }

    // GET /api/carrito/1  -> carrito del usuario 1 (items + total)
    @Operation(summary = "Ver carrito", description = "Devuelve los productos del carrito del usuario, con subtotales y total.")
    @GetMapping("/{usuarioId}")
    public CarritoRespuesta obtener(@PathVariable("usuarioId") Long usuarioId) {
        return service.obtener(usuarioId);
    }

    // POST /api/carrito/1/items  body: {"productoId": 3, "cantidad": 1}
    @Operation(summary = "Agregar producto", description = "Agrega un producto (si ya estaba, suma la cantidad). Consulta precio y stock al microservicio de productos. 409 si supera el stock.")
    @PostMapping("/{usuarioId}/items")
    public CarritoRespuesta agregar(@PathVariable("usuarioId") Long usuarioId,
                                    @Valid @RequestBody AgregarItemRequest datos) {
        return service.agregar(usuarioId, datos);
    }

    // PUT /api/carrito/1/items/3?cantidad=2
    @Operation(summary = "Cambiar cantidad", description = "Cambia la cantidad de un producto del carrito. Con cantidad 0 lo quita. 409 si supera el stock.")
    @PutMapping("/{usuarioId}/items/{productoId}")
    public CarritoRespuesta cambiarCantidad(@PathVariable("usuarioId") Long usuarioId,
                                            @PathVariable("productoId") Long productoId,
                                            @RequestParam("cantidad") int cantidad) {
        return service.cambiarCantidad(usuarioId, productoId, cantidad);
    }

    // DELETE /api/carrito/1/items/3  -> quita un producto
    @Operation(summary = "Quitar producto", description = "Quita un producto del carrito.")
    @DeleteMapping("/{usuarioId}/items/{productoId}")
    public CarritoRespuesta quitar(@PathVariable("usuarioId") Long usuarioId,
                                   @PathVariable("productoId") Long productoId) {
        return service.quitar(usuarioId, productoId);
    }

    // DELETE /api/carrito/1  -> vacía el carrito (se usa después de pagar)
    @Operation(summary = "Vaciar carrito", description = "Elimina todos los productos del carrito (se usa después de pagar).")
    @DeleteMapping("/{usuarioId}")
    public ResponseEntity<Void> vaciar(@PathVariable("usuarioId") Long usuarioId) {
        service.vaciar(usuarioId);
        return ResponseEntity.noContent().build();
    }
}
