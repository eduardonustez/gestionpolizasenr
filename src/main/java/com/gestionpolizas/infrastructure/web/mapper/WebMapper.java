package com.gestionpolizas.infrastructure.web.mapper;

import com.gestionpolizas.domain.model.Inmueble;
import com.gestionpolizas.domain.model.Poliza;
import com.gestionpolizas.domain.model.Riesgo;
import com.gestionpolizas.domain.model.Tercero;
import com.gestionpolizas.domain.port.in.NuevoRiesgoCommand;
import com.gestionpolizas.infrastructure.web.dto.InmuebleRequest;
import com.gestionpolizas.infrastructure.web.dto.InmuebleResponse;
import com.gestionpolizas.infrastructure.web.dto.NuevoRiesgoRequest;
import com.gestionpolizas.infrastructure.web.dto.PolizaResponse;
import com.gestionpolizas.infrastructure.web.dto.RiesgoResponse;
import com.gestionpolizas.infrastructure.web.dto.TerceroRequest;
import com.gestionpolizas.infrastructure.web.dto.TerceroResponse;

public final class WebMapper {

    private WebMapper() {
    }

    public static PolizaResponse toResponse(Poliza poliza) {
        return new PolizaResponse(
                poliza.getId(),
                poliza.getNumeroPoliza(),
                poliza.getTipo(),
                poliza.getTomadorId(),
                poliza.getFechaInicio(),
                poliza.getFechaFin(),
                poliza.getMesesVigencia(),
                poliza.getCanonMensual(),
                poliza.getValorPrima(),
                poliza.getEstado(),
                poliza.isRenovacionAutomatica(),
                poliza.getNumeroRenovacion(),
                poliza.getEstadoSyncCore(),
                poliza.riesgosActivos().size());
    }

    public static RiesgoResponse toResponse(Riesgo riesgo) {
        return new RiesgoResponse(
                riesgo.getId(),
                riesgo.getPolizaId(),
                toResponse(riesgo.getInmueble()),
                toResponse(riesgo.getAsegurado()),
                toResponse(riesgo.getBeneficiario()),
                riesgo.getCanonMensual(),
                riesgo.getValorPrima(),
                riesgo.getFechaInclusion(),
                riesgo.getFechaCancelacion(),
                riesgo.getMotivoCancelacion(),
                riesgo.getEstado());
    }

    public static NuevoRiesgoCommand toCommand(NuevoRiesgoRequest request) {
        return new NuevoRiesgoCommand(
                toDomain(request.inmueble()),
                toDomain(request.asegurado()),
                toDomain(request.beneficiario()),
                request.canonMensual());
    }

    private static InmuebleResponse toResponse(Inmueble inmueble) {
        return new InmuebleResponse(inmueble.id(), inmueble.matriculaInmobiliaria(), inmueble.direccion(),
                inmueble.ciudad(), inmueble.tipo());
    }

    private static TerceroResponse toResponse(Tercero tercero) {
        return new TerceroResponse(tercero.id(), tercero.tipoDocumento(), tercero.numeroDocumento(),
                tercero.nombreRazonSocial());
    }

    private static Inmueble toDomain(InmuebleRequest request) {
        return new Inmueble(null, request.matriculaInmobiliaria().trim(), request.direccion().trim(),
                request.ciudad().trim(), request.tipo());
    }

    private static Tercero toDomain(TerceroRequest request) {
        return new Tercero(null, request.tipoDocumento(), request.numeroDocumento().trim(), request.tipoPersona(),
                request.nombreRazonSocial().trim(), request.email(), request.celular(),
                !Boolean.FALSE.equals(request.aceptaNotificaciones()));
    }
}
