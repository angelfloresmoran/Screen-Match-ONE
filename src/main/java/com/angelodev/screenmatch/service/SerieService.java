package com.angelodev.screenmatch.service;

import com.angelodev.screenmatch.dto.EpisodioDTO;
import com.angelodev.screenmatch.dto.SerieDTO;
import com.angelodev.screenmatch.modelo.Categoria;
import com.angelodev.screenmatch.modelo.Serie;
import com.angelodev.screenmatch.repository.SerieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SerieService {

    @Autowired
    private SerieRepository repositorio;

    public List<SerieDTO> obtenerTodasLasSeries(){
        return convierteDatos(repositorio.findAll());
    }

    public List<SerieDTO> obtenerTop5() {
        return convierteDatos(repositorio.findTop5ByOrderByEvaluacionDesc());

    }

    public List<SerieDTO> obtenerLanzamientosMasRecientes(){
        return convierteDatos(repositorio.lanzamientosMasRecientes());
    }

    public List<SerieDTO> convierteDatos(List<Serie> serie){
        return serie.stream()
                .map(s -> new SerieDTO(s.getId(),s.getTitulo(), s.getActores(), s.getGenero(),
                        s.getPoster(),s.getTotalTemporadas(), s.getEvaluacion(),s.getDescripcion()))
                .collect(Collectors.toList());

    }

    public SerieDTO obtenerPorId(Long id) {
        Optional <Serie> serie = repositorio.findById(id);
        if (serie.isPresent()){
            Serie s = serie.get();
            return new SerieDTO(s.getId(),s.getTitulo(), s.getActores(), s.getGenero(),
                    s.getPoster(),s.getTotalTemporadas(), s.getEvaluacion(),s.getDescripcion());
        }
        return null;
    }

    public List<EpisodioDTO> obtenerTodasLasTemporadas(Long id) {
        Optional <Serie> serie = repositorio.findById(id);
        if (serie.isPresent()){
            Serie s = serie.get();
            return s.getEpisodios().stream().map(e -> new EpisodioDTO(e.getTemporada(), e.getTitulo(),
                    e.getNumeroEpisodio())).collect(Collectors.toList());
        }
        return null;
    }

    public List<EpisodioDTO> obtenerTemporadasPornumero(Long id, Long numeroTemporada) {
        return repositorio.obtenerTemporadasPorNumero(id, numeroTemporada).stream()
                .map(e -> new EpisodioDTO(e.getTemporada(), e.getTitulo(),
                        e.getNumeroEpisodio())).collect(Collectors.toList());
    }

    public List<SerieDTO> obtenerSeriesPorCategoria(String nombreGenero) {
        Categoria categoria = Categoria.fromEspanol(nombreGenero);
        return convierteDatos(repositorio.findByGenero(categoria));
    }
}
