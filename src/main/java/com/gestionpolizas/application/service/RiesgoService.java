package com.gestionpolizas.application.service;

import com.gestionpolizas.domain.exception.RecursoNoEncontradoException;
import com.gestionpolizas.domain.model.AccionEdicion;
import com.gestionpolizas.domain.model.EdicionCore;
import com.gestionpolizas.domain.model.Inmueble;
import com.gestionpolizas.domain.model.Poliza;
import com.gestionpolizas.domain.model.Riesgo;
import com.gestionpolizas.domain.model.Tercero;
import com.gestionpolizas.domain.port.in.GestionRiesgoUseCase;
import com.gestionpolizas.domain.port.in.NuevoRiesgoCommand;
import com.gestionpolizas.domain.port.out.CoreEdicionPort;
import com.gestionpolizas.domain.port.out.InmuebleRepositoryPort;
import com.gestionpolizas.domain.port.out.PolizaRepositoryPort;
import com.gestionpolizas.domain.port.out.TerceroRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@Transactional
public class RiesgoService implements GestionRiesgoUseCase {

    private static final String MOTIVO_CANCELACION_RIESGO = "Solicitud del tomador";

    private final PolizaRepositoryPort polizaRepository;
    private final InmuebleRepositoryPort inmuebleRepository;
    private final TerceroRepositoryPort terceroRepository;
    private final CoreEdicionPort coreEdicionPort;
    private final Clock clock;

    public RiesgoService(PolizaRepositoryPort polizaRepository, InmuebleRepositoryPort inmuebleRepository,
                         TerceroRepositoryPort terceroRepository, CoreEdicionPort coreEdicionPort, Clock clock) {
        this.polizaRepository = polizaRepository;
        this.inmuebleRepository = inmuebleRepository;
        this.terceroRepository = terceroRepository;
        this.coreEdicionPort = coreEdicionPort;
        this.clock = clock;
    }

    @Override
    public Riesgo agregar(Long polizaId, NuevoRiesgoCommand comando) {
        Poliza poliza = polizaRepository.buscarPorId(polizaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la póliza " + polizaId));
        Inmueble inmueble = resolverInmueble(comando.inmueble());
        poliza.agregarRiesgo(
                inmueble,
                resolverTercero(comando.asegurado()),
                resolverTercero(comando.beneficiario()),
                comando.canonMensual(),
                LocalDate.now(clock));
        Riesgo riesgo = polizaRepository.guardar(poliza).riesgoActivoDelInmueble(inmueble).orElseThrow();
        coreEdicionPort.registrarEdicion(
                EdicionCore.deRiesgo(riesgo.getId(), AccionEdicion.INCLUIR_RIESGO, LocalDateTime.now(clock)));
        return riesgo;
    }

    @Override
    public Riesgo cancelar(Long riesgoId, String motivo) {
        Poliza poliza = polizaRepository.buscarPorRiesgoId(riesgoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el riesgo " + riesgoId));
        Riesgo riesgo = poliza.cancelarRiesgo(riesgoId, LocalDate.now(clock), motivoOPorDefecto(motivo));
        polizaRepository.guardar(poliza);
        coreEdicionPort.registrarEdicion(
                EdicionCore.deRiesgo(riesgoId, AccionEdicion.EXCLUIR_RIESGO, LocalDateTime.now(clock)));
        return riesgo;
    }

    private Inmueble resolverInmueble(Inmueble inmueble) {
        return inmuebleRepository.buscarPorMatricula(inmueble.matriculaInmobiliaria())
                .orElseGet(() -> inmuebleRepository.guardar(inmueble));
    }

    private Tercero resolverTercero(Tercero tercero) {
        return terceroRepository.buscarPorDocumento(tercero.tipoDocumento(), tercero.numeroDocumento())
                .orElseGet(() -> terceroRepository.guardar(tercero));
    }

    private String motivoOPorDefecto(String motivo) {
        return motivo == null || motivo.isBlank() ? MOTIVO_CANCELACION_RIESGO : motivo.trim();
    }
}
