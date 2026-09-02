package dev.adriangabas.businessflow.rol.dto;

import dev.adriangabas.businessflow.rol.RolEstado;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CrearRolRequest(
        @NotBlank(message = "El código es obligatorio")
        @Size(max = 50, message = "El código no puede superar 50 caracteres")
        String codigo,
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
        String nombre,
        String descripcion,
        RolEstado estado,
        Boolean isSystem) {
}
