package com.angelodev.screenmatch.principal;

import com.angelodev.screenmatch.modelo.DatosEpisodio;
import com.angelodev.screenmatch.modelo.DatosSerie;
import com.angelodev.screenmatch.modelo.DatosTemporada;
import com.angelodev.screenmatch.service.ConsumoAPI;
import com.angelodev.screenmatch.service.ConvierteDatos;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Principal {

    private ConsumoAPI consumoApi = new ConsumoAPI();

    private Scanner teclado = new Scanner(System.in);

    private final String URL_BASE = "http://www.omdbapi.com/?t=";

    private final String API_KEY = "&apikey=4912ae8";

    private ConvierteDatos conversor = new ConvierteDatos();

    public void muestraMenu(){
        System.out.println("Escribe el nombre de la serie que deseas buscar");
        var nombreSerie = teclado.nextLine();

        //Se otienen los datos generales de la serie
        var json = consumoApi.obtenerDatos(URL_BASE + nombreSerie.replace(" ", "+") + API_KEY);
        var datos = conversor.obtenerDatos(json, DatosSerie.class);
        System.out.println("##### DATOS GENERALES #######");
        System.out.println(datos);

        //Se obtinen los datos de todas las temporadas
        System.out.println("\n#####DATOS DE TEMPORADAS:#######");
        List<DatosTemporada> temporadas = new ArrayList<>();
        for (int i = 1; i <=datos.totalTemporadas(); i++) {
            json = consumoApi.obtenerDatos(URL_BASE + nombreSerie.replace(" ", "+") + "&Season=" + i + API_KEY);
            var datosTemporadas = conversor.obtenerDatos(json, DatosTemporada.class);
            temporadas.add(datosTemporadas);
        }
        //temporadas.forEach(System.out::println);

//        //Mostrar solo los titulos de todos los episodios de todas las temporadas
//        for (int i = 0; i < datos.totalTemporadas(); i++) {
//            List<DatosEpisodio> episodiosTemporada = temporadas.get(i).episodios();
//            for (int j = 0; j < episodiosTemporada.size(); j++) {
//                System.out.println(episodiosTemporada.get(j).titulo());
//            }
//        }
        temporadas.forEach(t -> t.episodios().forEach(e -> System.out.println(e.titulo())));
    }

}
