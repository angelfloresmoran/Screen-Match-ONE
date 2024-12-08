package com.angelodev.screenmatch.modelo;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)

public record DatosSerie(

        @JsonAlias("Title") String titulo,

        @JsonAlias("Genre") String genero,

        @JsonAlias("Actors") String actores,

        @JsonAlias("Poster") String poster,

        @JsonAlias("totalSeasons") int totalTemporadas,

        @JsonAlias("imdbRating") String evaluacion,

        @JsonAlias("Plot") String descripcion
) {
}
