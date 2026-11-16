package com.gestionpolizas.domain.model;

import com.gestionpolizas.domain.exception.RecursoNoEncontradoException;
import com.gestionpolizas.domain.exception.ReglaNegocioException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class Poliza {

    public static final String MOTIVO_CANCELACION_POLIZA = "Cancelación de la póliza";

    private final Long id;
    private final String numeroPoliza;
    private final TipoPoliza tipo;
    private final Long tomadorId;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private final int mesesVigencia;
    private BigDecimal canonMensual;
    private BigDecimal valorPrima;
    private EstadoPoliza estado;
    private final boolean renovacionAutomatica;
    private int numeroRenovacion;
    private EstadoSyncCore estadoSyncCore;
    private final String numeroPolizaCore;
    private final Integer version;
    private final List<Riesgo> riesgos;

    public Poliza(Long id, String numeroPoliza, TipoPoliza tipo, Long tomadorId, LocalDate fechaInicio,
                  LocalDate fechaFin, int mesesVigencia, BigDecimal canonMensual, BigDecimal valorPrima,
                  EstadoPoliza estado, boolean renovacionAutomatica, int numeroRenovacion,
                  EstadoSyncCore estadoSyncCore, String numeroPolizaCore, Integer version, List<Riesgo> riesgos) {
        this.id = id;
        this.numeroPoliza = numeroPoliza;
        this.tipo = tipo;
        this.tomadorId = tomadorId;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.mesesVigencia = mesesVigencia;
        this.canonMensual = canonMensual;
        this.valorPrima = valorPrima;
        this.estado = estado;
        this.renovacionAutomatica = renovacionAutomatica;
        this.numeroRenovacion = numeroRenovacion;
        this.estadoSyncCore = estadoSyncCore;
        this.numeroPolizaCore = numeroPolizaCore;
        this.version = version;
        this.riesgos = new ArrayList<>(riesgos);
    }

    public List<Riesgo> riesgosActivos() {
        return riesgos.stream().filter(Riesgo::estaActivo).toList();
    }

    public Optional<Riesgo> riesgoActivoDelInmueble(Inmueble inmueble) {
        return riesgosActivos().stream()
                .filter(riesgo -> riesgo.getInmueble().mismaMatricula(inmueble))
                .findFirst();
    }

    public boolean estaCancelada() {
        return estado == EstadoPoliza.CANCELADA;
    }

    public boolean esColectiva() {
        return tipo == TipoPoliza.COLECTIVA;
    }

    public Riesgo agregarRiesgo(Inmueble inmueble, Tercero asegurado, Tercero beneficiario,
                                BigDecimal canonMensualRiesgo, LocalDate fechaInclusion) {
        validarNoCancelada("No se pueden agregar riesgos a una póliza cancelada");
        if (!esColectiva()) {
            throw new ReglaNegocioException(
                    "Solo se pueden agregar riesgos a pólizas colectivas. Una póliza individual solo puede tener un riesgo");
        }
        if (riesgoActivoDelInmueble(inmueble).isPresent()) {
            throw new ReglaNegocioException("El inmueble ya se encuentra asegurado en la póliza");
        }
        Riesgo riesgo = Riesgo.nuevo(id, inmueble, asegurado, beneficiario, canonMensualRiesgo, mesesVigencia,
                fechaInclusion);
        riesgos.add(riesgo);
        recalcularTotales();
        estadoSyncCore = EstadoSyncCore.PENDIENTE;
        return riesgo;
    }

    public Riesgo cancelarRiesgo(Long riesgoId, LocalDate fecha, String motivo) {
        validarNoCancelada("No se pueden cancelar riesgos de una póliza cancelada");
        Riesgo riesgo = riesgos.stream()
                .filter(r -> r.getId().equals(riesgoId))
                .findFirst()
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el riesgo " + riesgoId));
        if (!riesgo.estaActivo()) {
            throw new ReglaNegocioException("El riesgo ya se encuentra cancelado");
        }
        if (!esColectiva()) {
            throw new ReglaNegocioException(
                    "El riesgo de una póliza individual no se puede cancelar de forma independiente, se debe cancelar la póliza");
        }
        if (riesgosActivos().size() == 1) {
            throw new ReglaNegocioException(
                    "La póliza colectiva debe tener al menos un riesgo activo, para retirarlo se debe cancelar la póliza");
        }
        riesgo.cancelar(fecha, motivo);
        recalcularTotales();
        estadoSyncCore = EstadoSyncCore.PENDIENTE;
        return riesgo;
    }

    public Certificado renovar(BigDecimal ipc, LocalDateTime fechaRegistro) {
        if (estaCancelada()) {
            throw new ReglaNegocioException("No se puede renovar una póliza cancelada");
        }
        if (ipc == null || ipc.signum() < 0) {
            throw new ReglaNegocioException("El IPC configurado para la renovación no es válido");
        }
        List<Riesgo> activos = riesgosActivos();
        if (activos.isEmpty()) {
            canonMensual = Montos.incrementarPorIpc(canonMensual, ipc);
            valorPrima = Montos.prima(canonMensual, mesesVigencia);
        } else {
            activos.forEach(riesgo -> riesgo.ajustarPorIpc(ipc, mesesVigencia));
            recalcularTotales();
        }
        fechaInicio = fechaFin;
        fechaFin = fechaInicio.plusMonths(mesesVigencia);
        numeroRenovacion++;
        estado = EstadoPoliza.RENOVADA;
        estadoSyncCore = EstadoSyncCore.PENDIENTE;
        return Certificado.renovacion(id, numeroRenovacion + 1, fechaInicio, fechaFin, canonMensual, valorPrima,
                ipc, fechaRegistro);
    }

    public void cancelar(LocalDate fecha, String motivo) {
        validarNoCancelada("La póliza ya se encuentra cancelada");
        riesgosActivos().forEach(riesgo -> riesgo.cancelar(fecha, motivo));
        estado = EstadoPoliza.CANCELADA;
        estadoSyncCore = EstadoSyncCore.PENDIENTE;
    }

    private void validarNoCancelada(String mensaje) {
        if (estaCancelada()) {
            throw new ReglaNegocioException(mensaje);
        }
    }

    private void recalcularTotales() {
        List<Riesgo> activos = riesgosActivos();
        canonMensual = activos.stream().map(Riesgo::getCanonMensual).reduce(BigDecimal.ZERO, BigDecimal::add);
        valorPrima = activos.stream().map(Riesgo::getValorPrima).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Long getId() {
        return id;
    }

    public String getNumeroPoliza() {
        return numeroPoliza;
    }

    public TipoPoliza getTipo() {
        return tipo;
    }

    public Long getTomadorId() {
        return tomadorId;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public int getMesesVigencia() {
        return mesesVigencia;
    }

    public BigDecimal getCanonMensual() {
        return canonMensual;
    }

    public BigDecimal getValorPrima() {
        return valorPrima;
    }

    public EstadoPoliza getEstado() {
        return estado;
    }

    public boolean isRenovacionAutomatica() {
        return renovacionAutomatica;
    }

    public int getNumeroRenovacion() {
        return numeroRenovacion;
    }

    public EstadoSyncCore getEstadoSyncCore() {
        return estadoSyncCore;
    }

    public String getNumeroPolizaCore() {
        return numeroPolizaCore;
    }

    public Integer getVersion() {
        return version;
    }

    public List<Riesgo> getRiesgos() {
        return Collections.unmodifiableList(riesgos);
    }
}
