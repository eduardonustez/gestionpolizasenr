package com.gestionpolizas.infrastructure.persistence.repository;

import com.gestionpolizas.infrastructure.persistence.entity.InmuebleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InmuebleJpaRepository extends JpaRepository<InmuebleEntity, Long> {

    Optional<InmuebleEntity> findByMatriculaInmobiliariaIgnoreCase(String matriculaInmobiliaria);
}
