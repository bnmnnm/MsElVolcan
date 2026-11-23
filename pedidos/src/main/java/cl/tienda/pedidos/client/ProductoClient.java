package cl.tienda.pedidos.client;

import cl.tienda.pedidos.dto.ProductoInfo;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.function.Supplier;

// Llama al microservicio de productos (comunicación entre microservicios vía Eureka).
@Component
public class ProductoClient {

    private final RestClient restClient;

    public ProductoClient(@LoadBalanced RestClient.Builder builder) {
        this.restClient = builder.baseUrl("http://productos").build();
    }

    // GET /api/productos/{id}
    public ProductoInfo obtener(Long productoId) {
        return llamar(() -> restClient.get()
                .uri("/api/productos/{id}", productoId)
                .retrieve()
                .body(ProductoInfo.class));
    }

    // PUT /api/productos/{id}/descontar?cantidad=N  (409 si no hay stock)
    public void descontar(Long productoId, int cantidad) {
        llamar(() -> restClient.put()
                .uri("/api/productos/{id}/descontar?cantidad={c}", productoId, cantidad)
                .retrieve()
                .toBodilessEntity());
    }

    // PUT /api/productos/{id}/reponer?cantidad=N
    public void reponer(Long productoId, int cantidad) {
        llamar(() -> restClient.put()
                .uri("/api/productos/{id}/reponer?cantidad={c}", productoId, cantidad)
                .retrieve()
                .toBodilessEntity());
    }

    // Traduce los errores de productos a errores de pedidos, manteniendo el mensaje
    // (ej: 409 "Stock insuficiente para Cilindro GLP 11 kg").
    private <T> T llamar(Supplier<T> peticion) {
        try {
            return peticion.get();
        } catch (HttpClientErrorException e) {
            String mensaje = "Error en el microservicio de productos";
            try {
                Map<?, ?> cuerpo = e.getResponseBodyAs(Map.class);
                if (cuerpo != null && cuerpo.get("message") != null) {
                    mensaje = cuerpo.get("message").toString();
                }
            } catch (RuntimeException ignorado) {
                // el cuerpo no era JSON: se usa el mensaje por defecto
            }
            if (e.getStatusCode().value() == 404) {
                mensaje = "Uno de los productos ya no existe";
            }
            throw new ResponseStatusException(e.getStatusCode(), mensaje);
        } catch (RestClientException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "No se pudo conectar con el microservicio de productos");
        }
    }
}
