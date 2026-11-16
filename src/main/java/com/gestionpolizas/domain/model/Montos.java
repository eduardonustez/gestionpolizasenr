package com.gestionpolizas.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

final class Montos {

    private static final int ESCALA = 2;

    private Montos() {
    }

    static BigDecimal normalizar(BigDecimal valor) {
        return valor.setScale(ESCALA, RoundingMode.HALF_UP);
    }

    static BigDecimal incrementarPorIpc(BigDecimal valor, BigDecimal ipc) {
        return normalizar(valor.multiply(BigDecimal.ONE.add(ipc)));
    }

    static BigDecimal prima(BigDecimal canonMensual, int mesesVigencia) {
        return normalizar(canonMensual.multiply(BigDecimal.valueOf(mesesVigencia)));
    }
}
