package cl.duoc.mensajeria.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

// Datos que aparecen arriba en Swagger (http://localhost:8082/swagger-ui.html).
@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI documentacion() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Mensajería - El Volcán")
                        .version("1.0")
                        .description("Mensajes del formulario de contacto y su estado (PENDIENTE / RESPONDIDO)."))
                // Swagger prueba el microservicio directo (el Gateway solo acepta peticiones de React).
                .servers(List.of(new Server().url("http://localhost:8082").description("Microservicio")));
    }
}
