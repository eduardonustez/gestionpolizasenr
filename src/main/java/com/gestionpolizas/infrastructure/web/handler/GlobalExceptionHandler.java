package com.gestionpolizas.infrastructure.web.handler;

import com.gestionpolizas.domain.exception.RecursoNoEncontradoException;
import com.gestionpolizas.domain.exception.ReglaNegocioException;
import com.gestionpolizas.infrastructure.web.dto.ErrorResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String SOLICITUD_INVALIDA = "Solicitud inválida";

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> manejarNoEncontrado(RecursoNoEncontradoException ex) {
        return responder(ErrorResponse.of(HttpStatus.NOT_FOUND, "Recurso no encontrado", ex.getMessage()));
    }

    @ExceptionHandler(ReglaNegocioException.class)
    public ResponseEntity<ErrorResponse> manejarReglaNegocio(ReglaNegocioException ex) {
        return responder(ErrorResponse.of(HttpStatus.UNPROCESSABLE_ENTITY, "Regla de negocio incumplida",
                ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> manejarValidacion(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errors.putIfAbsent(error.getField(), error.getDefaultMessage()));
        return responder(ErrorResponse.of(HttpStatus.BAD_REQUEST, SOLICITUD_INVALIDA,
                "La solicitud tiene campos inválidos", errors));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> manejarTipoInvalido(MethodArgumentTypeMismatchException ex) {
        return responder(ErrorResponse.of(HttpStatus.BAD_REQUEST, SOLICITUD_INVALIDA,
                "El valor '" + ex.getValue() + "' no es válido para el parámetro '" + ex.getName() + "'"));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> manejarCuerpoInvalido(HttpMessageNotReadableException ex) {
        return responder(ErrorResponse.of(HttpStatus.BAD_REQUEST, SOLICITUD_INVALIDA,
                "El cuerpo de la solicitud no es válido"));
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<ErrorResponse> manejarConcurrencia(ObjectOptimisticLockingFailureException ex) {
        return responder(ErrorResponse.of(HttpStatus.CONFLICT, "Conflicto de concurrencia",
                "El registro fue modificado por otra operación, intente nuevamente"));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> manejarIntegridad(DataIntegrityViolationException ex) {
        return responder(ErrorResponse.of(HttpStatus.CONFLICT, "Conflicto de datos",
                "La operación viola una restricción de integridad de los datos"));
    }

    private ResponseEntity<ErrorResponse> responder(ErrorResponse error) {
        return ResponseEntity.status(error.status())
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(error);
    }
}
