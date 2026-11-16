package com.gestionpolizas.application.service;

import com.gestionpolizas.domain.exception.RecursoNoEncontradoException;
import com.gestionpolizas.domain.model.AccionEdicion;
import com.gestionpolizas.domain.model.Certificado;
import com.gestionpolizas.domain.model.EdicionCore;
import com.gestionpolizas.domain.model.EstadoPoliza;
import com.gestionpolizas.domain.model.Poliza;
import com.gestionpolizas.domain.model.Riesgo;
import com.gestionpolizas.domain.model.TipoPoliza;
import com.gestionpolizas.domain.port.in.GestionPolizaUseCase;
import com.gestionpolizas.domain.port.out.CertificadoRepositoryPort;
import com.gestionpolizas.domain.port.out.CoreEdicionPort;
import com.gestionpolizas.domain.port.out.IpcPort;
import com.gestionpolizas.domain.port.out.PolizaRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class PolizaService implements GestionPolizaUseCase {

    private final PolizaRepositoryPort polizaRepository;
    private final CertificadoRepositoryPort certificadoRepository;
    private final IpcPort ipcPort;
    private final CoreEdicionPort coreEdicionPort;
    private final Clock clock;

    public PolizaService(PolizaRepositoryPort polizaRepository, CertificadoRepositoryPort certificadoRepository,
                         IpcPort ipcPort, CoreEdicionPort coreEdicionPort, Clock clock) {
        this.polizaRepository = polizaRepository;
        this.certificadoRepository = certificadoRepository;
        this.ipcPort = ipcPort;
        this.coreEdicionPort = coreEdicionPort;
        this.clock = clock;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Poliza> listar(TipoPoliza tipo, EstadoPoliza estado) {
        return polizaRepository.buscar(tipo, estado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Riesgo> listarRiesgos(Long polizaId) {
        return obtenerPoliza(polizaId).getRiesgos();
    }

    @Override
    public Poliza renovar(Long polizaId) {
        Poliza poliza = obtenerPoliza(polizaId);
        Certificado certificado = poliza.renovar(ipcPort.obtenerIpcVigente(), LocalDateTime.now(clock));
        Poliza renovada = polizaRepository.guardar(poliza);
        certificadoRepository.guardar(certificado);
        coreEdicionPort.registrarEdicion(
                EdicionCore.dePoliza(polizaId, AccionEdicion.RENOVAR_POLIZA, LocalDateTime.now(clock)));
        return renovada;
    }

    @Override
    public Poliza cancelar(Long polizaId, String motivo) {
        Poliza poliza = obtenerPoliza(polizaId);
        poliza.cancelar(LocalDate.now(clock), motivoOPorDefecto(motivo));
        Poliza cancelada = polizaRepository.guardar(poliza);
        coreEdicionPort.registrarEdicion(
                EdicionCore.dePoliza(polizaId, AccionEdicion.CANCELAR_POLIZA, LocalDateTime.now(clock)));
        return cancelada;
    }

    private Poliza obtenerPoliza(Long polizaId) {
        return polizaRepository.buscarPorId(polizaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la póliza " + polizaId));
    }

    private String motivoOPorDefecto(String motivo) {
        return motivo == null || motivo.isBlank() ? Poliza.MOTIVO_CANCELACION_POLIZA : motivo.trim();
    }
}
