package dev.adriangabas.businessflow.categoria.dto;

import dev.adriangabas.businessflow.categoria.EstadoCategoria;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ActualizarCategoriaRequest(
        @NotBlank(message = "El código es obligatorio")
        @Size(max = 30, message = "El código no puede superar 30 caracteres")
        String codigo,
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 150, message = "El nombre no puede superar 150 caracteres")
        String nombre,
        String descripcion,
        @NotNull(message = "El estado es obligatorio")
        EstadoCategoria estado) {
}
