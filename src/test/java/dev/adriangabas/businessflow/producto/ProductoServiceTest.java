package dev.adriangabas.businessflow.producto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dev.adriangabas.businessflow.categoria.CategoriaNoEncontradaException;
import dev.adriangabas.businessflow.categoria.CategoriaProducto;
import dev.adriangabas.businessflow.categoria.CategoriaProductoRepository;
import dev.adriangabas.businessflow.categoria.EstadoCategoria;
import dev.adriangabas.businessflow.producto.dto.ActualizarProductoRequest;
import dev.adriangabas.businessflow.producto.dto.CrearProductoRequest;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock ProductoRepository repository;
    @Mock CategoriaProductoRepository categoriaRepository;
    private ProductoService service;

    @BeforeEach
    void setUp() {
        service = new ProductoService(repository, categoriaRepository);
    }

    @Test
    void creaProductoNormalizandoCodigoConPreciosExactosYEstadoPorDefecto() {
        conservarEntidadAlGuardar();
        when(categoriaRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(categoria()));

        var response = service.crear(crearRequest(" prod-1 ", null));

        assertThat(response.codigo()).isEqualTo("PROD-1");
        assertThat(response.estado()).isEqualTo(EstadoProducto.ACTIVO);
        assertThat(response.precioVenta()).isEqualByComparingTo("12.34");
        assertThat(response.precioCoste()).isEqualByComparingTo("5.67");
        assertThat(response.categoria().codigo()).isEqualTo("ALIM");
    }

    @Test
    void rechazaCodigoDuplicadoInclusoSiPerteneceAUnEliminado() {
        when(repository.existsByCodigo("PROD-1")).thenReturn(true);

        assertThatThrownBy(() -> service.crear(crearRequest("prod-1", null)))
                .isInstanceOf(ProductoDuplicadoException.class).hasMessageContaining("PROD-1");
    }

    @Test
    void rechazaCategoriaInexistenteOEliminada() {
        when(categoriaRepository.findByIdAndDeletedAtIsNull(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.crear(crearRequestConCategoria(99L)))
                .isInstanceOf(CategoriaNoEncontradaException.class);
    }

    @Test
    void permiteCategoriaInactivaNoEliminada() {
        conservarEntidadAlGuardar();
        CategoriaProducto inactiva = new CategoriaProducto("ALIM", "Alimentación", null, EstadoCategoria.INACTIVO);
        when(categoriaRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(inactiva));

        assertThat(service.crear(crearRequest("PROD-1", EstadoProducto.ACTIVO)).categoria().codigo())
                .isEqualTo("ALIM");
    }

    @Test
    void listaYObtieneProductosNoEliminados() {
        Producto producto = producto();
        when(repository.findAllByDeletedAtIsNullOrderByIdAsc()).thenReturn(List.of(producto));
        when(repository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(producto));

        assertThat(service.listar()).extracting("codigo").containsExactly("PROD-1");
        assertThat(service.obtener(1L).codigo()).isEqualTo("PROD-1");
    }

    @Test
    void actualizaProductoYCategoria() {
        conservarEntidadAlGuardar();
        Producto producto = producto();
        CategoriaProducto nuevaCategoria = new CategoriaProducto("BEB", "Bebidas", null, EstadoCategoria.ACTIVO);
        when(repository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(producto));
        when(categoriaRepository.findByIdAndDeletedAtIsNull(2L)).thenReturn(Optional.of(nuevaCategoria));

        var response = service.actualizar(1L, actualizarRequest(EstadoProducto.ACTIVO));

        assertThat(response.codigo()).isEqualTo("PROD-2");
        assertThat(response.categoria().codigo()).isEqualTo("BEB");
        assertThat(response.precioVenta()).isEqualByComparingTo("20.25");
    }

    @Test
    void inactivoSinBorradoSigueSiendoAccesibleYListable() {
        conservarEntidadAlGuardar();
        Producto producto = producto();
        when(repository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(producto));
        when(categoriaRepository.findByIdAndDeletedAtIsNull(2L)).thenReturn(Optional.of(categoria()));
        when(repository.findAllByDeletedAtIsNullOrderByIdAsc()).thenReturn(List.of(producto));

        service.actualizar(1L, actualizarRequest(EstadoProducto.INACTIVO));

        assertThat(producto.getEstado()).isEqualTo(EstadoProducto.INACTIVO);
        assertThat(producto.getDeletedAt()).isNull();
        assertThat(service.obtener(1L).estado()).isEqualTo(EstadoProducto.INACTIVO);
        assertThat(service.listar()).hasSize(1);
    }

    @Test
    void borradoLogicoLoExcluyeYHaceQueNoSeaEncontrado() {
        conservarEntidadAlGuardar();
        Producto producto = producto();
        when(repository.findByIdAndDeletedAtIsNull(1L))
                .thenAnswer(invocation -> producto.getDeletedAt() == null ? Optional.of(producto) : Optional.empty());
        when(repository.findAllByDeletedAtIsNullOrderByIdAsc())
                .thenAnswer(invocation -> producto.getDeletedAt() == null ? List.of(producto) : List.of());

        service.eliminar(1L);

        assertThat(producto.getEstado()).isEqualTo(EstadoProducto.INACTIVO);
        assertThat(producto.getDeletedAt()).isNotNull();
        assertThat(service.listar()).isEmpty();
        assertThatThrownBy(() -> service.obtener(1L)).isInstanceOf(ProductoNoEncontradoException.class);
        verify(repository).save(producto);
    }

    private CrearProductoRequest crearRequest(String codigo, EstadoProducto estado) {
        return new CrearProductoRequest(codigo, "Producto", "Descripción", 1L, new BigDecimal("12.34"),
                new BigDecimal("5.67"), 3, UnidadMedida.UNIDAD, "https://example.com/producto.png", estado,
                "Observaciones");
    }

    private CrearProductoRequest crearRequestConCategoria(Long categoriaId) {
        CrearProductoRequest request = crearRequest("PROD-1", EstadoProducto.ACTIVO);
        return new CrearProductoRequest(request.codigo(), request.nombre(), request.descripcion(), categoriaId,
                request.precioVenta(), request.precioCoste(), request.stockMinimo(), request.unidadMedida(),
                request.imagenUrl(), request.estado(), request.observaciones());
    }

    private ActualizarProductoRequest actualizarRequest(EstadoProducto estado) {
        return new ActualizarProductoRequest(" prod-2 ", "Producto actualizado", null, 2L,
                new BigDecimal("20.25"), new BigDecimal("10.10"), 4, UnidadMedida.CAJA, null, estado, null);
    }

    private CategoriaProducto categoria() {
        return new CategoriaProducto("ALIM", "Alimentación", null, EstadoCategoria.ACTIVO);
    }

    private Producto producto() {
        return new Producto("PROD-1", "Producto", null, categoria(), new BigDecimal("12.34"),
                new BigDecimal("5.67"), 3, UnidadMedida.UNIDAD, null, EstadoProducto.ACTIVO, null);
    }

    private void conservarEntidadAlGuardar() {
        when(repository.save(any(Producto.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }
}
