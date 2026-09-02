package dev.adriangabas.businessflow.cliente.dto;

import dev.adriangabas.businessflow.cliente.Cliente;
import dev.adriangabas.businessflow.cliente.EstadoCliente;
import dev.adriangabas.businessflow.cliente.TipoCliente;
import java.time.LocalDateTime;

public record ClienteResponse(
        Long id,
        String codigo,
        TipoCliente tipoCliente,
        String nombre,
        String nombreComercial,
        String identificacionFiscal,
        String email,
        String telefono,
        String direccion,
        String codigoPostal,
        String localidad,
        String provincia,
        String pais,
        EstadoCliente estado,
        String observaciones,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {

    public static ClienteResponse from(Cliente cliente) {
        return new ClienteResponse(cliente.getId(), cliente.getCodigo(), cliente.getTipoCliente(), cliente.getNombre(),
                cliente.getNombreComercial(), cliente.getIdentificacionFiscal(), cliente.getEmail(),
                cliente.getTelefono(), cliente.getDireccion(), cliente.getCodigoPostal(), cliente.getLocalidad(),
                cliente.getProvincia(), cliente.getPais(), cliente.getEstado(), cliente.getObservaciones(),
                cliente.getCreatedAt(), cliente.getUpdatedAt());
    }
}
