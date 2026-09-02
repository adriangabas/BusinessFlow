package dev.adriangabas.businessflow.usuario;

public class AsignacionRolDuplicadaException extends RuntimeException {
    public AsignacionRolDuplicadaException(Long usuarioId, Long rolId) {
        super("El usuario " + usuarioId + " ya tiene asignado el rol " + rolId);
    }
}
