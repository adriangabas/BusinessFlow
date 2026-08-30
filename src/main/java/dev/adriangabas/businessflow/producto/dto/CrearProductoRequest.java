package dev.adriangabas.businessflow.producto.dto;

import dev.adriangabas.businessflow.producto.EstadoProducto;
import dev.adriangabas.businessflow.producto.UnidadMedida;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record CrearProductoRequest(
        @NotBlank(message = "El código es obligatorio")
        @Size(max = 30, message = "El código no puede superar 30 caracteres")
        String codigo,
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 200, message = "El nombre no puede superar 200 caracteres")
        String nombre,
        String descripcion,
        @NotNull(message = "La categoría es obligatoria")
        Long categoriaId,
        @NotNull(message = "El precio de venta es obligatorio")
        @DecimalMin(value = "0.00", message = "El precio de venta no puede ser negativo")
        @Digits(integer = 10, fraction = 2, message = "El precio de venta debe tener como máximo 10 enteros y 2 decimales")
        BigDecimal precioVenta,
        @NotNull(message = "El precio de coste es obligatorio")
        @DecimalMin(value = "0.00", message = "El precio de coste no puede ser negativo")
        @Digits(integer = 10, fraction = 2, message = "El precio de coste debe tener como máximo 10 enteros y 2 decimales")
        BigDecimal precioCoste,
        @NotNull(message = "El stock mínimo es obligatorio")
        @PositiveOrZero(message = "El stock mínimo no puede ser negativo")
        Integer stockMinimo,
        @NotNull(message = "La unidad de medida es obligatoria")
        UnidadMedida unidadMedida,
        @Size(max = 500, message = "La URL de imagen no puede superar 500 caracteres")
        String imagenUrl,
        EstadoProducto estado,
        String observaciones) {
}
