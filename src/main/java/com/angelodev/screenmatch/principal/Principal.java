package com.angelodev.screenmatch.principal;

import com.angelodev.screenmatch.modelo.DatosEpisodio;
import com.angelodev.screenmatch.modelo.DatosSerie;
import com.angelodev.screenmatch.modelo.DatosTemporada;
import com.angelodev.screenmatch.modelo.Episodio;
import com.angelodev.screenmatch.service.ConsumoAPI;
import com.angelodev.screenmatch.service.ConvierteDatos;

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

    private String menu = """
            (1) Mostrar los titulos de todos los episodios de la serie. 
            (2) Obtener Top 5 episodios
            """;

    private int opcionMenu = 0;

    public void muestraMenu(){
        System.out.println("********** BIENVENIDO AL SISTEMA DE CONSULTA STREAMING **********");
        System.out.println("Escribe el nombre de la serie que deseas buscar");
        var nombreSerie = teclado.nextLine();

        //SE OBTIENEN LOS DATOS GENERALES DE LA SERIE
        System.out.println("##### DATOS GENERALES #######");
        var json = consumoApi.obtenerDatos(URL_BASE + nombreSerie.replace(" ", "+") + API_KEY);
        var datos = conversor.obtenerDatos(json, DatosSerie.class);
        System.out.println(datos);

        //Se obtinen los datos de todas las temporadas
        System.out.println("\n##### TEMPORADAS DE " + nombreSerie.toUpperCase() + ":#######");
        List<DatosTemporada> temporadas = new ArrayList<>();
        for (int i = 1; i <=datos.totalTemporadas(); i++) {
            json = consumoApi.obtenerDatos(URL_BASE + nombreSerie.replace(" ", "+") + "&Season=" + i + API_KEY);
            var datosTemporadas = conversor.obtenerDatos(json, DatosTemporada.class);
            temporadas.add(datosTemporadas);
        }
        temporadas.forEach(System.out::println);

        System.out.println("Require buscar información adicional? Y/N");
        var respuestaUsuario = teclado.next().toUpperCase();

        if (respuestaUsuario == "Y"){
            System.out.println("Información adicional. Selecciona una opción del menu: \n ");
            opcionMenu = teclado.nextInt();
            switch (opcionMenu){
                case 1:
                    //Mostrar solo los titulos de todos los episodios de todas las temporadas
                    for (int i = 0; i < datos.totalTemporadas(); i++) {
                        List<DatosEpisodio> episodiosTemporada = temporadas.get(i).episodios();
                        for (int j = 0; j < episodiosTemporada.size(); j++) {
                            System.out.println(episodiosTemporada.get(j).titulo());
                        }
                    }
                    temporadas.forEach(t -> t.episodios().forEach(e -> System.out.println(e.titulo())));
                    break;
                case 2:
                    //Se convierten todas las informaciones a una lista del tipo DatosEpisodio
                    List <DatosEpisodio> datosEpisodios = temporadas.stream()
                            .flatMap(t -> t.episodios().stream())
                            .collect(Collectors.toList());

                    //Se obtiene el Top 5 de Episodios
                    System.out.println("Top 5 episodios");
                    datosEpisodios.stream()
                            .filter(e -> !e.evaluacion().equalsIgnoreCase("N/A"))
                            .peek(e -> System.out.println("Primer filtro (N/A)" + e))
                            .sorted(Comparator.comparing(DatosEpisodio::evaluacion).reversed())
                            .peek(e -> System.out.println("Segundo ordenación (M>m)" + e))
                            .map(e -> e.titulo().toUpperCase())
                            .peek(e -> System.out.println("Tercer filtro Mayúscula (m>M)" + e))
                            .limit(5)
                            .forEach(System.out::println);
                    break;
                default:
                    System.out.println("Valor no válido");
            }
        } else {
            System.out.println("Fin del sistema. ");
        }


        //Convirtiendo los datos a una lista del tipo Episodio
        List<Episodio> episodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream()
                        .map(d -> new Episodio(t.numero(),d)))
                .collect(Collectors.toList());
        episodios.forEach(System.out::println);

        //Busqueda de episodios a partir de un año dado por el usuario
        System.out.println("Ingresa el año a partir del cual deseas ver los episodios");
        var fecha = teclado.nextInt();
        teclado.nextLine();

        LocalDate fechaBusqueda = LocalDate.of(fecha, 1, 1 );

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        episodios.stream()
                .filter(e -> e.getFechaLanzamiento() != null && e.getFechaLanzamiento().isAfter(fechaBusqueda))
                .forEach(e -> System.out.println(
                        "Temporada " + e.getTemporada()
                        + " | Episodio " + e.getTitulo()
                        + " | Fecha " + e.getFechaLanzamiento().format(dtf)
                ));
        
        //Busca episodios por un pedazo del título 
        System.out.println("Por favor escriba el titulo del episodio que desea ver");
        var pedazoTitulo = teclado.nextLine();
        Optional<Episodio> episodioBuscar = episodios.stream()
                .filter(e -> e.getTitulo().toUpperCase().contains(pedazoTitulo.toUpperCase()))
                .findFirst();
        System.out.println("########## Resultados busqueda por nombre ##########");
        if (episodioBuscar.isPresent()){
            System.out.println(" Episodio encontrado");
            System.out.println("Los datos son: " + episodioBuscar.get());
        } else {
            System.out.println("Episodio no encontrado");
        }
        System.out.println("########## Fin de esultados busqueda por nombre ##########");


        System.out.println("\n##### Más datos generales #####");
        Map<Integer, Double> evaluacionesPorTemporada = episodios.stream()
                .filter(e -> e.getEvaluacion() > 0.0)
                .collect(Collectors.groupingBy(Episodio::getTemporada,
                        Collectors.averagingDouble(Episodio::getEvaluacion)));
        System.out.println("Las evaluaciones por temporada son; " + evaluacionesPorTemporada);

        DoubleSummaryStatistics est = episodios.stream()
                .filter(e -> e.getEvaluacion() > 0.0)
                .collect(Collectors.summarizingDouble(Episodio::getEvaluacion));
        System.out.println("Media de las evaluaciones: " + est.getAverage());
        System.out.println("Episodio Mejor evaluado: " + est.getMax());
        System.out.println("Episodio Peor evaluado: " + est.getMin());

    }

}
