package com.gestionpolizas.infrastructure.persistence.entity;

import com.gestionpolizas.domain.model.EstadoPoliza;
import com.gestionpolizas.domain.model.EstadoSyncCore;
import com.gestionpolizas.domain.model.TipoPoliza;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "poliza")
public class PolizaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_poliza", nullable = false)
    private String numeroPoliza;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private TipoPoliza tipo;

    @Column(name = "tomador_id", nullable = false)
    private Long tomadorId;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;

    @Column(name = "meses_vigencia", nullable = false)
    private int mesesVigencia;

    @Column(name = "canon_mensual", nullable = false)
    private BigDecimal canonMensual;

    @Column(name = "valor_prima", nullable = false)
    private BigDecimal valorPrima;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoPoliza estado;

    @Column(name = "renovacion_automatica", nullable = false)
    private boolean renovacionAutomatica;

    @Column(name = "numero_renovacion", nullable = false)
    private int numeroRenovacion;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_sync_core", nullable = false)
    private EstadoSyncCore estadoSyncCore;

    @Column(name = "numero_poliza_core")
    private String numeroPolizaCore;

    @Version
    @Column(name = "version", nullable = false)
    private Integer version;

    @OneToMany(mappedBy = "poliza", cascade = CascadeType.ALL)
    @OrderBy("fechaInclusion ASC")
    private List<RiesgoEntity> riesgos = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumeroPoliza() {
        return numeroPoliza;
    }

    public void setNumeroPoliza(String numeroPoliza) {
        this.numeroPoliza = numeroPoliza;
    }

    public TipoPoliza getTipo() {
        return tipo;
    }

    public void setTipo(TipoPoliza tipo) {
        this.tipo = tipo;
    }

    public Long getTomadorId() {
        return tomadorId;
    }

    public void setTomadorId(Long tomadorId) {
        this.tomadorId = tomadorId;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public int getMesesVigencia() {
        return mesesVigencia;
    }

    public void setMesesVigencia(int mesesVigencia) {
        this.mesesVigencia = mesesVigencia;
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

    public EstadoPoliza getEstado() {
        return estado;
    }

    public void setEstado(EstadoPoliza estado) {
        this.estado = estado;
    }

    public boolean isRenovacionAutomatica() {
        return renovacionAutomatica;
    }

    public void setRenovacionAutomatica(boolean renovacionAutomatica) {
        this.renovacionAutomatica = renovacionAutomatica;
    }

    public int getNumeroRenovacion() {
        return numeroRenovacion;
    }

    public void setNumeroRenovacion(int numeroRenovacion) {
        this.numeroRenovacion = numeroRenovacion;
    }

    public EstadoSyncCore getEstadoSyncCore() {
        return estadoSyncCore;
    }

    public void setEstadoSyncCore(EstadoSyncCore estadoSyncCore) {
        this.estadoSyncCore = estadoSyncCore;
    }

    public String getNumeroPolizaCore() {
        return numeroPolizaCore;
    }

    public void setNumeroPolizaCore(String numeroPolizaCore) {
        this.numeroPolizaCore = numeroPolizaCore;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public List<RiesgoEntity> getRiesgos() {
        return riesgos;
    }

    public void setRiesgos(List<RiesgoEntity> riesgos) {
        this.riesgos = riesgos;
    }
}
