package com.gestionpolizas.infrastructure.web.dto;

import com.gestionpolizas.domain.model.EstadoPoliza;
import com.gestionpolizas.domain.model.EstadoSyncCore;
import com.gestionpolizas.domain.model.TipoPoliza;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PolizaResponse(
        Long id,
        String numeroPoliza,
        TipoPoliza tipo,
        Long tomadorId,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        int mesesVigencia,
        BigDecimal canonMensual,
        BigDecimal valorPrima,
        EstadoPoliza estado,
        boolean renovacionAutomatica,
        int numeroRenovacion,
        EstadoSyncCore estadoSyncCore,
        long riesgosActivos) {
}
