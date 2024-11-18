package com.angelodev.screenmatch;

import com.angelodev.screenmatch.modelo.DatosEpisodio;
import com.angelodev.screenmatch.modelo.DatosSerie;
import com.angelodev.screenmatch.modelo.DatosTemporada;
import com.angelodev.screenmatch.service.ConsumoAPI;
import com.angelodev.screenmatch.service.ConvierteDatos;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class ScreenmatchApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(ScreenmatchApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		var consumoApi = new ConsumoAPI();
		var json = consumoApi.obtenerDatos("http://www.omdbapi.com/?t=silicon+valley&apikey=4912ae8");
		System.out.println(json);
		ConvierteDatos conversor = new ConvierteDatos();
		var datos = conversor.obtenerDatos(json, DatosSerie.class);
		System.out.println("Los datos son los siguientes: " + datos);

		json = consumoApi.obtenerDatos("http://www.omdbapi.com/?t=silicon+valley&season=1&episode=1&apikey=4912ae8");
		DatosEpisodio datosEpisodio = conversor.obtenerDatos(json, DatosEpisodio.class);
		System.out.println(datosEpisodio);

		System.out.println("#####DATOS DE TEMPORADAS:#######");

		List<DatosTemporada> temporadas = new ArrayList<>();
		for (int i = 1; i <=datos.totalTemporadas(); i++) {
			json = consumoApi.obtenerDatos("http://www.omdbapi.com/?t=silicon+valley&season="+i+"&apikey=4912ae8");
			var datosTemporadas = conversor.obtenerDatos(json, DatosTemporada.class);
			temporadas.add(datosTemporadas);
		}
		temporadas.forEach(System.out::println);
	}
}
