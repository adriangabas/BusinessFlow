package dev.adriangabas.businessflow.usuario.dto;

import dev.adriangabas.businessflow.usuario.UsuarioEstado;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public record CrearUsuarioRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
        String nombre,
        @Size(max = 150, message = "Los apellidos no pueden superar 150 caracteres")
        String apellidos,
        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email debe tener un formato válido")
        @Size(max = 254, message = "El email no puede superar 254 caracteres")
        String email,
        @NotBlank(message = "El hash de contraseña es obligatorio")
        @Size(max = 255, message = "El hash de contraseña no puede superar 255 caracteres")
        String passwordHash,
        UsuarioEstado estado,
        LocalDateTime emailVerifiedAt,
        LocalDateTime lastLoginAt) {

    public CrearUsuarioRequest {
        if (email != null) email = email.trim();
    }
}
