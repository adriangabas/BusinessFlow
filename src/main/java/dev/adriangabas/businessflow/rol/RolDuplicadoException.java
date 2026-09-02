package dev.adriangabas.businessflow.rol;

public class RolDuplicadoException extends RuntimeException {
    public RolDuplicadoException(String codigo) {
        super("Ya existe un rol con el código " + codigo);
    }
}
