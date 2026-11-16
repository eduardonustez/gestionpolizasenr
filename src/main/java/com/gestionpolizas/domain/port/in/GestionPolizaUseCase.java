package com.gestionpolizas.domain.port.in;

import com.gestionpolizas.domain.model.EstadoPoliza;
import com.gestionpolizas.domain.model.Poliza;
import com.gestionpolizas.domain.model.Riesgo;
import com.gestionpolizas.domain.model.TipoPoliza;

import java.util.List;

public interface GestionPolizaUseCase {

    List<Poliza> listar(TipoPoliza tipo, EstadoPoliza estado);

    List<Riesgo> listarRiesgos(Long polizaId);

    Poliza renovar(Long polizaId);

    Poliza cancelar(Long polizaId, String motivo);
}
