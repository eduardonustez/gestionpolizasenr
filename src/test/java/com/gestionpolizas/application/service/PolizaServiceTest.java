package com.gestionpolizas.application.service;

import com.gestionpolizas.domain.exception.RecursoNoEncontradoException;
import com.gestionpolizas.domain.exception.ReglaNegocioException;
import com.gestionpolizas.domain.model.AccionEdicion;
import com.gestionpolizas.domain.model.Certificado;
import com.gestionpolizas.domain.model.EdicionCore;
import com.gestionpolizas.domain.model.EstadoPoliza;
import com.gestionpolizas.domain.model.Poliza;
import com.gestionpolizas.domain.model.PolizaFixture;
import com.gestionpolizas.domain.port.out.CertificadoRepositoryPort;
import com.gestionpolizas.domain.port.out.CoreEdicionPort;
import com.gestionpolizas.domain.port.out.IpcPort;
import com.gestionpolizas.domain.port.out.PolizaRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PolizaServiceTest {

    @Mock
    private PolizaRepositoryPort polizaRepository;

    @Mock
    private CertificadoRepositoryPort certificadoRepository;

    @Mock
    private IpcPort ipcPort;

    @Mock
    private CoreEdicionPort coreEdicionPort;

    private PolizaService service;

    @BeforeEach
    void configurar() {
        Clock clock = Clock.fixed(Instant.parse("2026-11-16T15:00:00Z"), ZoneId.of("America/Bogota"));
        service = new PolizaService(polizaRepository, certificadoRepository, ipcPort, coreEdicionPort, clock);
    }

    @Test
    void renovarGuardaPolizaCertificadoYNotificaAlCore() {
        Poliza poliza = PolizaFixture.individualVigente();
        when(polizaRepository.buscarPorId(poliza.getId())).thenReturn(Optional.of(poliza));
        when(ipcPort.obtenerIpcVigente()).thenReturn(new BigDecimal("0.0520"));
        when(polizaRepository.guardar(poliza)).thenReturn(poliza);

        Poliza resultado = service.renovar(poliza.getId());

        assertThat(resultado.getEstado()).isEqualTo(EstadoPoliza.RENOVADA);
        ArgumentCaptor<Certificado> certificado = ArgumentCaptor.forClass(Certificado.class);
        verify(certificadoRepository).guardar(certificado.capture());
        assertThat(certificado.getValue().polizaId()).isEqualTo(poliza.getId());
        ArgumentCaptor<EdicionCore> edicion = ArgumentCaptor.forClass(EdicionCore.class);
        verify(coreEdicionPort).registrarEdicion(edicion.capture());
        assertThat(edicion.getValue().accion()).isEqualTo(AccionEdicion.RENOVAR_POLIZA);
    }

    @Test
    void renovarPolizaCanceladaNoGuardaNiNotifica() {
        Poliza poliza = PolizaFixture.individualCancelada();
        when(polizaRepository.buscarPorId(poliza.getId())).thenReturn(Optional.of(poliza));
        when(ipcPort.obtenerIpcVigente()).thenReturn(new BigDecimal("0.0520"));

        assertThatThrownBy(() -> service.renovar(poliza.getId())).isInstanceOf(ReglaNegocioException.class);

        verify(polizaRepository, never()).guardar(any());
        verifyNoInteractions(certificadoRepository, coreEdicionPort);
    }

    @Test
    void cancelarUsaMotivoPorDefectoCuandoNoSeEnvia() {
        Poliza poliza = PolizaFixture.colectivaVigente();
        when(polizaRepository.buscarPorId(poliza.getId())).thenReturn(Optional.of(poliza));
        when(polizaRepository.guardar(poliza)).thenReturn(poliza);

        Poliza resultado = service.cancelar(poliza.getId(), " ");

        assertThat(resultado.getEstado()).isEqualTo(EstadoPoliza.CANCELADA);
        assertThat(resultado.getRiesgos())
                .allSatisfy(riesgo -> assertThat(riesgo.getMotivoCancelacion())
                        .isEqualTo(Poliza.MOTIVO_CANCELACION_POLIZA));
        verify(coreEdicionPort).registrarEdicion(any());
    }

    @Test
    void consultarRiesgosDePolizaInexistenteLanzaNoEncontrado() {
        Long id = 999L;
        when(polizaRepository.buscarPorId(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.listarRiesgos(id))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessage("No existe la póliza " + id);
    }
}
