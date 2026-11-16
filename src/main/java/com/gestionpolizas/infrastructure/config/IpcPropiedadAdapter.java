package com.gestionpolizas.infrastructure.config;

import com.gestionpolizas.domain.port.out.IpcPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class IpcPropiedadAdapter implements IpcPort {

    private final BigDecimal ipcVigente;

    public IpcPropiedadAdapter(@Value("${polizas.ipc}") BigDecimal ipcVigente) {
        this.ipcVigente = ipcVigente;
    }

    @Override
    public BigDecimal obtenerIpcVigente() {
        return ipcVigente;
    }
}
