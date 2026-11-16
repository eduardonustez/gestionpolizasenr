package com.gestionpolizas.infrastructure.core;

import com.gestionpolizas.domain.model.EdicionCore;
import com.gestionpolizas.domain.port.out.CoreEdicionPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CoreEdicionLogAdapter implements CoreEdicionPort {

    private static final Logger log = LoggerFactory.getLogger(CoreEdicionLogAdapter.class);

    @Override
    public void registrarEdicion(EdicionCore edicion) {
        log.info("Edición enviada al servicio agnóstico de edición: entidad={}, id={}, accion={}, fecha={}",
                edicion.entidad(), edicion.entidadId(), edicion.accion(), edicion.fecha());
    }
}
