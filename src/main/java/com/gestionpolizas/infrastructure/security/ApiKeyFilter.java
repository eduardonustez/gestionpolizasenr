package com.gestionpolizas.infrastructure.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestionpolizas.infrastructure.web.dto.ErrorResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;

@Component
public class ApiKeyFilter extends OncePerRequestFilter {

    public static final String HEADER_API_KEY = "x-api-key";

    private static final List<String> RUTAS_PUBLICAS = List.of("/swagger-ui", "/v3/api-docs");

    private final byte[] apiKey;
    private final ObjectMapper objectMapper;

    public ApiKeyFilter(@Value("${seguridad.api-key}") String apiKey, ObjectMapper objectMapper) {
        this.apiKey = apiKey.getBytes(StandardCharsets.UTF_8);
        this.objectMapper = objectMapper;
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        String ruta = request.getRequestURI().substring(request.getContextPath().length());
        return RUTAS_PUBLICAS.stream().anyMatch(ruta::startsWith);
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        String valor = request.getHeader(HEADER_API_KEY);
        if (valor == null || !MessageDigest.isEqual(apiKey, valor.getBytes(StandardCharsets.UTF_8))) {
            rechazar(response);
            return;
        }
        filterChain.doFilter(request, response);
    }

    private void rechazar(HttpServletResponse response) throws IOException {
        ErrorResponse error = ErrorResponse.of(HttpStatus.UNAUTHORIZED, "No autorizado",
                "Credenciales inválidas");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        objectMapper.writeValue(response.getOutputStream(), error);
    }
}
