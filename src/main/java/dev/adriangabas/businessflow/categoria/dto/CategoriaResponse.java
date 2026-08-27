package dev.adriangabas.businessflow.categoria.dto;

import dev.adriangabas.businessflow.categoria.CategoriaProducto;
import dev.adriangabas.businessflow.categoria.EstadoCategoria;
import java.time.LocalDateTime;

public record CategoriaResponse(
        Long id,
        String codigo,
        String nombre,
        String descripcion,
        EstadoCategoria estado,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {

    public static CategoriaResponse from(CategoriaProducto categoria) {
        return new CategoriaResponse(categoria.getId(), categoria.getCodigo(), categoria.getNombre(),
                categoria.getDescripcion(), categoria.getEstado(), categoria.getCreatedAt(), categoria.getUpdatedAt());
    }
}
