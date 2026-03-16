package br.com.alura.screenmatch;

import br.com.alura.screenmatch.model.DadosEpisodio;
import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.model.DadosTemporada;
import br.com.alura.screenmatch.service.ConsumoAPI;
import br.com.alura.screenmatch.service.ConverteDados;
import ch.qos.logback.core.encoder.ByteArrayUtil;
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

		// Obtém dados do fimle
		var consumoAPI = new ConsumoAPI();
		var json = consumoAPI.obterDados("https://www.omdbapi.com/?t=rick+and+morty&apikey=6585022c");
		System.out.println(json);

		// Converte desserializa dados
		ConverteDados conversor = new ConverteDados();
		DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
		System.out.println(dados);

		// Obtém e desserializa dados do episódio
		json = consumoAPI.obterDados("https://www.omdbapi.com/?t=rick+and+morty&season=1&episode=1&apikey=6585022c");
		DadosEpisodio dadosEpisodio = conversor.obterDados(json, DadosEpisodio.class);
		System.out.println(dadosEpisodio);

		List<DadosTemporada> temporadas = new ArrayList<>();

		// Temporadas
		for (int i = 1; i <= dados.totalTemporadas(); i++){
			json = consumoAPI.obterDados("https://www.omdbapi.com/?t=rick+and+morty&season="+i+"&apikey=6585022c");
			DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
			System.out.println(dadosTemporada);
			temporadas.add(dadosTemporada);
		}
	}
}
