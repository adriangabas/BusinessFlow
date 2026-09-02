package dev.adriangabas.businessflow.usuario;

import dev.adriangabas.businessflow.usuario.dto.ActualizarUsuarioRequest;
import dev.adriangabas.businessflow.usuario.dto.CrearUsuarioRequest;
import dev.adriangabas.businessflow.usuario.dto.UsuarioResponse;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class UsuarioService {

    private final UsuarioRepository repository;

    public UsuarioService(UsuarioRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public UsuarioResponse crear(CrearUsuarioRequest request) {
        String email = normalizarEmail(request.email());
        comprobarEmailDuplicado(email, null);
        UsuarioEstado estado = request.estado() == null ? UsuarioEstado.PENDING : request.estado();
        Usuario usuario = new Usuario(request.nombre().trim(), request.apellidos(), email, request.passwordHash(), estado,
                request.emailVerifiedAt(), request.lastLoginAt());
        return UsuarioResponse.from(repository.save(usuario));
    }

    public List<UsuarioResponse> listar() {
        return repository.findAllByDeletedAtIsNullOrderByIdAsc().stream().map(UsuarioResponse::from).toList();
    }

    public UsuarioResponse obtener(Long id) {
        return UsuarioResponse.from(buscarNoEliminado(id));
    }

    @Transactional
    public UsuarioResponse actualizar(Long id, ActualizarUsuarioRequest request) {
        Usuario usuario = buscarNoEliminado(id);
        String email = normalizarEmail(request.email());
        comprobarEmailDuplicado(email, id);
        usuario.actualizar(request.nombre().trim(), request.apellidos(), email, request.estado(),
                request.emailVerifiedAt(), request.lastLoginAt());
        if (request.passwordHash() != null) {
            usuario.cambiarPasswordHash(request.passwordHash());
        }
        return UsuarioResponse.from(repository.save(usuario));
    }

    @Transactional
    public void eliminar(Long id) {
        Usuario usuario = buscarNoEliminado(id);
        usuario.eliminar();
        repository.save(usuario);
    }

    Usuario buscarNoEliminado(Long id) {
        return repository.findByIdAndDeletedAtIsNull(id).orElseThrow(() -> new UsuarioNoEncontradoException(id));
    }

    private void comprobarEmailDuplicado(String email, Long id) {
        boolean duplicado = id == null ? repository.existsByEmail(email) : repository.existsByEmailAndIdNot(email, id);
        if (duplicado) {
            throw new UsuarioDuplicadoException(email);
        }
    }

    private String normalizarEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
