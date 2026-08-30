package dev.adriangabas.businessflow.categoria;

public class CategoriaNoEncontradaException extends RuntimeException {
    public CategoriaNoEncontradaException(Long id) {
        super("No existe la categoría de producto con id " + id);
    }
}
