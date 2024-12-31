package com.angelodev.screenmatch.principal;

import com.angelodev.screenmatch.modelo.*;
import com.angelodev.screenmatch.repository.SerieRepository;
import com.angelodev.screenmatch.service.ConsumoAPI;
import com.angelodev.screenmatch.service.ConvierteDatos;

import java.text.BreakIterator;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class Principal {

    private ConsumoAPI consumoApi = new ConsumoAPI();

    private Scanner teclado = new Scanner(System.in);

    private final String URL_BASE = "http://www.omdbapi.com/?t=";

    private final String API_KEY = "&apikey=4912ae8";

    private ConvierteDatos conversor = new ConvierteDatos();

    private List <DatosSerie> datosSeries = new ArrayList<>();

    private SerieRepository repositorio;

    private List<Serie> series;

    private Optional<Serie> serieBuscada;

    public Principal(SerieRepository repository) {
        this.repositorio = repository;
    }

    public void muestraElMenu() {
        var opcion = -1;
        while (opcion != 0) {
            var menu = """
                    1 - Buscar series 
                    2 - Buscar episodios
                    3 - Mostrar series buscadas
                    4 - Buscar series por titulo
                    5 - Buscar top 5 series
                    6 - Buscar Series por cateforia
                    7 - Buscar Series con un máximos de 3 temporadas
                    8 - Buscar serie por nombre de episodio
                    9 - Top 5 episodios por Serie
                                  
                    0 - Salir
                    """;
            System.out.println(menu);
            opcion = teclado.nextInt();
            teclado.nextLine();

            switch (opcion) {
                case 1:
                    buscarSerieWeb();
                    break;
                case 2:
                    buscarEpisodioPorSerie();
                    break;
                case 3:
                    mostrarSeriesBuscadas();
                    break;
                case 4:
                    buscarSeriesPorTitulo();
                    break;
                case 5:
                    buscarTop5Series();
                    break;
                case 6:
                    buscarSeriesPorCategorias();
                    break;
                case 7:
                    buscarTotalTemp3();
                    break;
                case 8:
                    buscarEpisodioPorTitulo();
                    break;
                case 9:
                    top5EpisodiosPorSerie();
                case 0:
                    System.out.println("Cerrando la aplicación...");
                    break;
                default:
                    System.out.println("Opción inválida");
            }
        }

    }

    private DatosSerie getDatosSerie() {
        System.out.println("Escribe el nombre de la serie que deseas buscar");
        var nombreSerie = teclado.nextLine();
        var json = consumoApi.obtenerDatos(URL_BASE + nombreSerie.replace(" ", "+") + API_KEY);
        System.out.println(json);
        DatosSerie datos = conversor.obtenerDatos(json, DatosSerie.class);
        return datos;
    }

    private void buscarEpisodioPorSerie() {
        mostrarSeriesBuscadas();
        System.out.println("Escribe el nombre de la serie de la cuál quieres ver los episodios");
        var nombreSerie = teclado.nextLine();

        Optional<Serie> serie = series.stream()
                .filter(s -> s.getTitulo().toLowerCase().contains(nombreSerie.toLowerCase()))
                .findFirst();

        if (serie.isPresent()){
            var serieEncontrada =serie.get();
            List<DatosTemporada> temporadas = new ArrayList<>();

            for (int i = 1; i <= serieEncontrada.getTotalTemporadas(); i++) {
                var json = consumoApi.obtenerDatos(URL_BASE + serieEncontrada.getTitulo().replace(" ", "+") + "&season=" + i + API_KEY);
                DatosTemporada datosTemporada = conversor.obtenerDatos(json, DatosTemporada.class);
                temporadas.add(datosTemporada);
            }
            temporadas.forEach(System.out::println);
            List<Episodio> episodios = temporadas.stream()
                    .flatMap(d -> d.episodios().stream()
                            .map(e -> new Episodio(d.numero(), e)))
                    .collect(Collectors.toList());
            serieEncontrada.setEpisodios(episodios);
            repositorio.save(serieEncontrada);
        }

    }

    private void buscarSerieWeb() {
        DatosSerie datos = getDatosSerie();
        //datosSeries.add(datos);
        Serie serie = new Serie(datos);
        repositorio.save(serie);
        System.out.println(datos);
    }

    private void mostrarSeriesBuscadas() {
        series = repositorio.findAll();
//                new ArrayList<>();
//        historialSeriesBuscadas = datosSeries.stream()
//                .map(d -> new Serie(d))
//                .collect(Collectors.toList());
        series.stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);
    }

    private void buscarSeriesPorTitulo(){
        System.out.println("Escribe el nombre de la serie que deseas buscar:");
        var nombreSerie = teclado.nextLine();
        serieBuscada= repositorio.findByTituloContainsIgnoreCase(nombreSerie);

        if (serieBuscada.isPresent()){
            System.out.println("La serie buscada es: " + serieBuscada.get());
        }else {
            System.out.println("Serie no encontrada.");
        }
    }

    public void buscarTop5Series(){
        List<Serie> topSeries = repositorio.findTop5ByOrderByEvaluacionDesc();
        topSeries.forEach(s ->
                System.out.println("Serie: " + s.getTitulo() + "| Evaluación: " + s.getEvaluacion()));
    }

    private void buscarSeriesPorCategorias(){
        System.out.println("Escribe el genero/categoría de las series que desea visaulizar");
        var genero = teclado.nextLine();
        var categoria = Categoria.fromEspanol(genero);
        List<Serie> seriesPorCategoria = repositorio.findByGenero(categoria);
        System.out.println("Las series de la categoría " + genero + " son:");
        seriesPorCategoria.forEach(System.out::println);
    }

    private void buscarTotalTemp3(){
        System.out.println("Escriba el līmite de cuantas temporadas debe tener la seríe que desea ver: ");
        var limiteTemporadas = teclado.nextInt();
        System.out.println("Ingrese la valoración de la serie aceptable");
        var calificacionAceptable = teclado.nextDouble();

        List<Serie> filtroSeries = repositorio.seriesPorTemporadaYEvaluacion(limiteTemporadas, calificacionAceptable);

        System.out.println("**Series Filtradas ***");
        filtroSeries.forEach(s ->
                System.out.println(s.getTitulo() + " Evaluación " + s.getEvaluacion()));
    }

    private void buscarEpisodioPorTitulo(){
        System.out.println("Escribe el nombre del episodio que deseas buscar");
        var nombreEpisodio = teclado.nextLine();

        List<Episodio> episodiosEncontrados = repositorio.episodioPorNombre(nombreEpisodio);
        episodiosEncontrados.forEach(e->
                System.out.printf(" Serie: %s - Temporada %s - Episodio %s - Nombre: %s - Evaluación %s\n",
                        e.getSerie().getTitulo(), e.getTemporada(), e.getNumeroEpisodio(), e.getTitulo(), e.getEvaluacion()));
    }

    private void top5EpisodiosPorSerie(){
        buscarSeriesPorTitulo();
        if (serieBuscada.isPresent()){
            Serie serie = serieBuscada.get();
            List<Episodio> topEpisodios = repositorio.top5Episodios(serie);
            topEpisodios.forEach(e->
                    System.out.printf(" Serie: %s - Temporada %s - Episodio %s - Nombre: %s -  Evaluación %s\n",
                            e.getSerie().getTitulo(), e.getTemporada(), e.getNumeroEpisodio(), e.getTitulo(), e.getEvaluacion()));
        }
    }

}
