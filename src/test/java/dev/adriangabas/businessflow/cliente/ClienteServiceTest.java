package dev.adriangabas.businessflow.cliente;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dev.adriangabas.businessflow.cliente.dto.ActualizarClienteRequest;
import dev.adriangabas.businessflow.cliente.dto.CrearClienteRequest;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock ClienteRepository repository;
    private ClienteService service;

    @BeforeEach
    void setUp() {
        service = new ClienteService(repository);
    }

    @Test
    void creaClienteNormalizandoCodigoYConEstadoActivoPorDefecto() {
        conservarEntidadAlGuardar();

        var response = service.crear(crearRequest(" cli-1 ", null));

        assertThat(response.codigo()).isEqualTo("CLI-1");
        assertThat(response.nombre()).isEqualTo("Cliente Uno");
        assertThat(response.tipoCliente()).isEqualTo(TipoCliente.EMPRESA);
        assertThat(response.estado()).isEqualTo(EstadoCliente.ACTIVO);
    }

    @Test
    void permiteIdentificacionFiscalRepetida() {
        conservarEntidadAlGuardar();

        service.crear(crearRequest("CLI-1", EstadoCliente.ACTIVO));

        verify(repository).existsByCodigo("CLI-1");
        verify(repository).save(any(Cliente.class));
    }

    @Test
    void rechazaCodigoDuplicadoInclusoSiPerteneceAUnEliminado() {
        when(repository.existsByCodigo("CLI-1")).thenReturn(true);

        assertThatThrownBy(() -> service.crear(crearRequest("cli-1", null)))
                .isInstanceOf(ClienteDuplicadoException.class).hasMessageContaining("CLI-1");
    }

    @Test
    void listaYObtieneClientesNoEliminados() {
        Cliente cliente = cliente();
        when(repository.findAllByDeletedAtIsNullOrderByIdAsc()).thenReturn(List.of(cliente));
        when(repository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(cliente));

        assertThat(service.listar()).extracting("codigo").containsExactly("CLI-1");
        assertThat(service.obtener(1L).codigo()).isEqualTo("CLI-1");
    }

    @Test
    void devuelveNoEncontradoParaClienteInexistente() {
        when(repository.findByIdAndDeletedAtIsNull(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.obtener(99L)).isInstanceOf(ClienteNoEncontradoException.class);
    }

    @Test
    void actualizaTodosLosDatosDelCliente() {
        conservarEntidadAlGuardar();
        Cliente cliente = cliente();
        when(repository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(cliente));

        var response = service.actualizar(1L, actualizarRequest(EstadoCliente.ACTIVO));

        assertThat(response.codigo()).isEqualTo("CLI-2");
        assertThat(response.tipoCliente()).isEqualTo(TipoCliente.PARTICULAR);
        assertThat(response.nombre()).isEqualTo("Cliente Dos");
        assertThat(response.email()).isEqualTo("dos@example.com");
    }

    @Test
    void rechazaCodigoDuplicadoAlActualizar() {
        Cliente cliente = cliente();
        when(repository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(cliente));
        when(repository.existsByCodigoAndIdNot("CLI-2", 1L)).thenReturn(true);

        assertThatThrownBy(() -> service.actualizar(1L, actualizarRequest(EstadoCliente.ACTIVO)))
                .isInstanceOf(ClienteDuplicadoException.class);
    }

    @Test
    void inactivoSinBorradoSigueSiendoAccesibleYListable() {
        conservarEntidadAlGuardar();
        Cliente cliente = cliente();
        when(repository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(cliente));
        when(repository.findAllByDeletedAtIsNullOrderByIdAsc()).thenReturn(List.of(cliente));

        service.actualizar(1L, actualizarRequest(EstadoCliente.INACTIVO));

        assertThat(cliente.getEstado()).isEqualTo(EstadoCliente.INACTIVO);
        assertThat(cliente.getDeletedAt()).isNull();
        assertThat(service.obtener(1L).estado()).isEqualTo(EstadoCliente.INACTIVO);
        assertThat(service.listar()).hasSize(1);
    }

    @Test
    void borradoLogicoLoExcluyeYHaceQueNoSeaEncontrado() {
        conservarEntidadAlGuardar();
        Cliente cliente = cliente();
        when(repository.findByIdAndDeletedAtIsNull(1L))
                .thenAnswer(invocation -> cliente.getDeletedAt() == null ? Optional.of(cliente) : Optional.empty());
        when(repository.findAllByDeletedAtIsNullOrderByIdAsc())
                .thenAnswer(invocation -> cliente.getDeletedAt() == null ? List.of(cliente) : List.of());

        service.eliminar(1L);

        assertThat(cliente.getEstado()).isEqualTo(EstadoCliente.INACTIVO);
        assertThat(cliente.getDeletedAt()).isNotNull();
        assertThat(service.listar()).isEmpty();
        assertThatThrownBy(() -> service.obtener(1L)).isInstanceOf(ClienteNoEncontradoException.class);
        verify(repository).save(cliente);
    }

    private CrearClienteRequest crearRequest(String codigo, EstadoCliente estado) {
        return new CrearClienteRequest(codigo, TipoCliente.EMPRESA, " Cliente Uno ", "Comercial Uno", "B12345678",
                "uno@example.com", "600000001", "Calle Uno", "28001", "Madrid", "Madrid", "España", estado,
                "Observaciones");
    }

    private ActualizarClienteRequest actualizarRequest(EstadoCliente estado) {
        return new ActualizarClienteRequest(" cli-2 ", TipoCliente.PARTICULAR, " Cliente Dos ", null, "B12345678",
                "dos@example.com", null, null, null, null, null, null, estado, null);
    }

    private Cliente cliente() {
        return new Cliente("CLI-1", TipoCliente.EMPRESA, "Cliente Uno", null, "B12345678", "uno@example.com",
                null, null, null, null, null, null, EstadoCliente.ACTIVO, null);
    }

    private void conservarEntidadAlGuardar() {
        when(repository.save(any(Cliente.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }
}
