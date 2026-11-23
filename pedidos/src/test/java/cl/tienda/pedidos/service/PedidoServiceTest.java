package cl.tienda.pedidos.service;

import cl.tienda.pedidos.client.ProductoClient;
import cl.tienda.pedidos.dto.CrearPedidoRequest;
import cl.tienda.pedidos.dto.ProductoInfo;
import cl.tienda.pedidos.model.Pedido;
import cl.tienda.pedidos.repository.PedidoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

// Pruebas unitarias: el repositorio y el microservicio de productos son mocks.
class PedidoServiceTest {

    private PedidoRepository repository;
    private ProductoClient productoClient;
    private PedidoService service;

    @BeforeEach
    void preparar() {
        repository = mock(PedidoRepository.class);
        productoClient = mock(ProductoClient.class);
        service = new PedidoService(repository, productoClient);
        when(repository.save(any(Pedido.class))).thenAnswer(inv -> inv.getArgument(0));
        when(productoClient.obtener(2L)).thenReturn(new ProductoInfo(2L, "Cilindro GLP 11 kg", 12000, 10));
        when(productoClient.obtener(5L)).thenReturn(new ProductoInfo(5L, "Regulador", 8990, 3));
    }

    private CrearPedidoRequest compra(CrearPedidoRequest.Item... items) {
        return new CrearPedidoRequest(7L, "Calle 1", "TARJETA", List.of(items));
    }

    @Test
    void crearUsaElPrecioDeProductosYDescuentaStock() {
        Pedido pedido = service.crear(compra(new CrearPedidoRequest.Item(2L, 2), new CrearPedidoRequest.Item(5L, 1)));

        assertEquals("PAGADO", pedido.getEstado());
        assertEquals(12000 * 2 + 8990, pedido.getTotal());
        assertEquals("Cilindro GLP 11 kg", pedido.getItems().get(0).getNombre());
        assertEquals(12000, pedido.getItems().get(0).getPrecioUnitario());
        verify(productoClient).descontar(2L, 2);
        verify(productoClient).descontar(5L, 1);
    }

    @Test
    void crearJuntaProductosRepetidos() {
        Pedido pedido = service.crear(compra(new CrearPedidoRequest.Item(2L, 1), new CrearPedidoRequest.Item(2L, 2)));

        assertEquals(1, pedido.getItems().size());
        assertEquals(3, pedido.getItems().get(0).getCantidad());
        verify(productoClient).descontar(2L, 3);
    }

    @Test
    void siFaltaStockDevuelveLoQueYaSeDesconto() {
        doThrow(new ResponseStatusException(HttpStatus.CONFLICT, "Stock insuficiente para Regulador"))
                .when(productoClient).descontar(5L, 9);

        ResponseStatusException e = assertThrows(ResponseStatusException.class,
                () -> service.crear(compra(new CrearPedidoRequest.Item(2L, 2), new CrearPedidoRequest.Item(5L, 9))));

        assertEquals(HttpStatus.CONFLICT, e.getStatusCode());
        assertEquals("Stock insuficiente para Regulador", e.getReason());
        verify(productoClient).reponer(2L, 2);
        verify(repository, never()).save(any());
    }

    @Test
    void metodoDePagoInvalidoDevuelve400() {
        ResponseStatusException e = assertThrows(ResponseStatusException.class,
                () -> service.crear(new CrearPedidoRequest(7L, "Calle 1", "BITCOIN", List.of(new CrearPedidoRequest.Item(2L, 1)))));
        assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
        verifyNoInteractions(productoClient);
    }

    @Test
    void cancelarPedidoPagadoDevuelveElStock() {
        when(repository.findById(1L)).thenReturn(Optional.of(pedidoGuardado("PAGADO")));

        Pedido pedido = service.cancelar(1L);

        assertEquals("CANCELADO", pedido.getEstado());
        verify(productoClient).reponer(2L, 2);
    }

    @Test
    void noSePuedeCancelarUnPedidoEnviado() {
        when(repository.findById(1L)).thenReturn(Optional.of(pedidoGuardado("ENVIADO")));

        ResponseStatusException e = assertThrows(ResponseStatusException.class, () -> service.cancelar(1L));
        assertEquals(HttpStatus.CONFLICT, e.getStatusCode());
        verify(productoClient, never()).reponer(anyLong(), anyInt());
    }

    @Test
    void noSePuedeCambiarElEstadoDeUnPedidoCancelado() {
        when(repository.findById(1L)).thenReturn(Optional.of(pedidoGuardado("CANCELADO")));

        ResponseStatusException e = assertThrows(ResponseStatusException.class, () -> service.cambiarEstado(1L, "ENVIADO"));
        assertEquals(HttpStatus.CONFLICT, e.getStatusCode());
    }

    private Pedido pedidoGuardado(String estado) {
        Pedido p = new Pedido();
        p.setId(1L);
        p.setUsuarioId(7L);
        p.setEstado(estado);
        Pedido.Item item = new Pedido.Item();
        item.setProductoId(2L);
        item.setNombre("Cilindro GLP 11 kg");
        item.setPrecioUnitario(12000);
        item.setCantidad(2);
        p.getItems().add(item);
        return p;
    }
}
