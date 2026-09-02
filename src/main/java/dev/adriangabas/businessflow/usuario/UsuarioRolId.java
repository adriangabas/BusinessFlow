package dev.adriangabas.businessflow.usuario;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class UsuarioRolId implements Serializable {
    @Column(name = "usuario_id", columnDefinition = "BIGINT UNSIGNED")
    private Long usuarioId;

    @Column(name = "rol_id", columnDefinition = "BIGINT UNSIGNED")
    private Long rolId;

    protected UsuarioRolId() {
    }

    public UsuarioRolId(Long usuarioId, Long rolId) {
        this.usuarioId = usuarioId;
        this.rolId = rolId;
    }

    public Long getUsuarioId() { return usuarioId; }
    public Long getRolId() { return rolId; }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof UsuarioRolId that)) return false;
        return Objects.equals(usuarioId, that.usuarioId) && Objects.equals(rolId, that.rolId);
    }

    @Override
    public int hashCode() { return Objects.hash(usuarioId, rolId); }
}
