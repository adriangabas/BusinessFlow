package dev.adriangabas.businessflow.usuario.dto;

import dev.adriangabas.businessflow.usuario.Usuario;
import dev.adriangabas.businessflow.usuario.UsuarioEstado;
import java.time.LocalDateTime;

public record UsuarioResponse(
        Long id,
        String nombre,
        String apellidos,
        String email,
        UsuarioEstado estado,
        LocalDateTime emailVerifiedAt,
        LocalDateTime lastLoginAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {

    public static UsuarioResponse from(Usuario usuario) {
        return new UsuarioResponse(usuario.getId(), usuario.getNombre(), usuario.getApellidos(), usuario.getEmail(),
                usuario.getEstado(), usuario.getEmailVerifiedAt(), usuario.getLastLoginAt(), usuario.getCreatedAt(),
                usuario.getUpdatedAt());
    }
}
