package dev.adriangabas.businessflow.producto.dto;

import dev.adriangabas.businessflow.categoria.CategoriaProducto;

public record CategoriaProductoResumen(Long id, String codigo, String nombre) {

    public static CategoriaProductoResumen from(CategoriaProducto categoria) {
        return new CategoriaProductoResumen(categoria.getId(), categoria.getCodigo(), categoria.getNombre());
    }
}
