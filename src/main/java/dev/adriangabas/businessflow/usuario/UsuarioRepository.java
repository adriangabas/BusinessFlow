package dev.adriangabas.businessflow.usuario;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);

    Optional<Usuario> findByIdAndDeletedAtIsNull(Long id);

    List<Usuario> findAllByDeletedAtIsNullOrderByIdAsc();
}
