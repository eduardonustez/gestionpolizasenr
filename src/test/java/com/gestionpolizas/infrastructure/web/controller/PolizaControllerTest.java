package com.gestionpolizas.infrastructure.web.controller;

import com.gestionpolizas.domain.exception.RecursoNoEncontradoException;
import com.gestionpolizas.domain.exception.ReglaNegocioException;
import com.gestionpolizas.domain.model.EstadoPoliza;
import com.gestionpolizas.domain.model.Poliza;
import com.gestionpolizas.domain.model.PolizaFixture;
import com.gestionpolizas.domain.model.TipoPoliza;
import com.gestionpolizas.domain.port.in.GestionPolizaUseCase;
import com.gestionpolizas.domain.port.in.GestionRiesgoUseCase;
import com.gestionpolizas.infrastructure.security.ApiKeyFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {PolizaController.class, RiesgoController.class})
@Import(ApiKeyFilter.class)
class PolizaControllerTest {

    private static final String API_KEY = "123456";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GestionPolizaUseCase gestionPoliza;

    @MockitoBean
    private GestionRiesgoUseCase gestionRiesgo;

    @Test
    void rechazaPeticionesSinApiKey() throws Exception {
        mockMvc.perform(get("/polizas"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.title").value("No autorizado"));

        verifyNoInteractions(gestionPoliza);
    }

    @Test
    void rechazaPeticionesConApiKeyInvalida() throws Exception {
        mockMvc.perform(get("/polizas").header(ApiKeyFilter.HEADER_API_KEY, "000000"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void listaPolizasFiltrandoPorTipoYEstado() throws Exception {
        Poliza poliza = PolizaFixture.colectivaVigente();
        when(gestionPoliza.listar(TipoPoliza.COLECTIVA, EstadoPoliza.VIGENTE)).thenReturn(List.of(poliza));

        mockMvc.perform(get("/polizas")
                        .param("tipo", "colectiva")
                        .param("estado", "VIGENTE")
                        .header(ApiKeyFilter.HEADER_API_KEY, API_KEY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(poliza.getId().toString()))
                .andExpect(jsonPath("$[0].tipo").value("COLECTIVA"))
                .andExpect(jsonPath("$[0].riesgosActivos").value(2));
    }

    @Test
    void tipoInvalidoRetornaBadRequest() throws Exception {
        mockMvc.perform(get("/polizas").param("tipo", "OTRA").header(ApiKeyFilter.HEADER_API_KEY, API_KEY))
                .andExpect(status().isBadRequest());
    }

    @Test
    void renovarPolizaCanceladaRetornaUnprocessableEntity() throws Exception {
        Long id = 999L;
        when(gestionPoliza.renovar(id)).thenThrow(new ReglaNegocioException("No se puede renovar una póliza cancelada"));

        mockMvc.perform(post("/polizas/{id}/renovar", id).header(ApiKeyFilter.HEADER_API_KEY, API_KEY))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.detail").value("No se puede renovar una póliza cancelada"));
    }

    @Test
    void riesgosDePolizaInexistenteRetornaNotFound() throws Exception {
        Long id = 999L;
        when(gestionPoliza.listarRiesgos(id)).thenThrow(new RecursoNoEncontradoException("No existe la póliza " + id));

        mockMvc.perform(get("/polizas/{id}/riesgos", id).header(ApiKeyFilter.HEADER_API_KEY, API_KEY))
                .andExpect(status().isNotFound());
    }

    @Test
    void agregarRiesgoConCuerpoInvalidoRetornaBadRequest() throws Exception {
        mockMvc.perform(post("/polizas/{id}/riesgos", 999L)
                        .header(ApiKeyFilter.HEADER_API_KEY, API_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"canonMensual\": -1}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.canonMensual").exists())
                .andExpect(jsonPath("$.errors.inmueble").exists());

        verifyNoInteractions(gestionRiesgo);
    }

    @Test
    void agregarRiesgoRetornaCreated() throws Exception {
        Poliza poliza = PolizaFixture.colectivaVigente();
        when(gestionRiesgo.agregar(any(), any())).thenReturn(poliza.riesgosActivos().getFirst());

        mockMvc.perform(post("/polizas/{id}/riesgos", poliza.getId())
                        .header(ApiKeyFilter.HEADER_API_KEY, API_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "inmueble": {"matriculaInmobiliaria": "50C-7777", "direccion": "Calle 80 # 10-20",
                                               "ciudad": "Bogotá", "tipo": "VIVIENDA"},
                                  "asegurado": {"tipoDocumento": "CC", "numeroDocumento": "1012345678",
                                                "tipoPersona": "NATURAL", "nombreRazonSocial": "Ana Pérez"},
                                  "beneficiario": {"tipoDocumento": "CC", "numeroDocumento": "79876543",
                                                   "tipoPersona": "NATURAL", "nombreRazonSocial": "Luis Gómez"},
                                  "canonMensual": 1200000
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estado").value("ACTIVO"));
    }
}
