package dev.adriangabas.businessflow.usuario;

import dev.adriangabas.businessflow.rol.Rol;
import dev.adriangabas.businessflow.rol.RolService;
import dev.adriangabas.businessflow.usuario.dto.UsuarioRolResponse;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class UsuarioRolService {
    private final UsuarioRolRepository repository;
    private final UsuarioService usuarioService;
    private final RolService rolService;

    public UsuarioRolService(UsuarioRolRepository repository, UsuarioService usuarioService, RolService rolService) {
        this.repository = repository;
        this.usuarioService = usuarioService;
        this.rolService = rolService;
    }

    public List<UsuarioRolResponse> listar(Long usuarioId) {
        usuarioService.buscarNoEliminado(usuarioId);
        return repository.findAllByUsuarioIdAndRolDeletedAtIsNullOrderByCreatedAtAsc(usuarioId).stream()
                .map(UsuarioRolResponse::from).toList();
    }

    @Transactional
    public UsuarioRolResponse asignar(Long usuarioId, Long rolId) {
        Usuario usuario = usuarioService.buscarNoEliminado(usuarioId);
        Rol rol = rolService.buscarNoEliminado(rolId);
        UsuarioRolId id = new UsuarioRolId(usuarioId, rolId);
        if (repository.existsById(id)) throw new AsignacionRolDuplicadaException(usuarioId, rolId);
        return UsuarioRolResponse.from(repository.save(new UsuarioRol(usuario, rol)));
    }

    @Transactional
    public void quitar(Long usuarioId, Long rolId) {
        usuarioService.buscarNoEliminado(usuarioId);
        rolService.buscarNoEliminado(rolId);
        UsuarioRolId id = new UsuarioRolId(usuarioId, rolId);
        if (!repository.existsById(id)) throw new AsignacionRolNoEncontradaException(usuarioId, rolId);
        repository.deleteById(id);
    }
}
