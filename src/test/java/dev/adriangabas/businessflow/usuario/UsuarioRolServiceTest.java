package dev.adriangabas.businessflow.usuario;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dev.adriangabas.businessflow.rol.Rol;
import dev.adriangabas.businessflow.rol.RolEstado;
import dev.adriangabas.businessflow.rol.RolNoEncontradoException;
import dev.adriangabas.businessflow.rol.RolService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class UsuarioRolServiceTest {
    @Mock UsuarioRolRepository repository;
    @Mock UsuarioService usuarioService;
    @Mock RolService rolService;
    private UsuarioRolService service;
    private Usuario usuario;
    private Rol rol;

    @BeforeEach
    void setUp() {
        service = new UsuarioRolService(repository, usuarioService, rolService);
        usuario = new Usuario("Persona", null, "persona@example.com", "hash", UsuarioEstado.ACTIVE, null, null);
        rol = new Rol("ADMIN", "Admin", null, RolEstado.ACTIVE, true);
        ReflectionTestUtils.setField(usuario, "id", 1L);
        ReflectionTestUtils.setField(rol, "id", 2L);
    }

    @Test
    void asignaRolYListaAsignaciones() {
        when(usuarioService.buscarNoEliminado(1L)).thenReturn(usuario);
        when(rolService.buscarNoEliminado(2L)).thenReturn(rol);
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));
        var response = service.asignar(1L, 2L);
        assertThat(response.codigo()).isEqualTo("ADMIN");
        UsuarioRol asignacion = new UsuarioRol(usuario, rol);
        when(repository.findAllByUsuarioIdAndRolDeletedAtIsNullOrderByCreatedAtAsc(1L)).thenReturn(List.of(asignacion));
        assertThat(service.listar(1L)).extracting("codigo").containsExactly("ADMIN");
    }

    @Test
    void rechazaAsignacionDuplicada() {
        when(usuarioService.buscarNoEliminado(1L)).thenReturn(usuario);
        when(rolService.buscarNoEliminado(2L)).thenReturn(rol);
        when(repository.existsById(new UsuarioRolId(1L, 2L))).thenReturn(true);
        assertThatThrownBy(() -> service.asignar(1L, 2L)).isInstanceOf(AsignacionRolDuplicadaException.class);
    }

    @Test
    void rechazaUsuarioInexistenteOEliminado() {
        when(usuarioService.buscarNoEliminado(99L)).thenThrow(new UsuarioNoEncontradoException(99L));
        assertThatThrownBy(() -> service.asignar(99L, 2L)).isInstanceOf(UsuarioNoEncontradoException.class);
    }

    @Test
    void rechazaRolInexistenteOEliminado() {
        when(usuarioService.buscarNoEliminado(1L)).thenReturn(usuario);
        when(rolService.buscarNoEliminado(99L)).thenThrow(new RolNoEncontradoException(99L));
        assertThatThrownBy(() -> service.asignar(1L, 99L)).isInstanceOf(RolNoEncontradoException.class);
    }

    @Test
    void quitaAsignacionSinEliminarRecursos() {
        when(usuarioService.buscarNoEliminado(1L)).thenReturn(usuario);
        when(rolService.buscarNoEliminado(2L)).thenReturn(rol);
        when(repository.existsById(new UsuarioRolId(1L, 2L))).thenReturn(true);
        service.quitar(1L, 2L);
        verify(repository).deleteById(new UsuarioRolId(1L, 2L));
    }

    @Test
    void devuelve404SiLaAsignacionNoExisteAlQuitar() {
        when(usuarioService.buscarNoEliminado(1L)).thenReturn(usuario);
        when(rolService.buscarNoEliminado(2L)).thenReturn(rol);
        assertThatThrownBy(() -> service.quitar(1L, 2L)).isInstanceOf(AsignacionRolNoEncontradaException.class);
    }
}
