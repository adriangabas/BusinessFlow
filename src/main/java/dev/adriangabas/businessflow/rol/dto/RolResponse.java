package dev.adriangabas.businessflow.rol.dto;

import dev.adriangabas.businessflow.rol.Rol;
import dev.adriangabas.businessflow.rol.RolEstado;
import java.time.LocalDateTime;

public record RolResponse(Long id, String codigo, String nombre, String descripcion, RolEstado estado,
        boolean isSystem, LocalDateTime createdAt, LocalDateTime updatedAt) {

    public static RolResponse from(Rol rol) {
        return new RolResponse(rol.getId(), rol.getCodigo(), rol.getNombre(), rol.getDescripcion(), rol.getEstado(),
                rol.isSystem(), rol.getCreatedAt(), rol.getUpdatedAt());
    }
}
