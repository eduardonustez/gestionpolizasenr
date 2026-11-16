package com.gestionpolizas.domain.port.out;

import com.gestionpolizas.domain.model.Inmueble;

import java.util.Optional;

public interface InmuebleRepositoryPort {

    Optional<Inmueble> buscarPorMatricula(String matriculaInmobiliaria);

    Inmueble guardar(Inmueble inmueble);
}
