package com.gestionpolizas.infrastructure.web.controller;

import com.gestionpolizas.domain.port.in.GestionRiesgoUseCase;
import com.gestionpolizas.infrastructure.web.dto.CancelacionRequest;
import com.gestionpolizas.infrastructure.web.dto.RiesgoResponse;
import com.gestionpolizas.infrastructure.web.mapper.WebMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/riesgos")
@Tag(name = "Riesgos")
public class RiesgoController {

    private final GestionRiesgoUseCase gestionRiesgo;

    public RiesgoController(GestionRiesgoUseCase gestionRiesgo) {
        this.gestionRiesgo = gestionRiesgo;
    }

    @PostMapping("/{id}/cancelar")
    @Operation(summary = "Cancela un riesgo de una póliza colectiva")
    public RiesgoResponse cancelar(@PathVariable Long id,
                                   @Valid @RequestBody(required = false) CancelacionRequest request) {
        return WebMapper.toResponse(gestionRiesgo.cancelar(id, request == null ? null : request.motivo()));
    }
}
