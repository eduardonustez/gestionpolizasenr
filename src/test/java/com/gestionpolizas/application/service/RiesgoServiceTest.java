package com.gestionpolizas.application.service;

import com.gestionpolizas.domain.exception.RecursoNoEncontradoException;
import com.gestionpolizas.domain.exception.ReglaNegocioException;
import com.gestionpolizas.domain.model.AccionEdicion;
import com.gestionpolizas.domain.model.EdicionCore;
import com.gestionpolizas.domain.model.EstadoRiesgo;
import com.gestionpolizas.domain.model.Inmueble;
import com.gestionpolizas.domain.model.Poliza;
import com.gestionpolizas.domain.model.PolizaFixture;
import com.gestionpolizas.domain.model.Riesgo;
import com.gestionpolizas.domain.model.Tercero;
import com.gestionpolizas.domain.model.TipoDocumento;
import com.gestionpolizas.domain.model.TipoInmueble;
import com.gestionpolizas.domain.model.TipoPersona;
import com.gestionpolizas.domain.port.in.NuevoRiesgoCommand;
import com.gestionpolizas.domain.port.out.CoreEdicionPort;
import com.gestionpolizas.domain.port.out.InmuebleRepositoryPort;
import com.gestionpolizas.domain.port.out.PolizaRepositoryPort;
import com.gestionpolizas.domain.port.out.TerceroRepositoryPort;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RiesgoServiceTest {

    @Mock
    private PolizaRepositoryPort polizaRepository;

    @Mock
    private InmuebleRepositoryPort inmuebleRepository;

    @Mock
    private TerceroRepositoryPort terceroRepository;

    @Mock
    private CoreEdicionPort coreEdicionPort;

    private RiesgoService service;

    @BeforeEach
    void configurar() {
        Clock clock = Clock.fixed(Instant.parse("2026-11-16T15:00:00Z"), ZoneId.of("America/Bogota"));
        service = new RiesgoService(polizaRepository, inmuebleRepository, terceroRepository, coreEdicionPort, clock);
    }

    @Test
    void agregarRiesgoReutilizaTercerosExistentesYCreaInmuebleNuevo() {
        Poliza poliza = PolizaFixture.colectivaVigente();
        Tercero asegurado = PolizaFixture.tercero("1012345678");
        Tercero beneficiario = PolizaFixture.tercero("79876543");
        when(polizaRepository.buscarPorId(poliza.getId())).thenReturn(Optional.of(poliza));
        when(inmuebleRepository.buscarPorMatricula("50C-7777")).thenReturn(Optional.empty());
        when(inmuebleRepository.guardar(any())).thenReturn(PolizaFixture.inmueble("50C-7777"));
        when(terceroRepository.buscarPorDocumento(TipoDocumento.CC, "1012345678")).thenReturn(Optional.of(asegurado));
        when(terceroRepository.buscarPorDocumento(TipoDocumento.CC, "79876543")).thenReturn(Optional.of(beneficiario));
        when(polizaRepository.guardar(poliza)).thenReturn(poliza);

        Riesgo riesgo = service.agregar(poliza.getId(), comando("1012345678", "79876543"));

        assertThat(riesgo.getEstado()).isEqualTo(EstadoRiesgo.ACTIVO);
        assertThat(riesgo.getAsegurado()).isEqualTo(asegurado);
        assertThat(riesgo.getInmueble().id()).isNotNull();
        verify(terceroRepository, never()).guardar(any());
        verify(polizaRepository).guardar(poliza);
        ArgumentCaptor<EdicionCore> edicion = ArgumentCaptor.forClass(EdicionCore.class);
        verify(coreEdicionPort).registrarEdicion(edicion.capture());
        assertThat(edicion.getValue().accion()).isEqualTo(AccionEdicion.INCLUIR_RIESGO);
        assertThat(edicion.getValue().entidadId()).isEqualTo(riesgo.getId());
    }

    @Test
    void agregarRiesgoAPolizaIndividualNoGuardaCambios() {
        Poliza poliza = PolizaFixture.individualVigente();
        when(polizaRepository.buscarPorId(poliza.getId())).thenReturn(Optional.of(poliza));
        when(inmuebleRepository.buscarPorMatricula(any())).thenReturn(Optional.of(PolizaFixture.inmueble("50C-7777")));
        when(terceroRepository.buscarPorDocumento(any(), any())).thenReturn(Optional.of(PolizaFixture.tercero("1")));

        assertThatThrownBy(() -> service.agregar(poliza.getId(), comando("1", "2")))
                .isInstanceOf(ReglaNegocioException.class);

        verify(polizaRepository, never()).guardar(any());
        verify(coreEdicionPort, never()).registrarEdicion(any());
    }

    @Test
    void cancelarRiesgoInexistenteLanzaNoEncontrado() {
        Long riesgoId = 999L;
        when(polizaRepository.buscarPorRiesgoId(riesgoId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.cancelar(riesgoId, null))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessage("No existe el riesgo " + riesgoId);
    }

    @Test
    void cancelarRiesgoNotificaExclusionAlCore() {
        Poliza poliza = PolizaFixture.colectivaVigente();
        Long riesgoId = poliza.riesgosActivos().getFirst().getId();
        when(polizaRepository.buscarPorRiesgoId(riesgoId)).thenReturn(Optional.of(poliza));

        Riesgo riesgo = service.cancelar(riesgoId, "Terminación del contrato");

        assertThat(riesgo.getEstado()).isEqualTo(EstadoRiesgo.CANCELADO);
        assertThat(riesgo.getMotivoCancelacion()).isEqualTo("Terminación del contrato");
        verify(polizaRepository).guardar(poliza);
        ArgumentCaptor<EdicionCore> edicion = ArgumentCaptor.forClass(EdicionCore.class);
        verify(coreEdicionPort).registrarEdicion(edicion.capture());
        assertThat(edicion.getValue().accion()).isEqualTo(AccionEdicion.EXCLUIR_RIESGO);
    }

    private NuevoRiesgoCommand comando(String documentoAsegurado, String documentoBeneficiario) {
        return new NuevoRiesgoCommand(
                new Inmueble(null, "50C-7777", "Calle 80 # 10-20", "Bogotá", TipoInmueble.VIVIENDA),
                new Tercero(null, TipoDocumento.CC, documentoAsegurado, TipoPersona.NATURAL, "Asegurado", null, null,
                        true),
                new Tercero(null, TipoDocumento.CC, documentoBeneficiario, TipoPersona.NATURAL, "Beneficiario", null,
                        null, true),
                new BigDecimal("1200000"));
    }
}
