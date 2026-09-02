package dev.adriangabas.businessflow.usuario;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRolRepository extends JpaRepository<UsuarioRol, UsuarioRolId> {
    List<UsuarioRol> findAllByUsuarioIdAndRolDeletedAtIsNullOrderByCreatedAtAsc(Long usuarioId);
}
