package dev.adriangabas.businessflow.producto;

public class ProductoNoEncontradoException extends RuntimeException {
    public ProductoNoEncontradoException(Long id) {
        super("No existe un producto no eliminado con id " + id);
    }
}
