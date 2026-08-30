package dev.adriangabas.businessflow.categoria;

public class CategoriaDuplicadaException extends RuntimeException {
    public CategoriaDuplicadaException(String codigo) {
        super("Ya existe una categoría de producto con el código " + codigo);
    }
}
