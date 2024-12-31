package com.angelodev.screenmatch.repository;

import com.angelodev.screenmatch.modelo.Categoria;
import com.angelodev.screenmatch.modelo.Episodio;
import com.angelodev.screenmatch.modelo.Serie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SerieRepository extends JpaRepository<Serie, Long> {

   Optional<Serie> findByTituloContainsIgnoreCase(String nombreSerie);

   List<Serie> findTop5ByOrderByEvaluacionDesc();

   List<Serie> findByGenero(Categoria categoria);

   //List<Serie> findByTotalTemporadasLessThanEqualAndEvaluacionGreaterThanEqual(Integer numeroTemporadas, Double evaluacion);
   @Query(" SELECT s FROM Serie s WHERE s.totalTemporadas <= :totalTemporadas AND s.evaluacion >= :evaluacion ")
   List<Serie> seriesPorTemporadaYEvaluacion(int totalTemporadas, double evaluacion);

   @Query(" SELECT e FROM Serie s JOIN s.episodios e WHERE e.titulo ILIKE %:nombreEpisodio% ")
   List<Episodio> episodioPorNombre(String nombreEpisodio);

   @Query(" SELECT e FROM Serie s JOIN s.episodios e WHERE s = :serie ORDER BY e.evaluacion DESC LIMIT 5 ")
   List<Episodio> top5Episodios (Serie serie);


}
