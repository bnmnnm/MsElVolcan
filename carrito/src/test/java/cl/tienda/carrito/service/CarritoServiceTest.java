package cl.tienda.carrito.service;

import cl.tienda.carrito.client.ProductoClient;
import cl.tienda.carrito.dto.AgregarItemRequest;
import cl.tienda.carrito.dto.CarritoRespuesta;
import cl.tienda.carrito.dto.ProductoInfo;
import cl.tienda.carrito.model.CarritoItem;
import cl.tienda.carrito.repository.CarritoItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// Pruebas unitarias: el repositorio y el microservicio de productos son mocks.
class CarritoServiceTest {

    private CarritoItemRepository repository;
    private ProductoClient productoClient;
    private CarritoService service;

    private final ProductoInfo cilindro = new ProductoInfo(2L, "Cilindro GLP 11 kg", 12000, "/img/11kg.jpg", 5);

    @BeforeEach
    void preparar() {
        repository = mock(CarritoItemRepository.class);
        productoClient = mock(ProductoClient.class);
        service = new CarritoService(repository, productoClient);
        when(productoClient.obtener(2L)).thenReturn(cilindro);
        when(repository.save(any(CarritoItem.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    void agregarProductoNuevoLoGuardaConLosDatosDeProductos() {
        when(repository.findByUsuarioIdAndProductoId(1L, 2L)).thenReturn(Optional.empty());

        service.agregar(1L, new AgregarItemRequest(2L, 2));

        verify(repository).save(argThat(i -> i.getUsuarioId() == 1L && i.getProductoId() == 2L
                && i.getCantidad() == 2 && i.getPrecio() == 12000 && i.getNombre().equals("Cilindro GLP 11 kg")));
    }

    @Test
    void agregarProductoQueYaEstabaSumaLaCantidad() {
        CarritoItem existente = item(3);
        when(repository.findByUsuarioIdAndProductoId(1L, 2L)).thenReturn(Optional.of(existente));

        service.agregar(1L, new AgregarItemRequest(2L, 1));

        assertEquals(4, existente.getCantidad());
    }

    @Test
    void noDejaAgregarMasQueElStock() {
        when(repository.findByUsuarioIdAndProductoId(1L, 2L)).thenReturn(Optional.of(item(4)));

        ResponseStatusException e = assertThrows(ResponseStatusException.class,
                () -> service.agregar(1L, new AgregarItemRequest(2L, 2)));
        assertEquals(HttpStatus.CONFLICT, e.getStatusCode());
        verify(repository, never()).save(any());
    }

    @Test
    void cambiarCantidadACeroQuitaElProducto() {
        CarritoItem existente = item(2);
        when(repository.findByUsuarioIdAndProductoId(1L, 2L)).thenReturn(Optional.of(existente));

        service.cambiarCantidad(1L, 2L, 0);

        verify(repository).delete(existente);
    }

    @Test
    void obtenerCalculaSubtotalesYTotal() {
        when(repository.findByUsuarioIdOrderByIdAsc(1L)).thenReturn(List.of(item(3)));

        CarritoRespuesta carrito = service.obtener(1L);

        assertEquals(36000, carrito.items().get(0).subtotal());
        assertEquals(36000, carrito.total());
    }

    @Test
    void quitarProductoQueNoEstaDevuelve404() {
        when(repository.findByUsuarioIdAndProductoId(1L, 9L)).thenReturn(Optional.empty());

        ResponseStatusException e = assertThrows(ResponseStatusException.class, () -> service.quitar(1L, 9L));
        assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
    }

    private CarritoItem item(int cantidad) {
        CarritoItem i = new CarritoItem();
        i.setId(10L);
        i.setUsuarioId(1L);
        i.setProductoId(2L);
        i.setNombre("Cilindro GLP 11 kg");
        i.setPrecio(12000);
        i.setImagen("/img/11kg.jpg");
        i.setCantidad(cantidad);
        return i;
    }
}
