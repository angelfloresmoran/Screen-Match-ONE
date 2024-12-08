package com.angelodev.screenmatch.modelo;

import com.fasterxml.jackson.annotation.JsonAlias;

import java.util.OptionalDouble;
import java.util.OptionalInt;

public class Serie {

    private String titulo;

    private String actores;

    private Categoria genero;

    private String poster;

    private Integer totalTemporadas;

    private Double evaluacion;

    private String descripcion;

    public Serie(DatosSerie datosSerie){
        this.titulo = datosSerie.titulo();
        this.actores = datosSerie.actores();
        this.genero = Categoria.fromString(datosSerie.genero().split(",")[0].trim());
        this.poster = datosSerie.poster();
        this.totalTemporadas = OptionalInt.of(Integer.valueOf(datosSerie.totalTemporadas())).orElse(0);
        this.evaluacion = OptionalDouble.of(Double.valueOf(datosSerie.evaluacion())).orElse(0);
        this.descripcion = datosSerie.descripcion();
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getActores() {
        return actores;
    }

    public void setActores(String actores) {
        this.actores = actores;
    }

    public Categoria getGenero() {
        return genero;
    }

    public void setGenero(Categoria genero) {
        this.genero = genero;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public Integer getTotalTemporadas() {
        return totalTemporadas;
    }

    public void setTotalTemporadas(Integer totalTemporadas) {
        this.totalTemporadas = totalTemporadas;
    }

    public Double getEvaluacion() {
        return evaluacion;
    }

    public void setEvaluacion(Double evaluacion) {
        this.evaluacion = evaluacion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return
                "titulo='" + titulo + '\'' +
                ", actores='" + actores + '\'' +
                ", genero=" + genero +
                ", poster='" + poster + '\'' +
                ", totalTemporadas=" + totalTemporadas +
                ", evaluacion=" + evaluacion +
                ", descripcion='" + descripcion;
    }
}
