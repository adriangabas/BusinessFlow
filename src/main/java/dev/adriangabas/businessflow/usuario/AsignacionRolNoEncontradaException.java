package dev.adriangabas.businessflow.usuario;

public class AsignacionRolNoEncontradaException extends RuntimeException {
    public AsignacionRolNoEncontradaException(Long usuarioId, Long rolId) {
        super("El usuario " + usuarioId + " no tiene asignado el rol " + rolId);
    }
}
