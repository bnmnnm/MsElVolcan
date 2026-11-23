package cl.tienda.usuarios.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import cl.tienda.usuarios.dto.LoginRequest;
import cl.tienda.usuarios.dto.RegistroRequest;
import cl.tienda.usuarios.dto.UsuarioRespuesta;
import cl.tienda.usuarios.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Usuarios", description = "Registro, login y administración de usuarios")
@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "http://localhost:5173")
public class UsuarioController {

    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    // POST /api/usuarios/registro  -> crea un cliente nuevo.
    @Operation(summary = "Registrar cliente", description = "Crea un usuario con rol CLIENTE. La contraseña se guarda con hash BCrypt. 409 si el correo ya existe.")
    @PostMapping("/registro")
    public ResponseEntity<UsuarioRespuesta> registrar(@Valid @RequestBody RegistroRequest datos) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrar(datos));
    }

    // POST /api/usuarios/login  -> devuelve id, nombre, correo y rol si los datos son correctos (401 si no).
    @Operation(summary = "Iniciar sesión", description = "Compara la contraseña con el hash guardado. Devuelve id, nombre, correo y rol (nunca la contraseña). 401 si es incorrecta.")
    @PostMapping("/login")
    public UsuarioRespuesta login(@Valid @RequestBody LoginRequest datos) {
        return service.login(datos);
    }

    // GET /api/usuarios  -> lista para el panel de administrador.
    @Operation(summary = "Listar usuarios", description = "Lista todos los usuarios (para el panel del administrador).")
    @GetMapping
    public List<UsuarioRespuesta> listar() {
        return service.listar();
    }

    @Operation(summary = "Obtener usuario", description = "Devuelve un usuario por su id. 404 si no existe.")
    @GetMapping("/{id}")
    public UsuarioRespuesta obtener(@PathVariable("id") Long id) {
        return service.obtener(id);
    }

    // PUT /api/usuarios/2/rol?rol=ADMIN
    @Operation(summary = "Cambiar rol", description = "Asigna ADMIN o CLIENTE a un usuario. 400 si el rol no es válido.")
    @PutMapping("/{id}/rol")
    public UsuarioRespuesta cambiarRol(@PathVariable("id") Long id, @RequestParam("rol") String rol) {
        return service.cambiarRol(id, rol);
    }

    @Operation(summary = "Eliminar usuario", description = "Elimina un usuario por su id.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable("id") Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
