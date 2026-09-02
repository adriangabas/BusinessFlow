package dev.adriangabas.businessflow.usuario;

import dev.adriangabas.businessflow.rol.Rol;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios_roles")
public class UsuarioRol {
    @EmbeddedId
    private UsuarioRolId id;

    @MapsId("usuarioId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @MapsId("rolId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "rol_id", nullable = false)
    private Rol rol;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    protected UsuarioRol() {
    }

    public UsuarioRol(Usuario usuario, Rol rol) {
        this.id = new UsuarioRolId(usuario.getId(), rol.getId());
        this.usuario = usuario;
        this.rol = rol;
    }

    @PrePersist
    void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }

    public UsuarioRolId getId() { return id; }
    public Usuario getUsuario() { return usuario; }
    public Rol getRol() { return rol; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
