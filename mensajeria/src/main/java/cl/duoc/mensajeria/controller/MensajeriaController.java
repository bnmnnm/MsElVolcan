package cl.duoc.mensajeria.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.mensajeria.model.Mensajeria;
import cl.duoc.mensajeria.service.MensajeriaService;
import jakarta.validation.Valid;

@Tag(name = "Mensajería", description = "Mensajes del formulario de contacto")
@RestController
@RequestMapping("/api/contactos")
// Igual que los demás microservicios: solo se permite React.
@CrossOrigin(origins = "http://localhost:5173")
public class MensajeriaController {

    private final MensajeriaService service;

    public MensajeriaController(MensajeriaService service) {
        this.service = service;
    }

    // POST: guarda un mensaje de contacto (valida nombre, correo y mensaje).
    @Operation(summary = "Enviar mensaje", description = "Guarda un mensaje del formulario de contacto con estado PENDIENTE. Valida nombre, correo y mensaje.")
    @PostMapping
    public ResponseEntity<Mensajeria> guardar(
            @Valid @RequestBody Mensajeria contacto) {

        Mensajeria guardado = service.guardar(contacto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(guardado);
    }

    @Operation(summary = "Listar mensajes", description = "Devuelve todos los mensajes recibidos (para el administrador).")
    @GetMapping
    public List<Mensajeria> listar() {
        return service.listar();
    }

    // PUT: cambia el estado. Ejemplo: /api/contactos/1/estado?estado=RESPONDIDO
    @Operation(summary = "Cambiar estado", description = "Marca un mensaje como PENDIENTE o RESPONDIDO.")
    @PutMapping("/{id}/estado")
    public Mensajeria cambiarEstado(@PathVariable("id") Long id, @RequestParam("estado") String estado) {
        return service.cambiarEstado(id, estado);
    }
}
