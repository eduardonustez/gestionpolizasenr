package com.gestionpolizas.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record Certificado(
        Long id,
        Long polizaId,
        int numeroCertificado,
        TipoMovimiento tipoMovimiento,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        BigDecimal canonMensual,
        BigDecimal valorPrima,
        BigDecimal ipcAplicado,
        LocalDateTime fechaRegistro) {

    static Certificado renovacion(Long polizaId, int numeroCertificado, LocalDate fechaInicio, LocalDate fechaFin,
                                  BigDecimal canonMensual, BigDecimal valorPrima, BigDecimal ipc,
                                  LocalDateTime fechaRegistro) {
        return new Certificado(null, polizaId, numeroCertificado, TipoMovimiento.RENOVACION,
                fechaInicio, fechaFin, canonMensual, valorPrima, ipc, fechaRegistro);
    }
}
