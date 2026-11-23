package cl.tienda.productos.service;

import cl.tienda.productos.model.Producto;
import cl.tienda.productos.repository.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// Pruebas unitarias del servicio (el repositorio es un mock: no se necesita MySQL).
class ProductoServiceTest {

    private ProductoRepository repository;
    private ProductoService service;

    @BeforeEach
    void preparar() {
        repository = mock(ProductoRepository.class);
        service = new ProductoService(repository);
        when(repository.save(any(Producto.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    void guardarNormalizaElCodigo() {
        Producto p = producto(null, " ac010 ", 5);

        Producto guardado = service.guardar(p);

        assertEquals("AC010", guardado.getCodigo());
        assertNull(guardado.getId());
    }

    @Test
    void guardarConCodigoRepetidoDevuelve409() {
        when(repository.existsByCodigoIgnoreCase("CL001")).thenReturn(true);

        ResponseStatusException e = assertThrows(ResponseStatusException.class,
                () -> service.guardar(producto(null, "CL001", 5)));
        assertEquals(HttpStatus.CONFLICT, e.getStatusCode());
        verify(repository, never()).save(any());
    }

    @Test
    void actualizarConCodigoDeOtroProductoDevuelve409() {
        when(repository.findById(1L)).thenReturn(Optional.of(producto(1L, "CL001", 5)));
        when(repository.existsByCodigoIgnoreCaseAndIdNot("CL002", 1L)).thenReturn(true);

        ResponseStatusException e = assertThrows(ResponseStatusException.class,
                () -> service.actualizar(1L, producto(null, "CL002", 5)));
        assertEquals(HttpStatus.CONFLICT, e.getStatusCode());
    }

    @Test
    void actualizarCopiaLosDatos() {
        Producto existente = producto(1L, "CL001", 5);
        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        Producto datos = producto(null, "CL001", 20);
        datos.setPrecio(9990);

        Producto actualizado = service.actualizar(1L, datos);

        assertEquals(20, actualizado.getStock());
        assertEquals(9990, actualizado.getPrecio());
    }

    @Test
    void descontarMasQueElStockDevuelve409() {
        when(repository.findById(1L)).thenReturn(Optional.of(producto(1L, "CL001", 2)));

        ResponseStatusException e = assertThrows(ResponseStatusException.class, () -> service.descontar(1L, 3));
        assertEquals(HttpStatus.CONFLICT, e.getStatusCode());
    }

    @Test
    void eliminarProductoQueNoExisteDevuelve404() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        ResponseStatusException e = assertThrows(ResponseStatusException.class, () -> service.eliminar(99L));
        assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
    }

    private Producto producto(Long id, String codigo, int stock) {
        Producto p = new Producto();
        p.setId(id);
        p.setCodigo(codigo);
        p.setNombre("Cilindro");
        p.setCategoria("Cilindros de Gas");
        p.setStock(stock);
        p.setPrecio(6500);
        return p;
    }
}
