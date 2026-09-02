package dev.adriangabas.businessflow.usuario.dto;

import dev.adriangabas.businessflow.rol.RolEstado;
import dev.adriangabas.businessflow.usuario.UsuarioRol;
import java.time.LocalDateTime;

public record UsuarioRolResponse(Long id, String codigo, String nombre, RolEstado estado, boolean isSystem,
        LocalDateTime asignadoAt) {
    public static UsuarioRolResponse from(UsuarioRol asignacion) {
        var rol = asignacion.getRol();
        return new UsuarioRolResponse(rol.getId(), rol.getCodigo(), rol.getNombre(), rol.getEstado(), rol.isSystem(),
                asignacion.getCreatedAt());
    }
}
