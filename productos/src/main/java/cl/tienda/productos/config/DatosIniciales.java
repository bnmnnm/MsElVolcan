package cl.tienda.productos.config;

import cl.tienda.productos.model.Producto;
import cl.tienda.productos.repository.ProductoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

// Carga los datos iniciales al arrancar el microservicio (solo si la tabla está vacía).
// Son los mismos productos que antes estaban escritos en Productos.tsx.
@Configuration
public class DatosIniciales {

    @Bean
    CommandLineRunner cargarProductos(ProductoRepository repository) {
        return args -> {
            if (repository.count() > 0) {
                return;
            }

            repository.saveAll(List.of(
                crear("CL001", "Cilindro GLP 5 kg", "Cilindros de Gas", 8, 6500,
                        "Cilindro de gas licuado de petróleo 5 kg. Para uso residencial (cocina, calefacción pequeña).",
                        "/img/5kg.jpg"),
                crear("CL002", "Cilindro GLP 11 kg", "Cilindros de Gas", 12, 12000,
                        "Cilindro estándar doméstico. El más utilizado en hogares chilenos. Compatible con reguladores estándar.",
                        "/img/11kg.jpg"),
                crear("CL003", "Cilindro GLP 15 kg", "Cilindros de Gas", 6, 16000,
                        "Cilindro de mayor capacidad para hogares de alto consumo o locales pequeños.",
                        "/img/15kg.png"),
                crear("CL004", "Cilindro GLP 45 kg", "Cilindros de Gas", 4, 45000,
                        "Cilindro industrial. Uso comercial: restaurantes, talleres, calefacción de locales.",
                        "/img/45kg.png"),
                crear("RG001", "Regulador domestico estándar", "Reguladores", 15, 8990,
                        "Regulador de 1 etapa para cilindros 5, 11 y 15 kg. Presion de salida 28 mbar.",
                        "/img/regulador.jpg"),
                crear("RG002", "Regulador de alta presion", "Reguladores", 7, 18990,
                        "Regulador de cocinas industriales o equipo de mayor consumo. Presión regulable.",
                        "/img/rg002.jpg"),
                crear("RG003", "Regulador dual (2 salidas)", "Reguladores", 5, 14990,
                        "Permite conectar dos artefactos simultaneamente al mismo cilindro.",
                        "/img/rg003.jpg"),
                crear("MG001", "Manguera gas 1.5 m", "Manguera y Conexiones", 20, 3990,
                        "Manguera flexible homologada. Diámetro interior 9mm. Compatible con reguladores estandar.",
                        "/img/mg001.jpg"),
                crear("MG002", "Manguera gas 3 m", "Manguera y Conexiones", 10, 6990,
                        "Manguera larga para instalaciones donde el artefacto esta alejado del cilindro.",
                        "/img/mg002.jpg"),
                crear("MG003", "Abrazadera metálica", "Manguera y Conexiones", 50, 990,
                        "Abrazadera de acero para asegurar la conexión manguera-regulador y manguera-artefacto.",
                        "/img/mg003.jpg"),
                crear("MG004", "Kit conexión completo", "Manguera y Conexiones", 12, 12990,
                        "Todo lo necesario para instalar un cilindro nuevo (regulador + manguera 1.5m + abrazaderas).",
                        "/img/mg004.jpg"),
                crear("AC001", "Carro porta cilindros", "Accesorios", 8, 12990,
                        "Carro metálico con ruedas para transportar cilindros dentro del hogar con seguridad.",
                        "/img/ac001.jpg"),
                crear("AC002", "Tapa protectora válvula", "Accesorios", 25, 1490,
                        "Tapa de plástico ABS para proteger la válvula del cilindro durante el transporte.",
                        "/img/ac002.jpg"),
                crear("AC003", "Detector de gas", "Accesorios", 10, 19990,
                        "Sensor electroquímico. Alarma sonora y visual ante fuga de gas GLP o metano.",
                        "/img/ac003.jpg")
            ));
        };
    }

    private Producto crear(String codigo, String nombre, String categoria, int stock, int precio,
                           String descripcion, String imagen) {
        Producto producto = new Producto();
        producto.setCodigo(codigo);
        producto.setNombre(nombre);
        producto.setCategoria(categoria);
        producto.setStock(stock);
        producto.setPrecio(precio);
        producto.setDescripcion(descripcion);
        producto.setImagen(imagen);
        return producto;
    }
}
