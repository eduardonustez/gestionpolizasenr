package com.gestionpolizas.infrastructure.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;

import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        String title,
        int status,
        String detail,
        Map<String, String> errors) {

    public static ErrorResponse of(HttpStatus status, String title, String detail) {
        return new ErrorResponse(title, status.value(), detail, null);
    }

    public static ErrorResponse of(HttpStatus status, String title, String detail, Map<String, String> errors) {
        return new ErrorResponse(title, status.value(), detail, errors);
    }
}
