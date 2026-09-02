package dev.adriangabas.businessflow.rol;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import dev.adriangabas.businessflow.rol.dto.ActualizarRolRequest;
import dev.adriangabas.businessflow.rol.dto.CrearRolRequest;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RolServiceTest {
    @Mock RolRepository repository;
    private RolService service;

    @BeforeEach void setUp() { service = new RolService(repository); }

    @Test
    void creaRolNormalizandoCodigoYConDefaults() {
        conservarEntidadAlGuardar();
        var response = service.crear(new CrearRolRequest(" admin ", " Administrador ", null, null, null));
        assertThat(response.codigo()).isEqualTo("ADMIN");
        assertThat(response.nombre()).isEqualTo("Administrador");
        assertThat(response.estado()).isEqualTo(RolEstado.ACTIVE);
        assertThat(response.isSystem()).isTrue();
    }

    @Test
    void rechazaCodigoDuplicadoInclusoSiPerteneceAEliminado() {
        when(repository.existsByCodigo("ADMIN")).thenReturn(true);
        assertThatThrownBy(() -> service.crear(new CrearRolRequest("admin", "Admin", null, null, false)))
                .isInstanceOf(RolDuplicadoException.class);
    }

    @Test
    void actualizaRol() {
        Rol rol = rol();
        when(repository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(rol));
        conservarEntidadAlGuardar();
        var response = service.actualizar(1L,
                new ActualizarRolRequest(" editor ", "Editor", "Edita", RolEstado.ACTIVE, false));
        assertThat(response.codigo()).isEqualTo("EDITOR");
        assertThat(response.isSystem()).isFalse();
    }

    @Test
    void inactivoSinDeletedAtSigueExistiendo() {
        Rol rol = rol();
        when(repository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(rol));
        when(repository.findAllByDeletedAtIsNullOrderByIdAsc()).thenReturn(List.of(rol));
        conservarEntidadAlGuardar();
        service.actualizar(1L, new ActualizarRolRequest("ADMIN", "Admin", null, RolEstado.INACTIVE, true));
        assertThat(rol.getDeletedAt()).isNull();
        assertThat(service.obtener(1L).estado()).isEqualTo(RolEstado.INACTIVE);
        assertThat(service.listar()).hasSize(1);
    }

    @Test
    void borradoLogicoLoOcultaYReservaCodigo() {
        Rol rol = rol();
        when(repository.findByIdAndDeletedAtIsNull(1L))
                .thenAnswer(i -> rol.getDeletedAt() == null ? Optional.of(rol) : Optional.empty());
        when(repository.findAllByDeletedAtIsNullOrderByIdAsc())
                .thenAnswer(i -> rol.getDeletedAt() == null ? List.of(rol) : List.of());
        conservarEntidadAlGuardar();
        service.eliminar(1L);
        assertThat(rol.getEstado()).isEqualTo(RolEstado.INACTIVE);
        assertThat(rol.getDeletedAt()).isNotNull();
        assertThat(service.listar()).isEmpty();
        assertThatThrownBy(() -> service.obtener(1L)).isInstanceOf(RolNoEncontradoException.class);
        when(repository.existsByCodigo("ADMIN")).thenReturn(true);
        assertThatThrownBy(() -> service.crear(new CrearRolRequest("ADMIN", "Admin", null, null, true)))
                .isInstanceOf(RolDuplicadoException.class);
    }

    private Rol rol() { return new Rol("ADMIN", "Admin", null, RolEstado.ACTIVE, true); }
    private void conservarEntidadAlGuardar() {
        when(repository.save(any(Rol.class))).thenAnswer(i -> i.getArgument(0));
    }
}
