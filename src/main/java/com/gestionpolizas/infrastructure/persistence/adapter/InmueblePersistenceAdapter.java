package com.gestionpolizas.infrastructure.persistence.adapter;

import com.gestionpolizas.domain.model.Inmueble;
import com.gestionpolizas.domain.port.out.InmuebleRepositoryPort;
import com.gestionpolizas.infrastructure.persistence.mapper.PersistenceMapper;
import com.gestionpolizas.infrastructure.persistence.repository.InmuebleJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class InmueblePersistenceAdapter implements InmuebleRepositoryPort {

    private final InmuebleJpaRepository inmuebleJpaRepository;
    private final PersistenceMapper mapper;

    public InmueblePersistenceAdapter(InmuebleJpaRepository inmuebleJpaRepository, PersistenceMapper mapper) {
        this.inmuebleJpaRepository = inmuebleJpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Inmueble> buscarPorMatricula(String matriculaInmobiliaria) {
        return inmuebleJpaRepository.findByMatriculaInmobiliariaIgnoreCase(matriculaInmobiliaria)
                .map(mapper::toDomain);
    }

    @Override
    public Inmueble guardar(Inmueble inmueble) {
        return mapper.toDomain(inmuebleJpaRepository.save(mapper.toEntity(inmueble)));
    }
}
