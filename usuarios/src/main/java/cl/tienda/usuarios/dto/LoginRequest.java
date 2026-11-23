package cl.tienda.usuarios.dto;

import jakarta.validation.constraints.NotBlank;

// Datos que React envía al iniciar sesión.
public record LoginRequest(
        @NotBlank(message = "El correo es obligatorio")
        String correo,

        @NotBlank(message = "La contraseña es obligatoria")
        String password
) {
}
