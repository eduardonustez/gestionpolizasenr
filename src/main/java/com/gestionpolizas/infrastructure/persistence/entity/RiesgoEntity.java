package com.gestionpolizas.infrastructure.persistence.entity;

import com.gestionpolizas.domain.model.EstadoRiesgo;
import com.gestionpolizas.domain.model.EstadoSyncCore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "riesgo")
public class RiesgoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "poliza_id", nullable = false)
    private PolizaEntity poliza;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "inmueble_id", nullable = false)
    private InmuebleEntity inmueble;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "asegurado_id", nullable = false)
    private TerceroEntity asegurado;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "beneficiario_id", nullable = false)
    private TerceroEntity beneficiario;

    @Column(name = "canon_mensual", nullable = false)
    private BigDecimal canonMensual;

    @Column(name = "valor_prima", nullable = false)
    private BigDecimal valorPrima;

    @Column(name = "fecha_inclusion", nullable = false)
    private LocalDate fechaInclusion;

    @Column(name = "fecha_cancelacion")
    private LocalDate fechaCancelacion;

    @Column(name = "motivo_cancelacion")
    private String motivoCancelacion;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoRiesgo estado;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_sync_core", nullable = false)
    private EstadoSyncCore estadoSyncCore;

    @Version
    @Column(name = "version", nullable = false)
    private Integer version;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PolizaEntity getPoliza() {
        return poliza;
    }

    public void setPoliza(PolizaEntity poliza) {
        this.poliza = poliza;
    }

    public InmuebleEntity getInmueble() {
        return inmueble;
    }

    public void setInmueble(InmuebleEntity inmueble) {
        this.inmueble = inmueble;
    }

    public TerceroEntity getAsegurado() {
        return asegurado;
    }

    public void setAsegurado(TerceroEntity asegurado) {
        this.asegurado = asegurado;
    }

    public TerceroEntity getBeneficiario() {
        return beneficiario;
    }

    public void setBeneficiario(TerceroEntity beneficiario) {
        this.beneficiario = beneficiario;
    }

    public BigDecimal getCanonMensual() {
        return canonMensual;
    }

    public void setCanonMensual(BigDecimal canonMensual) {
        this.canonMensual = canonMensual;
    }

    public BigDecimal getValorPrima() {
        return valorPrima;
    }

    public void setValorPrima(BigDecimal valorPrima) {
        this.valorPrima = valorPrima;
    }

    public LocalDate getFechaInclusion() {
        return fechaInclusion;
    }

    public void setFechaInclusion(LocalDate fechaInclusion) {
        this.fechaInclusion = fechaInclusion;
    }

    public LocalDate getFechaCancelacion() {
        return fechaCancelacion;
    }

    public void setFechaCancelacion(LocalDate fechaCancelacion) {
        this.fechaCancelacion = fechaCancelacion;
    }

    public String getMotivoCancelacion() {
        return motivoCancelacion;
    }

    public void setMotivoCancelacion(String motivoCancelacion) {
        this.motivoCancelacion = motivoCancelacion;
    }

    public EstadoRiesgo getEstado() {
        return estado;
    }

    public void setEstado(EstadoRiesgo estado) {
        this.estado = estado;
    }

    public EstadoSyncCore getEstadoSyncCore() {
        return estadoSyncCore;
    }

    public void setEstadoSyncCore(EstadoSyncCore estadoSyncCore) {
        this.estadoSyncCore = estadoSyncCore;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
