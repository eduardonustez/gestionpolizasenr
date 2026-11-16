package com.gestionpolizas.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public final class PolizaFixture {

    private static final AtomicLong SECUENCIA = new AtomicLong();

    private PolizaFixture() {
    }

    public static Poliza individualVigente() {
        Long polizaId = siguienteId();
        Riesgo riesgo = riesgoActivo(polizaId, "50C-0001", "2000000.00", 12);
        return poliza(polizaId, TipoPoliza.INDIVIDUAL, EstadoPoliza.VIGENTE, "2000000.00", "24000000.00",
                List.of(riesgo));
    }

    public static Poliza colectivaVigente() {
        Long polizaId = siguienteId();
        Riesgo primero = riesgoActivo(polizaId, "50C-0002", "1500000.00", 12);
        Riesgo segundo = riesgoActivo(polizaId, "50C-0003", "3200000.00", 12);
        return poliza(polizaId, TipoPoliza.COLECTIVA, EstadoPoliza.VIGENTE, "4700000.00", "56400000.00",
                List.of(primero, segundo));
    }

    public static Poliza colectivaConUnRiesgo() {
        Long polizaId = siguienteId();
        Riesgo riesgo = riesgoActivo(polizaId, "50C-0004", "2500000.00", 12);
        return poliza(polizaId, TipoPoliza.COLECTIVA, EstadoPoliza.VIGENTE, "2500000.00", "30000000.00",
                List.of(riesgo));
    }

    public static Poliza individualCancelada() {
        Long polizaId = siguienteId();
        Riesgo riesgo = new Riesgo(siguienteId(), polizaId, inmueble("50C-0005"), tercero("100"),
                tercero("200"), new BigDecimal("1800000.00"), new BigDecimal("21600000.00"),
                LocalDate.of(2025, 6, 1), LocalDate.of(2025, 11, 15), "Terminación del contrato",
                EstadoRiesgo.CANCELADO, EstadoSyncCore.SINCRONIZADO, 0);
        return poliza(polizaId, TipoPoliza.INDIVIDUAL, EstadoPoliza.CANCELADA, "1800000.00", "21600000.00",
                List.of(riesgo));
    }

    public static Inmueble inmueble(String matricula) {
        return new Inmueble(siguienteId(), matricula, "Calle 1 # 2-3", "Bogotá", TipoInmueble.VIVIENDA);
    }

    public static Tercero tercero(String documento) {
        return new Tercero(siguienteId(), TipoDocumento.CC, documento, TipoPersona.NATURAL, "Tercero " + documento,
                "tercero" + documento + "@correo.com", "3000000000", true);
    }

    private static Long siguienteId() {
        return SECUENCIA.incrementAndGet();
    }

    private static Riesgo riesgoActivo(Long polizaId, String matricula, String canon, int meses) {
        BigDecimal canonMensual = new BigDecimal(canon);
        return new Riesgo(siguienteId(), polizaId, inmueble(matricula), tercero(matricula + "-A"),
                tercero(matricula + "-B"), canonMensual, canonMensual.multiply(BigDecimal.valueOf(meses)),
                LocalDate.of(2026, 1, 1), null, null, EstadoRiesgo.ACTIVO, EstadoSyncCore.SINCRONIZADO, 0);
    }

    private static Poliza poliza(Long id, TipoPoliza tipo, EstadoPoliza estado, String canon, String prima,
                                 List<Riesgo> riesgos) {
        return new Poliza(id, "POL-" + id, tipo, siguienteId(),
                LocalDate.of(2026, 1, 1), LocalDate.of(2027, 1, 1), 12, new BigDecimal(canon),
                new BigDecimal(prima), estado, true, 0, EstadoSyncCore.SINCRONIZADO, "CORE-1", 0, riesgos);
    }
}
