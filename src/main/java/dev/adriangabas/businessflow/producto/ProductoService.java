package dev.adriangabas.businessflow.producto;

import dev.adriangabas.businessflow.categoria.CategoriaNoEncontradaException;
import dev.adriangabas.businessflow.categoria.CategoriaProducto;
import dev.adriangabas.businessflow.categoria.CategoriaProductoRepository;
import dev.adriangabas.businessflow.producto.dto.ActualizarProductoRequest;
import dev.adriangabas.businessflow.producto.dto.CrearProductoRequest;
import dev.adriangabas.businessflow.producto.dto.ProductoResponse;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ProductoService {

    private final ProductoRepository repository;
    private final CategoriaProductoRepository categoriaRepository;

    public ProductoService(ProductoRepository repository, CategoriaProductoRepository categoriaRepository) {
        this.repository = repository;
        this.categoriaRepository = categoriaRepository;
    }

    @Transactional
    public ProductoResponse crear(CrearProductoRequest request) {
        String codigo = normalizar(request.codigo());
        comprobarCodigoDuplicado(codigo, null);
        CategoriaProducto categoria = resolverCategoria(request.categoriaId());
        EstadoProducto estado = request.estado() == null ? EstadoProducto.ACTIVO : request.estado();
        Producto producto = new Producto(codigo, request.nombre().trim(), request.descripcion(), categoria,
                request.precioVenta(), request.precioCoste(), request.stockMinimo(), request.unidadMedida(),
                request.imagenUrl(), estado, request.observaciones());
        return ProductoResponse.from(repository.save(producto));
    }

    public List<ProductoResponse> listar() {
        return repository.findAllByDeletedAtIsNullOrderByIdAsc().stream().map(ProductoResponse::from).toList();
    }

    public ProductoResponse obtener(Long id) {
        return ProductoResponse.from(buscarNoEliminado(id));
    }

    @Transactional
    public ProductoResponse actualizar(Long id, ActualizarProductoRequest request) {
        Producto producto = buscarNoEliminado(id);
        String codigo = normalizar(request.codigo());
        comprobarCodigoDuplicado(codigo, id);
        CategoriaProducto categoria = resolverCategoria(request.categoriaId());
        producto.actualizar(codigo, request.nombre().trim(), request.descripcion(), categoria, request.precioVenta(),
                request.precioCoste(), request.stockMinimo(), request.unidadMedida(), request.imagenUrl(),
                request.estado(), request.observaciones());
        return ProductoResponse.from(repository.save(producto));
    }

    @Transactional
    public void eliminar(Long id) {
        Producto producto = buscarNoEliminado(id);
        producto.eliminar();
        repository.save(producto);
    }

    private Producto buscarNoEliminado(Long id) {
        return repository.findByIdAndDeletedAtIsNull(id).orElseThrow(() -> new ProductoNoEncontradoException(id));
    }

    private CategoriaProducto resolverCategoria(Long id) {
        return categoriaRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new CategoriaNoEncontradaException(id));
    }

    private void comprobarCodigoDuplicado(String codigo, Long id) {
        boolean duplicado = id == null ? repository.existsByCodigo(codigo)
                : repository.existsByCodigoAndIdNot(codigo, id);
        if (duplicado) {
            throw new ProductoDuplicadoException(codigo);
        }
    }

    private String normalizar(String codigo) {
        return codigo.trim().toUpperCase();
    }
}
