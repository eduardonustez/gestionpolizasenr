package com.gestionpolizas.infrastructure.web.dto;

import com.gestionpolizas.domain.model.TipoInmueble;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record InmuebleRequest(
        @NotBlank @Size(max = 30) String matriculaInmobiliaria,
        @NotBlank @Size(max = 200) String direccion,
        @NotBlank @Size(max = 100) String ciudad,
        @NotNull TipoInmueble tipo) {
}
