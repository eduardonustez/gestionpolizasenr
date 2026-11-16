package com.gestionpolizas.infrastructure.persistence.adapter;

import com.gestionpolizas.domain.model.Certificado;
import com.gestionpolizas.domain.port.out.CertificadoRepositoryPort;
import com.gestionpolizas.infrastructure.persistence.mapper.PersistenceMapper;
import com.gestionpolizas.infrastructure.persistence.repository.CertificadoJpaRepository;
import org.springframework.stereotype.Component;

@Component
public class CertificadoPersistenceAdapter implements CertificadoRepositoryPort {

    private final CertificadoJpaRepository certificadoJpaRepository;
    private final PersistenceMapper mapper;

    public CertificadoPersistenceAdapter(CertificadoJpaRepository certificadoJpaRepository, PersistenceMapper mapper) {
        this.certificadoJpaRepository = certificadoJpaRepository;
        this.mapper = mapper;
    }

    @Override
    public void guardar(Certificado certificado) {
        certificadoJpaRepository.save(mapper.toEntity(certificado));
    }
}
