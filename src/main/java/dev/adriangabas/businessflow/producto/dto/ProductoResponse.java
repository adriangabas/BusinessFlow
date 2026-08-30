package dev.adriangabas.businessflow.producto.dto;

import dev.adriangabas.businessflow.producto.EstadoProducto;
import dev.adriangabas.businessflow.producto.Producto;
import dev.adriangabas.businessflow.producto.UnidadMedida;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductoResponse(
        Long id,
        String codigo,
        String nombre,
        String descripcion,
        CategoriaProductoResumen categoria,
        BigDecimal precioVenta,
        BigDecimal precioCoste,
        Integer stockMinimo,
        UnidadMedida unidadMedida,
        String imagenUrl,
        EstadoProducto estado,
        String observaciones,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {

    public static ProductoResponse from(Producto producto) {
        return new ProductoResponse(producto.getId(), producto.getCodigo(), producto.getNombre(),
                producto.getDescripcion(), CategoriaProductoResumen.from(producto.getCategoria()),
                producto.getPrecioVenta(), producto.getPrecioCoste(), producto.getStockMinimo(),
                producto.getUnidadMedida(), producto.getImagenUrl(), producto.getEstado(),
                producto.getObservaciones(), producto.getCreatedAt(), producto.getUpdatedAt());
    }
}
