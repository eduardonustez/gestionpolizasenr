package com.gestionpolizas.infrastructure.persistence.adapter;

import com.gestionpolizas.domain.exception.RecursoNoEncontradoException;
import com.gestionpolizas.domain.model.EstadoPoliza;
import com.gestionpolizas.domain.model.Poliza;
import com.gestionpolizas.domain.model.Riesgo;
import com.gestionpolizas.domain.model.TipoPoliza;
import com.gestionpolizas.domain.port.out.PolizaRepositoryPort;
import com.gestionpolizas.infrastructure.persistence.entity.PolizaEntity;
import com.gestionpolizas.infrastructure.persistence.entity.RiesgoEntity;
import com.gestionpolizas.infrastructure.persistence.mapper.PersistenceMapper;
import com.gestionpolizas.infrastructure.persistence.repository.InmuebleJpaRepository;
import com.gestionpolizas.infrastructure.persistence.repository.PolizaJpaRepository;
import com.gestionpolizas.infrastructure.persistence.repository.RiesgoJpaRepository;
import com.gestionpolizas.infrastructure.persistence.repository.TerceroJpaRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class PolizaPersistenceAdapter implements PolizaRepositoryPort {

    private final PolizaJpaRepository polizaJpaRepository;
    private final RiesgoJpaRepository riesgoJpaRepository;
    private final InmuebleJpaRepository inmuebleJpaRepository;
    private final TerceroJpaRepository terceroJpaRepository;
    private final PersistenceMapper mapper;

    public PolizaPersistenceAdapter(PolizaJpaRepository polizaJpaRepository, RiesgoJpaRepository riesgoJpaRepository,
                                    InmuebleJpaRepository inmuebleJpaRepository,
                                    TerceroJpaRepository terceroJpaRepository, PersistenceMapper mapper) {
        this.polizaJpaRepository = polizaJpaRepository;
        this.riesgoJpaRepository = riesgoJpaRepository;
        this.inmuebleJpaRepository = inmuebleJpaRepository;
        this.terceroJpaRepository = terceroJpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Poliza> buscarPorId(Long id) {
        return polizaJpaRepository.findWithRiesgosById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Poliza> buscarPorRiesgoId(Long riesgoId) {
        return riesgoJpaRepository.findPolizaIdByRiesgoId(riesgoId).flatMap(this::buscarPorId);
    }

    @Override
    public List<Poliza> buscar(TipoPoliza tipo, EstadoPoliza estado) {
        Specification<PolizaEntity> filtro = (root, query, builder) -> {
            List<Predicate> condiciones = new ArrayList<>();
            if (tipo != null) {
                condiciones.add(builder.equal(root.get("tipo"), tipo));
            }
            if (estado != null) {
                condiciones.add(builder.equal(root.get("estado"), estado));
            }
            return builder.and(condiciones.toArray(Predicate[]::new));
        };
        return polizaJpaRepository.findAll(filtro, Sort.by("numeroPoliza")).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Poliza guardar(Poliza poliza) {
        PolizaEntity entity = polizaJpaRepository.findWithRiesgosById(poliza.getId())
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la póliza " + poliza.getId()));
        mapper.actualizarEntity(entity, poliza);
        Map<Long, RiesgoEntity> existentes = entity.getRiesgos().stream()
                .collect(Collectors.toMap(RiesgoEntity::getId, Function.identity()));
        for (Riesgo riesgo : poliza.getRiesgos()) {
            RiesgoEntity riesgoEntity = existentes.get(riesgo.getId());
            if (riesgoEntity == null) {
                RiesgoEntity nuevo = nuevoRiesgo(entity, riesgo);
                riesgoJpaRepository.save(nuevo);
                entity.getRiesgos().add(nuevo);
            } else {
                mapper.actualizarEntity(riesgoEntity, riesgo);
            }
        }
        polizaJpaRepository.flush();
        return mapper.toDomain(entity);
    }

    private RiesgoEntity nuevoRiesgo(PolizaEntity poliza, Riesgo riesgo) {
        RiesgoEntity entity = new RiesgoEntity();
        entity.setPoliza(poliza);
        entity.setInmueble(inmuebleJpaRepository.getReferenceById(riesgo.getInmueble().id()));
        entity.setAsegurado(terceroJpaRepository.getReferenceById(riesgo.getAsegurado().id()));
        entity.setBeneficiario(terceroJpaRepository.getReferenceById(riesgo.getBeneficiario().id()));
        entity.setFechaInclusion(riesgo.getFechaInclusion());
        mapper.actualizarEntity(entity, riesgo);
        return entity;
    }
}
