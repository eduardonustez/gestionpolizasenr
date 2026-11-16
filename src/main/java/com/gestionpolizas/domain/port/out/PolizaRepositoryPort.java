package com.gestionpolizas.domain.port.out;

import com.gestionpolizas.domain.model.EstadoPoliza;
import com.gestionpolizas.domain.model.Poliza;
import com.gestionpolizas.domain.model.TipoPoliza;

import java.util.List;
import java.util.Optional;

public interface PolizaRepositoryPort {

    Optional<Poliza> buscarPorId(Long id);

    Optional<Poliza> buscarPorRiesgoId(Long riesgoId);

    List<Poliza> buscar(TipoPoliza tipo, EstadoPoliza estado);

    Poliza guardar(Poliza poliza);
}
