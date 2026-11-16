package com.gestionpolizas.domain.port.out;

import com.gestionpolizas.domain.model.Certificado;

public interface CertificadoRepositoryPort {

    void guardar(Certificado certificado);
}
