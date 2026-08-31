package dev.adriangabas.businessflow.cliente.dto;

import dev.adriangabas.businessflow.cliente.EstadoCliente;
import dev.adriangabas.businessflow.cliente.TipoCliente;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ActualizarClienteRequest(
        @NotBlank(message = "El código es obligatorio")
        @Size(max = 30, message = "El código no puede superar 30 caracteres")
        String codigo,
        @NotNull(message = "El tipo de cliente es obligatorio")
        TipoCliente tipoCliente,
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 200, message = "El nombre no puede superar 200 caracteres")
        String nombre,
        @Size(max = 200, message = "El nombre comercial no puede superar 200 caracteres")
        String nombreComercial,
        @Size(max = 50, message = "La identificación fiscal no puede superar 50 caracteres")
        String identificacionFiscal,
        @Email(message = "El email debe tener un formato válido")
        @Size(max = 254, message = "El email no puede superar 254 caracteres")
        String email,
        @Size(max = 20, message = "El teléfono no puede superar 20 caracteres")
        String telefono,
        @Size(max = 255, message = "La dirección no puede superar 255 caracteres")
        String direccion,
        @Size(max = 20, message = "El código postal no puede superar 20 caracteres")
        String codigoPostal,
        @Size(max = 100, message = "La localidad no puede superar 100 caracteres")
        String localidad,
        @Size(max = 100, message = "La provincia no puede superar 100 caracteres")
        String provincia,
        @Size(max = 100, message = "El país no puede superar 100 caracteres")
        String pais,
        @NotNull(message = "El estado es obligatorio")
        EstadoCliente estado,
        String observaciones) {
}
