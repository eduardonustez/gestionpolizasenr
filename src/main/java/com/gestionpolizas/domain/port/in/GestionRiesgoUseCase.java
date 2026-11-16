package com.gestionpolizas.domain.port.in;

import com.gestionpolizas.domain.model.Riesgo;

public interface GestionRiesgoUseCase {

    Riesgo agregar(Long polizaId, NuevoRiesgoCommand comando);

    Riesgo cancelar(Long riesgoId, String motivo);
}
