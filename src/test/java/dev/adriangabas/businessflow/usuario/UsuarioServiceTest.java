package dev.adriangabas.businessflow.usuario;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dev.adriangabas.businessflow.usuario.dto.ActualizarUsuarioRequest;
import dev.adriangabas.businessflow.usuario.dto.CrearUsuarioRequest;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {
    @Mock UsuarioRepository repository;
    private UsuarioService service;

    @BeforeEach void setUp() { service = new UsuarioService(repository); }

    @Test
    void creaUsuarioNormalizandoEmailYConPendingPorDefecto() {
        conservarEntidadAlGuardar();
        var response = service.crear(crearRequest(" Persona@Example.COM ", null));
        assertThat(response.email()).isEqualTo("persona@example.com");
        assertThat(response.nombre()).isEqualTo("Persona");
        assertThat(response.estado()).isEqualTo(UsuarioEstado.PENDING);
    }

    @Test
    void rechazaEmailDuplicadoInclusoSiPerteneceAEliminado() {
        when(repository.existsByEmail("persona@example.com")).thenReturn(true);
        assertThatThrownBy(() -> service.crear(crearRequest("persona@example.com", null)))
                .isInstanceOf(UsuarioDuplicadoException.class);
    }

    @Test
    void actualizaDatosYConservaHashCuandoNoSeInforma() {
        Usuario usuario = usuario();
        when(repository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(usuario));
        conservarEntidadAlGuardar();
        var response = service.actualizar(1L, actualizarRequest(null, UsuarioEstado.BLOCKED));
        assertThat(response.email()).isEqualTo("nuevo@example.com");
        assertThat(response.estado()).isEqualTo(UsuarioEstado.BLOCKED);
        assertThat(usuario.getPasswordHash()).isEqualTo("hash-inicial");
    }

    @Test
    void actualizaHashOpacoCuandoSeInforma() {
        Usuario usuario = usuario();
        when(repository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(usuario));
        conservarEntidadAlGuardar();
        service.actualizar(1L, actualizarRequest("hash-nuevo", UsuarioEstado.ACTIVE));
        assertThat(usuario.getPasswordHash()).isEqualTo("hash-nuevo");
    }

    @Test
    void inactivoSinDeletedAtSigueExistiendo() {
        Usuario usuario = usuario();
        when(repository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(usuario));
        when(repository.findAllByDeletedAtIsNullOrderByIdAsc()).thenReturn(List.of(usuario));
        conservarEntidadAlGuardar();
        service.actualizar(1L, actualizarRequest(null, UsuarioEstado.INACTIVE));
        assertThat(usuario.getDeletedAt()).isNull();
        assertThat(service.obtener(1L).estado()).isEqualTo(UsuarioEstado.INACTIVE);
        assertThat(service.listar()).hasSize(1);
    }

    @Test
    void borradoLogicoLoOcultaYReservaEmail() {
        Usuario usuario = usuario();
        when(repository.findByIdAndDeletedAtIsNull(1L))
                .thenAnswer(i -> usuario.getDeletedAt() == null ? Optional.of(usuario) : Optional.empty());
        when(repository.findAllByDeletedAtIsNullOrderByIdAsc())
                .thenAnswer(i -> usuario.getDeletedAt() == null ? List.of(usuario) : List.of());
        conservarEntidadAlGuardar();
        service.eliminar(1L);
        assertThat(usuario.getEstado()).isEqualTo(UsuarioEstado.INACTIVE);
        assertThat(usuario.getDeletedAt()).isNotNull();
        assertThat(service.listar()).isEmpty();
        assertThatThrownBy(() -> service.obtener(1L)).isInstanceOf(UsuarioNoEncontradoException.class);
        when(repository.existsByEmail("persona@example.com")).thenReturn(true);
        assertThatThrownBy(() -> service.crear(crearRequest("persona@example.com", null)))
                .isInstanceOf(UsuarioDuplicadoException.class);
        verify(repository).save(usuario);
    }

    private Usuario usuario() {
        return new Usuario("Persona", "Prueba", "persona@example.com", "hash-inicial", UsuarioEstado.ACTIVE,
                null, null);
    }

    private CrearUsuarioRequest crearRequest(String email, UsuarioEstado estado) {
        return new CrearUsuarioRequest(" Persona ", "Prueba", email, "hash-opaco", estado, null, null);
    }

    private ActualizarUsuarioRequest actualizarRequest(String hash, UsuarioEstado estado) {
        return new ActualizarUsuarioRequest("Nueva", "Persona", " Nuevo@Example.com ", hash, estado, null, null);
    }

    private void conservarEntidadAlGuardar() {
        when(repository.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));
    }
}
