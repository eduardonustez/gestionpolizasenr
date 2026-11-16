package com.gestionpolizas.domain.port.out;

import com.gestionpolizas.domain.model.Tercero;
import com.gestionpolizas.domain.model.TipoDocumento;

import java.util.Optional;

public interface TerceroRepositoryPort {

    Optional<Tercero> buscarPorDocumento(TipoDocumento tipoDocumento, String numeroDocumento);

    Tercero guardar(Tercero tercero);
}
