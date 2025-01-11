package com.angelodev.screenmatch.dto;

import com.angelodev.screenmatch.modelo.Categoria;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

public record SerieDTO(

        Long id,

        String titulo,

        String actores,

        Categoria genero,

        String poster,

        Integer totalTemporadas,

        Double evaluacion,

        String descripcion
) {
}
