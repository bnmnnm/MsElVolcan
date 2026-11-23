package cl.tienda.usuarios.controller;

import cl.tienda.usuarios.dto.LoginRequest;
import cl.tienda.usuarios.dto.RegistroRequest;
import cl.tienda.usuarios.dto.UsuarioRespuesta;
import cl.tienda.usuarios.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "http://localhost:5173")
public class UsuarioController {

    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    // POST /api/usuarios/registro  -> crea un cliente nuevo.
    @PostMapping("/registro")
    public ResponseEntity<UsuarioRespuesta> registrar(@Valid @RequestBody RegistroRequest datos) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrar(datos));
    }

    // POST /api/usuarios/login  -> devuelve id, nombre, correo y rol si los datos son correctos (401 si no).
    @PostMapping("/login")
    public UsuarioRespuesta login(@Valid @RequestBody LoginRequest datos) {
        return service.login(datos);
    }

    // GET /api/usuarios  -> lista para el panel de administrador.
    @GetMapping
    public List<UsuarioRespuesta> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public UsuarioRespuesta obtener(@PathVariable("id") Long id) {
        return service.obtener(id);
    }

    // PUT /api/usuarios/2/rol?rol=ADMIN
    @PutMapping("/{id}/rol")
    public UsuarioRespuesta cambiarRol(@PathVariable("id") Long id, @RequestParam("rol") String rol) {
        return service.cambiarRol(id, rol);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable("id") Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
