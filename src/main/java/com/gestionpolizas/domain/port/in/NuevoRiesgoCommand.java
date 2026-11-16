package com.gestionpolizas.domain.port.in;

import com.gestionpolizas.domain.model.Inmueble;
import com.gestionpolizas.domain.model.Tercero;

import java.math.BigDecimal;

public record NuevoRiesgoCommand(
        Inmueble inmueble,
        Tercero asegurado,
        Tercero beneficiario,
        BigDecimal canonMensual) {
}
