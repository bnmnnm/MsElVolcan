package cl.tienda.usuarios.config;

import cl.tienda.usuarios.model.Usuario;
import cl.tienda.usuarios.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

// Crea el administrador al arrancar (solo si la tabla está vacía).
// Queda con id = 1, que es el usuarioId que usa el pedido de ejemplo del microservicio de pedidos.
@Configuration
public class DatosIniciales {

    @Bean
    CommandLineRunner cargarUsuarios(UsuarioRepository repository, PasswordEncoder encoder) {
        return args -> {
            if (repository.count() > 0) {
                return;
            }
            Usuario admin = new Usuario();
            admin.setNombre("Administrador");
            admin.setCorreo("admin@demo.com");
            admin.setPassword(encoder.encode("1234"));
            admin.setRol("ADMIN");
            repository.save(admin);
        };
    }
}
