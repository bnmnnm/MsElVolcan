package cl.tienda.usuarios.service;

import cl.tienda.usuarios.dto.LoginRequest;
import cl.tienda.usuarios.dto.RegistroRequest;
import cl.tienda.usuarios.dto.UsuarioRespuesta;
import cl.tienda.usuarios.model.Usuario;
import cl.tienda.usuarios.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;

@Service
public class UsuarioService {

    private static final Set<String> ROLES_VALIDOS = Set.of("ADMIN", "CLIENTE");

    private final UsuarioRepository repository;
    private final PasswordEncoder encoder;

    public UsuarioService(UsuarioRepository repository, PasswordEncoder encoder) {
        this.repository = repository;
        this.encoder = encoder;
    }

    // Registra un cliente nuevo. El rol siempre es CLIENTE: un usuario no puede registrarse como ADMIN.
    public UsuarioRespuesta registrar(RegistroRequest datos) {
        String correo = datos.correo().trim().toLowerCase();
        if (repository.existsByCorreoIgnoreCase(correo)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El correo ya está registrado");
        }
        Usuario usuario = new Usuario();
        usuario.setNombre(datos.nombre().trim());
        usuario.setCorreo(correo);
        usuario.setPassword(encoder.encode(datos.password()));
        usuario.setRol("CLIENTE");
        return UsuarioRespuesta.desde(repository.save(usuario));
    }

    // Comprueba correo y contraseña. Mismo mensaje en ambos casos para no revelar qué correos existen.
    public UsuarioRespuesta login(LoginRequest datos) {
        Usuario usuario = repository.findByCorreoIgnoreCase(datos.correo().trim())
                .filter(u -> encoder.matches(datos.password(), u.getPassword()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Correo o contraseña incorrectos"));
        return UsuarioRespuesta.desde(usuario);
    }

    public List<UsuarioRespuesta> listar() {
        return repository.findAll().stream().map(UsuarioRespuesta::desde).toList();
    }

    public UsuarioRespuesta obtener(Long id) {
        return UsuarioRespuesta.desde(buscar(id));
    }

    // El administrador puede cambiar el rol de un usuario.
    public UsuarioRespuesta cambiarRol(Long id, String rol) {
        if (rol == null || !ROLES_VALIDOS.contains(rol)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rol no válido");
        }
        Usuario usuario = buscar(id);
        usuario.setRol(rol);
        return UsuarioRespuesta.desde(repository.save(usuario));
    }

    public void eliminar(Long id) {
        repository.delete(buscar(id));
    }

    private Usuario buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    }
}
