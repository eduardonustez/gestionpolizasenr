package com.gestionpolizas.infrastructure.persistence.repository;

import com.gestionpolizas.domain.model.TipoDocumento;
import com.gestionpolizas.infrastructure.persistence.entity.TerceroEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TerceroJpaRepository extends JpaRepository<TerceroEntity, Long> {

    Optional<TerceroEntity> findByTipoDocumentoAndNumeroDocumento(TipoDocumento tipoDocumento, String numeroDocumento);
}
