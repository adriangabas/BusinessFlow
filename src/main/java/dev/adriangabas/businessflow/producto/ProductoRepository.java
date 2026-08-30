package dev.adriangabas.businessflow.producto;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    boolean existsByCodigo(String codigo);

    boolean existsByCodigoAndIdNot(String codigo, Long id);

    List<Producto> findAllByDeletedAtIsNullOrderByIdAsc();

    Optional<Producto> findByIdAndDeletedAtIsNull(Long id);
}
