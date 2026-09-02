package dev.adriangabas.businessflow.rol;

import dev.adriangabas.businessflow.rol.dto.ActualizarRolRequest;
import dev.adriangabas.businessflow.rol.dto.CrearRolRequest;
import dev.adriangabas.businessflow.rol.dto.RolResponse;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class RolService {
    private final RolRepository repository;

    public RolService(RolRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public RolResponse crear(CrearRolRequest request) {
        String codigo = normalizarCodigo(request.codigo());
        comprobarCodigoDuplicado(codigo, null);
        RolEstado estado = request.estado() == null ? RolEstado.ACTIVE : request.estado();
        boolean system = request.isSystem() == null || request.isSystem();
        Rol rol = new Rol(codigo, request.nombre().trim(), request.descripcion(), estado, system);
        return RolResponse.from(repository.save(rol));
    }

    public List<RolResponse> listar() {
        return repository.findAllByDeletedAtIsNullOrderByIdAsc().stream().map(RolResponse::from).toList();
    }

    public RolResponse obtener(Long id) {
        return RolResponse.from(buscarNoEliminado(id));
    }

    @Transactional
    public RolResponse actualizar(Long id, ActualizarRolRequest request) {
        Rol rol = buscarNoEliminado(id);
        String codigo = normalizarCodigo(request.codigo());
        comprobarCodigoDuplicado(codigo, id);
        rol.actualizar(codigo, request.nombre().trim(), request.descripcion(), request.estado(), request.isSystem());
        return RolResponse.from(repository.save(rol));
    }

    @Transactional
    public void eliminar(Long id) {
        Rol rol = buscarNoEliminado(id);
        rol.eliminar();
        repository.save(rol);
    }

    public Rol buscarNoEliminado(Long id) {
        return repository.findByIdAndDeletedAtIsNull(id).orElseThrow(() -> new RolNoEncontradoException(id));
    }

    private void comprobarCodigoDuplicado(String codigo, Long id) {
        boolean duplicado = id == null ? repository.existsByCodigo(codigo) : repository.existsByCodigoAndIdNot(codigo, id);
        if (duplicado) throw new RolDuplicadoException(codigo);
    }

    private String normalizarCodigo(String codigo) {
        return codigo.trim().toUpperCase(Locale.ROOT);
    }
}
