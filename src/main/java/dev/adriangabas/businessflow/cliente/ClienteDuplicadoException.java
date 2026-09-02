package dev.adriangabas.businessflow.cliente;

public class ClienteDuplicadoException extends RuntimeException {
    public ClienteDuplicadoException(String codigo) {
        super("Ya existe un cliente con el código " + codigo);
    }
}
