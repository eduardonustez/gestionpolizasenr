package com.gestionpolizas.domain.model;

public record Tercero(
        Long id,
        TipoDocumento tipoDocumento,
        String numeroDocumento,
        TipoPersona tipoPersona,
        String nombreRazonSocial,
        String email,
        String celular,
        boolean aceptaNotificaciones) {
}
