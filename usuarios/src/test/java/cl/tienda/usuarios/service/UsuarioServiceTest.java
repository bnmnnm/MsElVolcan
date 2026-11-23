package cl.tienda.usuarios.service;

import cl.tienda.usuarios.dto.LoginRequest;
import cl.tienda.usuarios.dto.RegistroRequest;
import cl.tienda.usuarios.dto.UsuarioRespuesta;
import cl.tienda.usuarios.model.Usuario;
import cl.tienda.usuarios.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// Pruebas unitarias del servicio (sin base de datos: el repositorio es un mock).
class UsuarioServiceTest {

    private UsuarioRepository repository;
    private final PasswordEncoder encoder = new BCryptPasswordEncoder();
    private UsuarioService service;

    @BeforeEach
    void preparar() {
        repository = mock(UsuarioRepository.class);
        service = new UsuarioService(repository, encoder);
        when(repository.save(any(Usuario.class))).thenAnswer(inv -> {
            Usuario u = inv.getArgument(0);
            u.setId(7L);
            return u;
        });
    }

    @Test
    void registrarCifraLaContraseñaYAsignaRolCliente() {
        UsuarioRespuesta r = service.registrar(new RegistroRequest("Ana", " Ana@Correo.cl ", "secreta"));

        assertEquals(7L, r.id());
        assertEquals("ana@correo.cl", r.correo());
        assertEquals("CLIENTE", r.rol());
        verify(repository).save(argThat(u -> !u.getPassword().equals("secreta")
                && encoder.matches("secreta", u.getPassword())));
    }

    @Test
    void registrarConCorreoRepetidoDevuelve409() {
        when(repository.existsByCorreoIgnoreCase("ana@correo.cl")).thenReturn(true);

        ResponseStatusException e = assertThrows(ResponseStatusException.class,
                () -> service.registrar(new RegistroRequest("Ana", "ana@correo.cl", "secreta")));
        assertEquals(HttpStatus.CONFLICT, e.getStatusCode());
    }

    @Test
    void loginCorrectoDevuelveElUsuarioSinPassword() {
        when(repository.findByCorreoIgnoreCase("admin@demo.com")).thenReturn(Optional.of(usuario("1234")));

        UsuarioRespuesta r = service.login(new LoginRequest("admin@demo.com", "1234"));

        assertEquals("ADMIN", r.rol());
    }

    @Test
    void loginConPasswordIncorrectaDevuelve401() {
        when(repository.findByCorreoIgnoreCase("admin@demo.com")).thenReturn(Optional.of(usuario("1234")));

        ResponseStatusException e = assertThrows(ResponseStatusException.class,
                () -> service.login(new LoginRequest("admin@demo.com", "mala")));
        assertEquals(HttpStatus.UNAUTHORIZED, e.getStatusCode());
    }

    @Test
    void cambiarRolInvalidoDevuelve400() {
        ResponseStatusException e = assertThrows(ResponseStatusException.class,
                () -> service.cambiarRol(1L, "SUPERUSUARIO"));
        assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
    }

    private Usuario usuario(String password) {
        Usuario u = new Usuario();
        u.setId(1L);
        u.setNombre("Administrador");
        u.setCorreo("admin@demo.com");
        u.setPassword(encoder.encode(password));
        u.setRol("ADMIN");
        return u;
    }
}
