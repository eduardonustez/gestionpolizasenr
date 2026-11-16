package com.gestionpolizas.infrastructure.web.dto;

import com.gestionpolizas.domain.model.TipoDocumento;

public record TerceroResponse(
        Long id,
        TipoDocumento tipoDocumento,
        String numeroDocumento,
        String nombreRazonSocial) {
}
