package dev.adriangabas.businessflow.rol;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolRepository extends JpaRepository<Rol, Long> {
    boolean existsByCodigo(String codigo);
    boolean existsByCodigoAndIdNot(String codigo, Long id);
    Optional<Rol> findByIdAndDeletedAtIsNull(Long id);
    List<Rol> findAllByDeletedAtIsNullOrderByIdAsc();
}
