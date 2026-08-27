package dev.adriangabas.businessflow.categoria;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaProductoRepository extends JpaRepository<CategoriaProducto, Long> {

    boolean existsByCodigo(String codigo);

    boolean existsByCodigoAndIdNot(String codigo, Long id);

    List<CategoriaProducto> findAllByDeletedAtIsNullOrderByIdAsc();

    Optional<CategoriaProducto> findByIdAndDeletedAtIsNull(Long id);
}
