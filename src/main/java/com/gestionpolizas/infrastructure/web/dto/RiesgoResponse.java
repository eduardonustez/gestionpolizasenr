package com.gestionpolizas.infrastructure.web.dto;

import com.gestionpolizas.domain.model.EstadoRiesgo;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RiesgoResponse(
        Long id,
        Long polizaId,
        InmuebleResponse inmueble,
        TerceroResponse asegurado,
        TerceroResponse beneficiario,
        BigDecimal canonMensual,
        BigDecimal valorPrima,
        LocalDate fechaInclusion,
        LocalDate fechaCancelacion,
        String motivoCancelacion,
        EstadoRiesgo estado) {
}
