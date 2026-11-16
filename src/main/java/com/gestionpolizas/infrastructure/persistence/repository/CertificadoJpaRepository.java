package com.gestionpolizas.infrastructure.persistence.repository;

import com.gestionpolizas.infrastructure.persistence.entity.CertificadoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CertificadoJpaRepository extends JpaRepository<CertificadoEntity, Long> {
}
