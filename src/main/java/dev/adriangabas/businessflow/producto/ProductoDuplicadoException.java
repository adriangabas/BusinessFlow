package dev.adriangabas.businessflow.producto;

public class ProductoDuplicadoException extends RuntimeException {
    public ProductoDuplicadoException(String codigo) {
        super("Ya existe un producto con código " + codigo);
    }
}
