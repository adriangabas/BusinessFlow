package dev.adriangabas.businessflow.categoria;

import dev.adriangabas.businessflow.categoria.dto.ActualizarCategoriaRequest;
import dev.adriangabas.businessflow.categoria.dto.CategoriaResponse;
import dev.adriangabas.businessflow.categoria.dto.CrearCategoriaRequest;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class CategoriaProductoService {

    private final CategoriaProductoRepository repository;

    public CategoriaProductoService(CategoriaProductoRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public CategoriaResponse crear(CrearCategoriaRequest request) {
        String codigo = normalizar(request.codigo());
        comprobarCodigoDuplicado(codigo, null);
        EstadoCategoria estado = request.estado() == null ? EstadoCategoria.ACTIVO : request.estado();
        CategoriaProducto categoria = new CategoriaProducto(codigo, request.nombre().trim(), request.descripcion(), estado);
        return CategoriaResponse.from(repository.save(categoria));
    }

    public List<CategoriaResponse> listar() {
        return repository.findAllByDeletedAtIsNullOrderByIdAsc().stream().map(CategoriaResponse::from).toList();
    }

    public CategoriaResponse obtener(Long id) {
        return CategoriaResponse.from(buscarActiva(id));
    }

    @Transactional
    public CategoriaResponse actualizar(Long id, ActualizarCategoriaRequest request) {
        CategoriaProducto categoria = buscarActiva(id);
        String codigo = normalizar(request.codigo());
        comprobarCodigoDuplicado(codigo, id);
        categoria.actualizar(codigo, request.nombre().trim(), request.descripcion(), request.estado());
        return CategoriaResponse.from(repository.save(categoria));
    }

    @Transactional
    public void eliminar(Long id) {
        CategoriaProducto categoria = buscarActiva(id);
        categoria.eliminar();
        repository.save(categoria);
    }

    private CategoriaProducto buscarActiva(Long id) {
        return repository.findByIdAndDeletedAtIsNull(id).orElseThrow(() -> new CategoriaNoEncontradaException(id));
    }

    private void comprobarCodigoDuplicado(String codigo, Long id) {
        boolean duplicado = id == null ? repository.existsByCodigo(codigo) : repository.existsByCodigoAndIdNot(codigo, id);
        if (duplicado) {
            throw new CategoriaDuplicadaException(codigo);
        }
    }

    private String normalizar(String codigo) {
        return codigo.trim().toUpperCase();
    }
}
