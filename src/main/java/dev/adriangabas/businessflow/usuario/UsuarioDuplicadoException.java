package dev.adriangabas.businessflow.usuario;

public class UsuarioDuplicadoException extends RuntimeException {
    public UsuarioDuplicadoException(String email) {
        super("Ya existe un usuario con el email " + email);
    }
}
