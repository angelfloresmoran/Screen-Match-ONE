package com.angelodev.screenmatch.modelo;

import com.fasterxml.jackson.annotation.JsonAlias;

public record DatosSerie(

        @JsonAlias("Title") String titulo,

        @JsonAlias("Genre") String genero,

        @JsonAlias("Plot") String descripcion,

        @JsonAlias("totalSeasons") String totalTemporadas,

        @JsonAlias("imdbRating") String evaluacion
) {
}
