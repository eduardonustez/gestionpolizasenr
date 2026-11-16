package com.gestionpolizas.domain.model;

import com.gestionpolizas.domain.exception.ReglaNegocioException;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Riesgo {

    private final Long id;
    private final Long polizaId;
    private final Inmueble inmueble;
    private final Tercero asegurado;
    private final Tercero beneficiario;
    private BigDecimal canonMensual;
    private BigDecimal valorPrima;
    private final LocalDate fechaInclusion;
    private LocalDate fechaCancelacion;
    private String motivoCancelacion;
    private EstadoRiesgo estado;
    private EstadoSyncCore estadoSyncCore;
    private final Integer version;

    public Riesgo(Long id, Long polizaId, Inmueble inmueble, Tercero asegurado, Tercero beneficiario,
                  BigDecimal canonMensual, BigDecimal valorPrima, LocalDate fechaInclusion,
                  LocalDate fechaCancelacion, String motivoCancelacion, EstadoRiesgo estado,
                  EstadoSyncCore estadoSyncCore, Integer version) {
        this.id = id;
        this.polizaId = polizaId;
        this.inmueble = inmueble;
        this.asegurado = asegurado;
        this.beneficiario = beneficiario;
        this.canonMensual = canonMensual;
        this.valorPrima = valorPrima;
        this.fechaInclusion = fechaInclusion;
        this.fechaCancelacion = fechaCancelacion;
        this.motivoCancelacion = motivoCancelacion;
        this.estado = estado;
        this.estadoSyncCore = estadoSyncCore;
        this.version = version;
    }

    static Riesgo nuevo(Long polizaId, Inmueble inmueble, Tercero asegurado, Tercero beneficiario,
                        BigDecimal canonMensual, int mesesVigencia, LocalDate fechaInclusion) {
        if (canonMensual == null || canonMensual.signum() <= 0) {
            throw new ReglaNegocioException("El canon mensual del riesgo debe ser mayor a cero");
        }
        BigDecimal canon = Montos.normalizar(canonMensual);
        return new Riesgo(null, polizaId, inmueble, asegurado, beneficiario, canon,
                Montos.prima(canon, mesesVigencia), fechaInclusion, null, null, EstadoRiesgo.ACTIVO,
                EstadoSyncCore.PENDIENTE, null);
    }

    public boolean estaActivo() {
        return estado == EstadoRiesgo.ACTIVO;
    }

    void cancelar(LocalDate fecha, String motivo) {
        this.estado = EstadoRiesgo.CANCELADO;
        this.fechaCancelacion = fecha;
        this.motivoCancelacion = motivo;
        this.estadoSyncCore = EstadoSyncCore.PENDIENTE;
    }

    void ajustarPorIpc(BigDecimal ipc, int mesesVigencia) {
        this.canonMensual = Montos.incrementarPorIpc(canonMensual, ipc);
        this.valorPrima = Montos.prima(canonMensual, mesesVigencia);
        this.estadoSyncCore = EstadoSyncCore.PENDIENTE;
    }

    public Long getId() {
        return id;
    }

    public Long getPolizaId() {
        return polizaId;
    }

    public Inmueble getInmueble() {
        return inmueble;
    }

    public Tercero getAsegurado() {
        return asegurado;
    }

    public Tercero getBeneficiario() {
        return beneficiario;
    }

    public BigDecimal getCanonMensual() {
        return canonMensual;
    }

    public BigDecimal getValorPrima() {
        return valorPrima;
    }

    public LocalDate getFechaInclusion() {
        return fechaInclusion;
    }

    public LocalDate getFechaCancelacion() {
        return fechaCancelacion;
    }

    public String getMotivoCancelacion() {
        return motivoCancelacion;
    }

    public EstadoRiesgo getEstado() {
        return estado;
    }

    public EstadoSyncCore getEstadoSyncCore() {
        return estadoSyncCore;
    }

    public Integer getVersion() {
        return version;
    }
}
