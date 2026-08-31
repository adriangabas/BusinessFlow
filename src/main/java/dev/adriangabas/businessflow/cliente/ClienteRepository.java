package dev.adriangabas.businessflow.cliente;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    boolean existsByCodigo(String codigo);

    boolean existsByCodigoAndIdNot(String codigo, Long id);

    Optional<Cliente> findByIdAndDeletedAtIsNull(Long id);

    List<Cliente> findAllByDeletedAtIsNullOrderByIdAsc();
}
