package com.gestionpolizas.infrastructure.web.dto;

import com.gestionpolizas.domain.model.TipoDocumento;
import com.gestionpolizas.domain.model.TipoPersona;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TerceroRequest(
        @NotNull TipoDocumento tipoDocumento,
        @NotBlank @Size(max = 20) String numeroDocumento,
        @NotNull TipoPersona tipoPersona,
        @NotBlank @Size(max = 200) String nombreRazonSocial,
        @Email @Size(max = 150) String email,
        @Size(max = 20) String celular,
        Boolean aceptaNotificaciones) {
}
