package com.angelodev.screenmatch.repository;

import com.angelodev.screenmatch.modelo.Categoria;
import com.angelodev.screenmatch.modelo.Serie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SerieRepository extends JpaRepository<Serie, Long> {

   Optional<Serie> findByTituloContainsIgnoreCase(String nombreSerie);

   List<Serie> findTop5ByOrderByEvaluacionDesc();

   List<Serie> findByGenero(Categoria categoria);

   List<Serie> findByTotalTemporadasLessThanEqualAndEvaluacionGreaterThanEqual(Integer numeroTemporadas, Double evaluacion);

}
