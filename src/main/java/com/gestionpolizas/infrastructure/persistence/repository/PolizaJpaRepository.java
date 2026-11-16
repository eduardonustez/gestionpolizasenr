package com.gestionpolizas.infrastructure.persistence.repository;

import com.gestionpolizas.infrastructure.persistence.entity.PolizaEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface PolizaJpaRepository extends JpaRepository<PolizaEntity, Long>, JpaSpecificationExecutor<PolizaEntity> {

    @EntityGraph(attributePaths = {"riesgos", "riesgos.inmueble", "riesgos.asegurado", "riesgos.beneficiario"})
    Optional<PolizaEntity> findWithRiesgosById(Long id);

    @Override
    @EntityGraph(attributePaths = {"riesgos", "riesgos.inmueble", "riesgos.asegurado", "riesgos.beneficiario"})
    List<PolizaEntity> findAll(Specification<PolizaEntity> specification, Sort sort);
}
