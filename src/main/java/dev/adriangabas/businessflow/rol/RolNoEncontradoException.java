package dev.adriangabas.businessflow.rol;

public class RolNoEncontradoException extends RuntimeException {
    public RolNoEncontradoException(Long id) {
        super("No se ha encontrado el rol con id " + id);
    }
}
