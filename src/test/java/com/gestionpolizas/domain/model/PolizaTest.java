package com.gestionpolizas.domain.model;

import com.gestionpolizas.domain.exception.RecursoNoEncontradoException;
import com.gestionpolizas.domain.exception.ReglaNegocioException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PolizaTest {

    private static final BigDecimal IPC = new BigDecimal("0.0520");
    private static final LocalDate HOY = LocalDate.of(2026, 11, 16);
    private static final LocalDateTime AHORA = LocalDateTime.of(2026, 11, 16, 10, 0);

    @Test
    void renovarIncrementaCanonYPrimaConIpcYCambiaEstadoARenovada() {
        Poliza poliza = PolizaFixture.individualVigente();

        Certificado certificado = poliza.renovar(IPC, AHORA);

        assertThat(poliza.getEstado()).isEqualTo(EstadoPoliza.RENOVADA);
        assertThat(poliza.getCanonMensual()).isEqualByComparingTo("2104000.00");
        assertThat(poliza.getValorPrima()).isEqualByComparingTo("25248000.00");
        assertThat(poliza.getFechaInicio()).isEqualTo(LocalDate.of(2027, 1, 1));
        assertThat(poliza.getFechaFin()).isEqualTo(LocalDate.of(2028, 1, 1));
        assertThat(poliza.getNumeroRenovacion()).isEqualTo(1);
        assertThat(poliza.getEstadoSyncCore()).isEqualTo(EstadoSyncCore.PENDIENTE);
        assertThat(certificado.tipoMovimiento()).isEqualTo(TipoMovimiento.RENOVACION);
        assertThat(certificado.numeroCertificado()).isEqualTo(2);
        assertThat(certificado.ipcAplicado()).isEqualByComparingTo(IPC);
    }

    @Test
    void renovarColectivaAjustaCadaRiesgoActivoYRecalculaTotales() {
        Poliza poliza = PolizaFixture.colectivaVigente();

        poliza.renovar(IPC, AHORA);

        assertThat(poliza.riesgosActivos())
                .extracting(Riesgo::getCanonMensual)
                .usingComparatorForType(BigDecimal::compareTo, BigDecimal.class)
                .containsExactly(new BigDecimal("1578000.00"), new BigDecimal("3366400.00"));
        assertThat(poliza.getCanonMensual()).isEqualByComparingTo("4944400.00");
        assertThat(poliza.getValorPrima()).isEqualByComparingTo("59332800.00");
    }

    @Test
    void noSePuedeRenovarUnaPolizaCancelada() {
        Poliza poliza = PolizaFixture.individualCancelada();

        assertThatThrownBy(() -> poliza.renovar(IPC, AHORA))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessage("No se puede renovar una póliza cancelada");
    }

    @Test
    void cancelarPolizaCancelaTodosSusRiesgos() {
        Poliza poliza = PolizaFixture.colectivaVigente();

        poliza.cancelar(HOY, Poliza.MOTIVO_CANCELACION_POLIZA);

        assertThat(poliza.getEstado()).isEqualTo(EstadoPoliza.CANCELADA);
        assertThat(poliza.riesgosActivos()).isEmpty();
        assertThat(poliza.getRiesgos())
                .allSatisfy(riesgo -> {
                    assertThat(riesgo.getEstado()).isEqualTo(EstadoRiesgo.CANCELADO);
                    assertThat(riesgo.getFechaCancelacion()).isEqualTo(HOY);
                    assertThat(riesgo.getMotivoCancelacion()).isEqualTo(Poliza.MOTIVO_CANCELACION_POLIZA);
                });
    }

    @Test
    void noSePuedeCancelarUnaPolizaYaCancelada() {
        Poliza poliza = PolizaFixture.individualCancelada();

        assertThatThrownBy(() -> poliza.cancelar(HOY, "Motivo"))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessage("La póliza ya se encuentra cancelada");
    }

    @Test
    void agregarRiesgoAColectivaRecalculaCanonYPrima() {
        Poliza poliza = PolizaFixture.colectivaVigente();

        Riesgo riesgo = poliza.agregarRiesgo(PolizaFixture.inmueble("50C-9999"), PolizaFixture.tercero("1"),
                PolizaFixture.tercero("2"), new BigDecimal("1000000"), HOY);

        assertThat(riesgo.getEstado()).isEqualTo(EstadoRiesgo.ACTIVO);
        assertThat(riesgo.getPolizaId()).isEqualTo(poliza.getId());
        assertThat(riesgo.getValorPrima()).isEqualByComparingTo("12000000.00");
        assertThat(poliza.riesgosActivos()).hasSize(3);
        assertThat(poliza.getCanonMensual()).isEqualByComparingTo("5700000.00");
        assertThat(poliza.getValorPrima()).isEqualByComparingTo("68400000.00");
    }

    @Test
    void agregarRiesgoAIndividualNoEstaPermitido() {
        Poliza poliza = PolizaFixture.individualVigente();

        assertThatThrownBy(() -> poliza.agregarRiesgo(PolizaFixture.inmueble("50C-9999"),
                PolizaFixture.tercero("1"), PolizaFixture.tercero("2"), new BigDecimal("1000000"), HOY))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("Una póliza individual solo puede tener un riesgo");
        assertThat(poliza.getRiesgos()).hasSize(1);
    }

    @Test
    void agregarRiesgoConInmuebleYaAseguradoNoEstaPermitido() {
        Poliza poliza = PolizaFixture.colectivaVigente();
        Inmueble existente = poliza.riesgosActivos().getFirst().getInmueble();

        assertThatThrownBy(() -> poliza.agregarRiesgo(existente, PolizaFixture.tercero("1"),
                PolizaFixture.tercero("2"), new BigDecimal("1000000"), HOY))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessage("El inmueble ya se encuentra asegurado en la póliza");
    }

    @Test
    void agregarRiesgoAPolizaCanceladaNoEstaPermitido() {
        Poliza poliza = PolizaFixture.colectivaVigente();
        poliza.cancelar(HOY, "Motivo");

        assertThatThrownBy(() -> poliza.agregarRiesgo(PolizaFixture.inmueble("50C-9999"),
                PolizaFixture.tercero("1"), PolizaFixture.tercero("2"), new BigDecimal("1000000"), HOY))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessage("No se pueden agregar riesgos a una póliza cancelada");
    }

    @Test
    void cancelarRiesgoDeColectivaRecalculaTotales() {
        Poliza poliza = PolizaFixture.colectivaVigente();
        Riesgo riesgo = poliza.riesgosActivos().getFirst();

        Riesgo cancelado = poliza.cancelarRiesgo(riesgo.getId(), HOY, "Terminación del contrato");

        assertThat(cancelado.getEstado()).isEqualTo(EstadoRiesgo.CANCELADO);
        assertThat(poliza.riesgosActivos()).hasSize(1);
        assertThat(poliza.getCanonMensual()).isEqualByComparingTo("3200000.00");
        assertThat(poliza.getValorPrima()).isEqualByComparingTo("38400000.00");
    }

    @Test
    void noSePuedeCancelarElUltimoRiesgoActivoDeUnaColectiva() {
        Poliza poliza = PolizaFixture.colectivaConUnRiesgo();
        Long riesgoId = poliza.riesgosActivos().getFirst().getId();

        assertThatThrownBy(() -> poliza.cancelarRiesgo(riesgoId, HOY, "Motivo"))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("al menos un riesgo activo");
    }

    @Test
    void noSePuedeCancelarElRiesgoDeUnaIndividual() {
        Poliza poliza = PolizaFixture.individualVigente();
        Long riesgoId = poliza.riesgosActivos().getFirst().getId();

        assertThatThrownBy(() -> poliza.cancelarRiesgo(riesgoId, HOY, "Motivo"))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("se debe cancelar la póliza");
    }

    @Test
    void cancelarRiesgoInexistenteLanzaNoEncontrado() {
        Poliza poliza = PolizaFixture.colectivaVigente();

        assertThatThrownBy(() -> poliza.cancelarRiesgo(999L, HOY, "Motivo"))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }
}
