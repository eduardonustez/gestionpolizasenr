package com.gestionpolizas.infrastructure.web.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record NuevoRiesgoRequest(
        @NotNull @Valid InmuebleRequest inmueble,
        @NotNull @Valid TerceroRequest asegurado,
        @NotNull @Valid TerceroRequest beneficiario,
        @NotNull @Positive @Digits(integer = 13, fraction = 2) BigDecimal canonMensual) {
}
