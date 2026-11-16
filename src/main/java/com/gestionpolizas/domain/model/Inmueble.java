package com.gestionpolizas.domain.model;

public record Inmueble(
        Long id,
        String matriculaInmobiliaria,
        String direccion,
        String ciudad,
        TipoInmueble tipo) {

    public boolean mismaMatricula(Inmueble otro) {
        return matriculaInmobiliaria.equalsIgnoreCase(otro.matriculaInmobiliaria());
    }
}
