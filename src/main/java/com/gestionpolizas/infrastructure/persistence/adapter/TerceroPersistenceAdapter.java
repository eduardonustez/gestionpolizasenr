package com.gestionpolizas.infrastructure.persistence.adapter;

import com.gestionpolizas.domain.model.Tercero;
import com.gestionpolizas.domain.model.TipoDocumento;
import com.gestionpolizas.domain.port.out.TerceroRepositoryPort;
import com.gestionpolizas.infrastructure.persistence.mapper.PersistenceMapper;
import com.gestionpolizas.infrastructure.persistence.repository.TerceroJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class TerceroPersistenceAdapter implements TerceroRepositoryPort {

    private final TerceroJpaRepository terceroJpaRepository;
    private final PersistenceMapper mapper;

    public TerceroPersistenceAdapter(TerceroJpaRepository terceroJpaRepository, PersistenceMapper mapper) {
        this.terceroJpaRepository = terceroJpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Tercero> buscarPorDocumento(TipoDocumento tipoDocumento, String numeroDocumento) {
        return terceroJpaRepository.findByTipoDocumentoAndNumeroDocumento(tipoDocumento, numeroDocumento)
                .map(mapper::toDomain);
    }

    @Override
    public Tercero guardar(Tercero tercero) {
        return mapper.toDomain(terceroJpaRepository.save(mapper.toEntity(tercero)));
    }
}
