package com.gestionpolizas.infrastructure.web.dto;

import jakarta.validation.constraints.Size;

public record CancelacionRequest(
        @Size(max = 200) String motivo) {
}
