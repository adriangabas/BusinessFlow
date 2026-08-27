package dev.adriangabas.businessflow.categoria;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dev.adriangabas.businessflow.categoria.dto.ActualizarCategoriaRequest;
import dev.adriangabas.businessflow.categoria.dto.CrearCategoriaRequest;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CategoriaProductoServiceTest {
    @Mock CategoriaProductoRepository repository;
    private CategoriaProductoService service;

    @BeforeEach
    void setUp() {
        service = new CategoriaProductoService(repository);
    }

    @Test
    void creaCategoriaNormalizandoCodigoYEstadoPorDefecto() {
        conservarEntidadAlGuardar();
        var response = service.crear(new CrearCategoriaRequest("  alim ", "Alimentación", "Productos", null));
        assertThat(response.codigo()).isEqualTo("ALIM");
        assertThat(response.estado()).isEqualTo(EstadoCategoria.ACTIVO);
        verify(repository).save(any(CategoriaProducto.class));
    }

    @Test
    void rechazaCodigoDuplicado() {
        when(repository.existsByCodigo("ALIM")).thenReturn(true);
        assertThatThrownBy(() -> service.crear(new CrearCategoriaRequest("alim", "Alimentación", null, null)))
                .isInstanceOf(CategoriaDuplicadaException.class).hasMessageContaining("ALIM");
    }

    @Test
    void obtieneCategoriaPorId() {
        when(repository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(categoria()));
        assertThat(service.obtener(1L).codigo()).isEqualTo("ALIM");
    }

    @Test
    void informaCuandoNoExiste() {
        when(repository.findByIdAndDeletedAtIsNull(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.obtener(99L)).isInstanceOf(CategoriaNoEncontradaException.class);
    }

    @Test
    void listaCategoriasNoEliminadas() {
        when(repository.findAllByDeletedAtIsNullOrderByIdAsc()).thenReturn(List.of(categoria()));
        assertThat(service.listar()).extracting("codigo").containsExactly("ALIM");
    }

    @Test
    void actualizaCategoria() {
        conservarEntidadAlGuardar();
        CategoriaProducto categoria = categoria();
        when(repository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(categoria));
        var response = service.actualizar(1L,
                new ActualizarCategoriaRequest("beb", "Bebidas", null, EstadoCategoria.INACTIVO));
        assertThat(response.codigo()).isEqualTo("BEB");
        assertThat(response.estado()).isEqualTo(EstadoCategoria.INACTIVO);
    }

    @Test
    void eliminaCategoriaDeFormaLogica() {
        conservarEntidadAlGuardar();
        CategoriaProducto categoria = categoria();
        when(repository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(categoria));
        service.eliminar(1L);
        assertThat(categoria.getEstado()).isEqualTo(EstadoCategoria.INACTIVO);
        assertThat(categoria.getDeletedAt()).isNotNull();
        verify(repository).save(categoria);
    }

    private CategoriaProducto categoria() {
        return new CategoriaProducto("ALIM", "Alimentación", null, EstadoCategoria.ACTIVO);
    }

    private void conservarEntidadAlGuardar() {
        when(repository.save(any(CategoriaProducto.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }
}
