package com.gestionpolizas.infrastructure.persistence.mapper;

import com.gestionpolizas.domain.model.Certificado;
import com.gestionpolizas.domain.model.Inmueble;
import com.gestionpolizas.domain.model.Poliza;
import com.gestionpolizas.domain.model.Riesgo;
import com.gestionpolizas.domain.model.Tercero;
import com.gestionpolizas.infrastructure.persistence.entity.CertificadoEntity;
import com.gestionpolizas.infrastructure.persistence.entity.InmuebleEntity;
import com.gestionpolizas.infrastructure.persistence.entity.PolizaEntity;
import com.gestionpolizas.infrastructure.persistence.entity.RiesgoEntity;
import com.gestionpolizas.infrastructure.persistence.entity.TerceroEntity;
import org.springframework.stereotype.Component;

@Component
public class PersistenceMapper {

    public Poliza toDomain(PolizaEntity entity) {
        return new Poliza(
                entity.getId(),
                entity.getNumeroPoliza(),
                entity.getTipo(),
                entity.getTomadorId(),
                entity.getFechaInicio(),
                entity.getFechaFin(),
                entity.getMesesVigencia(),
                entity.getCanonMensual(),
                entity.getValorPrima(),
                entity.getEstado(),
                entity.isRenovacionAutomatica(),
                entity.getNumeroRenovacion(),
                entity.getEstadoSyncCore(),
                entity.getNumeroPolizaCore(),
                entity.getVersion(),
                entity.getRiesgos().stream().map(riesgo -> toDomain(riesgo, entity)).toList());
    }

    public Inmueble toDomain(InmuebleEntity entity) {
        return new Inmueble(entity.getId(), entity.getMatriculaInmobiliaria(), entity.getDireccion(),
                entity.getCiudad(), entity.getTipo());
    }

    public Tercero toDomain(TerceroEntity entity) {
        return new Tercero(entity.getId(), entity.getTipoDocumento(), entity.getNumeroDocumento(),
                entity.getTipoPersona(), entity.getNombreRazonSocial(), entity.getEmail(), entity.getCelular(),
                entity.isAceptaNotificaciones());
    }

    public InmuebleEntity toEntity(Inmueble inmueble) {
        InmuebleEntity entity = new InmuebleEntity();
        entity.setId(inmueble.id());
        entity.setMatriculaInmobiliaria(inmueble.matriculaInmobiliaria());
        entity.setDireccion(inmueble.direccion());
        entity.setCiudad(inmueble.ciudad());
        entity.setTipo(inmueble.tipo());
        return entity;
    }

    public TerceroEntity toEntity(Tercero tercero) {
        TerceroEntity entity = new TerceroEntity();
        entity.setId(tercero.id());
        entity.setTipoDocumento(tercero.tipoDocumento());
        entity.setNumeroDocumento(tercero.numeroDocumento());
        entity.setTipoPersona(tercero.tipoPersona());
        entity.setNombreRazonSocial(tercero.nombreRazonSocial());
        entity.setEmail(tercero.email());
        entity.setCelular(tercero.celular());
        entity.setAceptaNotificaciones(tercero.aceptaNotificaciones());
        return entity;
    }

    public CertificadoEntity toEntity(Certificado certificado) {
        CertificadoEntity entity = new CertificadoEntity();
        entity.setId(certificado.id());
        entity.setPolizaId(certificado.polizaId());
        entity.setNumeroCertificado(certificado.numeroCertificado());
        entity.setTipoMovimiento(certificado.tipoMovimiento());
        entity.setFechaInicio(certificado.fechaInicio());
        entity.setFechaFin(certificado.fechaFin());
        entity.setCanonMensual(certificado.canonMensual());
        entity.setValorPrima(certificado.valorPrima());
        entity.setIpcAplicado(certificado.ipcAplicado());
        entity.setFechaRegistro(certificado.fechaRegistro());
        return entity;
    }

    public void actualizarEntity(PolizaEntity entity, Poliza poliza) {
        entity.setFechaInicio(poliza.getFechaInicio());
        entity.setFechaFin(poliza.getFechaFin());
        entity.setCanonMensual(poliza.getCanonMensual());
        entity.setValorPrima(poliza.getValorPrima());
        entity.setEstado(poliza.getEstado());
        entity.setNumeroRenovacion(poliza.getNumeroRenovacion());
        entity.setEstadoSyncCore(poliza.getEstadoSyncCore());
    }

    public void actualizarEntity(RiesgoEntity entity, Riesgo riesgo) {
        entity.setCanonMensual(riesgo.getCanonMensual());
        entity.setValorPrima(riesgo.getValorPrima());
        entity.setFechaCancelacion(riesgo.getFechaCancelacion());
        entity.setMotivoCancelacion(riesgo.getMotivoCancelacion());
        entity.setEstado(riesgo.getEstado());
        entity.setEstadoSyncCore(riesgo.getEstadoSyncCore());
    }

    private Riesgo toDomain(RiesgoEntity entity, PolizaEntity poliza) {
        return new Riesgo(
                entity.getId(),
                poliza.getId(),
                toDomain(entity.getInmueble()),
                toDomain(entity.getAsegurado()),
                toDomain(entity.getBeneficiario()),
                entity.getCanonMensual(),
                entity.getValorPrima(),
                entity.getFechaInclusion(),
                entity.getFechaCancelacion(),
                entity.getMotivoCancelacion(),
                entity.getEstado(),
                entity.getEstadoSyncCore(),
                entity.getVersion());
    }
}
