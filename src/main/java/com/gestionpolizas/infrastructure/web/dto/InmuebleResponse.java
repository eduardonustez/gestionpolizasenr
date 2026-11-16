package com.gestionpolizas.infrastructure.web.dto;

import com.gestionpolizas.domain.model.TipoInmueble;

public record InmuebleResponse(
        Long id,
        String matriculaInmobiliaria,
        String direccion,
        String ciudad,
        TipoInmueble tipo) {
}
