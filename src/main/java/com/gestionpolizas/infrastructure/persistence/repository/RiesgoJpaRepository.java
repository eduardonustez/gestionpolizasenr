package com.gestionpolizas.infrastructure.persistence.repository;

import com.gestionpolizas.infrastructure.persistence.entity.RiesgoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RiesgoJpaRepository extends JpaRepository<RiesgoEntity, Long> {

    @Query("select r.poliza.id from RiesgoEntity r where r.id = :riesgoId")
    Optional<Long> findPolizaIdByRiesgoId(@Param("riesgoId") Long riesgoId);
}
