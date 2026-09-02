package dev.adriangabas.businessflow.usuario;

public class UsuarioNoEncontradoException extends RuntimeException {
    public UsuarioNoEncontradoException(Long id) {
        super("No se ha encontrado el usuario con id " + id);
    }
}
