package com.gestionpolizas.domain.model;

import java.time.LocalDateTime;

public record EdicionCore(
        String entidad,
        Long entidadId,
        AccionEdicion accion,
        LocalDateTime fecha) {

    public static EdicionCore dePoliza(Long polizaId, AccionEdicion accion, LocalDateTime fecha) {
        return new EdicionCore("POLIZA", polizaId, accion, fecha);
    }

    public static EdicionCore deRiesgo(Long riesgoId, AccionEdicion accion, LocalDateTime fecha) {
        return new EdicionCore("RIESGO", riesgoId, accion, fecha);
    }
}
