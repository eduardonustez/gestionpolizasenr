package com.gestionpolizas.infrastructure.web.controller;

import com.gestionpolizas.domain.model.EstadoPoliza;
import com.gestionpolizas.domain.model.Riesgo;
import com.gestionpolizas.domain.model.TipoPoliza;
import com.gestionpolizas.domain.port.in.GestionPolizaUseCase;
import com.gestionpolizas.domain.port.in.GestionRiesgoUseCase;
import com.gestionpolizas.infrastructure.web.dto.CancelacionRequest;
import com.gestionpolizas.infrastructure.web.dto.NuevoRiesgoRequest;
import com.gestionpolizas.infrastructure.web.dto.PolizaResponse;
import com.gestionpolizas.infrastructure.web.dto.RiesgoResponse;
import com.gestionpolizas.infrastructure.web.mapper.WebMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/polizas")
@Tag(name = "Pólizas")
public class PolizaController {

    private final GestionPolizaUseCase gestionPoliza;
    private final GestionRiesgoUseCase gestionRiesgo;

    public PolizaController(GestionPolizaUseCase gestionPoliza, GestionRiesgoUseCase gestionRiesgo) {
        this.gestionPoliza = gestionPoliza;
        this.gestionRiesgo = gestionRiesgo;
    }

    @GetMapping
    @Operation(summary = "Lista las pólizas filtrando por tipo y estado")
    public List<PolizaResponse> listar(@RequestParam(required = false) TipoPoliza tipo,
                                       @RequestParam(required = false) EstadoPoliza estado) {
        return gestionPoliza.listar(tipo, estado).stream().map(WebMapper::toResponse).toList();
    }

    @GetMapping("/{id}/riesgos")
    @Operation(summary = "Lista los riesgos de una póliza")
    public List<RiesgoResponse> listarRiesgos(@PathVariable Long id) {
        return gestionPoliza.listarRiesgos(id).stream().map(WebMapper::toResponse).toList();
    }

    @PostMapping("/{id}/renovar")
    @Operation(summary = "Renueva la póliza ajustando canon y prima con el IPC vigente")
    public PolizaResponse renovar(@PathVariable Long id) {
        return WebMapper.toResponse(gestionPoliza.renovar(id));
    }

    @PostMapping("/{id}/cancelar")
    @Operation(summary = "Cancela la póliza y todos sus riesgos activos")
    public PolizaResponse cancelar(@PathVariable Long id,
                                   @Valid @RequestBody(required = false) CancelacionRequest request) {
        return WebMapper.toResponse(gestionPoliza.cancelar(id, request == null ? null : request.motivo()));
    }

    @PostMapping("/{id}/riesgos")
    @Operation(summary = "Agrega un riesgo a una póliza colectiva")
    public ResponseEntity<RiesgoResponse> agregarRiesgo(@PathVariable Long id,
                                                        @Valid @RequestBody NuevoRiesgoRequest request) {
        Riesgo riesgo = gestionRiesgo.agregar(id, WebMapper.toCommand(request));
        return ResponseEntity.created(URI.create("/polizas/" + id + "/riesgos"))
                .body(WebMapper.toResponse(riesgo));
    }
}
