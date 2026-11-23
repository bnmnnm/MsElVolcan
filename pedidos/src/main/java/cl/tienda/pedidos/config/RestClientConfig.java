package cl.tienda.pedidos.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    // Builder normal. Es @Primary porque el cliente de Eureka también usa un RestClient.Builder
    // para registrarse en http://localhost:8761; si usara el balanceado, el registro fallaría.
    @Bean
    @Primary
    RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }

    // Builder balanceado: permite usar "http://productos"; Eureka resuelve la dirección real.
    @Bean
    @LoadBalanced
    RestClient.Builder loadBalancedRestClientBuilder() {
        return RestClient.builder();
    }
}
