package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.DadosEpisodio;
import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.model.DadosTemporada;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.service.ConsumoAPI;
import br.com.alura.screenmatch.service.ConverteDados;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class Principal {
    private Scanner leitura = new Scanner(System.in);
    private ConsumoAPI consumo = new ConsumoAPI();
    private ConverteDados conversor = new ConverteDados();

    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=6585022c";

    public void exibeMenu(){
        System.out.println("Digite o nome da série para pesquisar:");
        var nomeSerie = leitura.nextLine();
        var json = consumo.obterDados(ENDERECO + nomeSerie.replaceAll(" ", "+") + API_KEY);
        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
        //System.out.println(dados);

        List<DadosTemporada> temporadas = new ArrayList<>();

		// Temporadas
		for (int i = 1; i <= dados.totalTemporadas(); i++){
			json = consumo.obterDados(ENDERECO + nomeSerie.replaceAll(" ", "+") + "&season="+i + API_KEY);
			DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
			//System.out.println(dadosTemporada);
			temporadas.add(dadosTemporada);
		}

        // Imprime os títulos em todos os episódios
        // temporadas.forEach(t -> t.episodios().forEach(e -> System.out.println(e.titulo())));

        // Temporadas -> lista de episódios
        List<DadosEpisodio> todosEpisodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream())
                .collect(Collectors.toList());

//        // Top 10 episódios
//        todosEpisodios.stream() // Torna stream
//                .filter(e -> !e.avaliacao().equals("N/A")) // Remove avaliações N/A
//                .sorted(Comparator.comparing(DadosEpisodio::avaliacao).reversed()) // Ordena os valores por avaliacao
//                .limit(10) // Limita a 5
//                .map(e -> e.titulo().toUpperCase())
//                .forEach(System.out::println); // Imprime cada um

        // DadosEpisodio -> Episodios
        List<Episodio> episodios = temporadas.stream()
                .flatMap(t -> t.episodios()
                .stream().map(d -> new Episodio(t.numero(), d)))
                .collect(Collectors.toList());

        episodios.forEach(System.out::println);


//        // Busca por título
//        System.out.println("Qual episódio deseja buscar?");
//        var trechoTitulo = leitura.nextLine();
//        Optional<Episodio> episodioBuscado = episodios.stream()
//                .filter(e -> e.getTitulo().toLowerCase().contains(trechoTitulo.toLowerCase()))
//                .findFirst();
//        if (episodioBuscado.isPresent()){
//            System.out.println("Episódio encontrado!");
//            System.out.println("Temporada: "+ episodioBuscado.get());
//        } else{
//            System.out.println("Episódio não encontrado");
//        }

//        // Busca por ano
//        System.out.println("A partir de que ano você deseja ver os episódios?");
//        var ano = leitura.nextInt();
//        leitura.nextLine();
//
//        LocalDate dataBusca = LocalDate.of(ano, 1, 1);
//
//        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyy");
//        episodios.stream()
//                .filter(e -> e.getDataLancamento() != null &&
//                        (e.getDataLancamento().isAfter(dataBusca)
//                        || e.getDataLancamento().isEqual(dataBusca)
//                        )
//                )
//                .forEach(e -> System.out.println(
//                        "Temporada: "+e.getTemporada()+
//                                ", Episódio: "+e.getTitulo()+
//                                ", Data lançamento: "+e.getDataLancamento().format(formatador)
//                ));

        Map<Integer, Double> avaliacoesPorTemporada = episodios.stream()
                .filter(e -> e.getAvaliacao() != null)
                .collect(Collectors.groupingBy(Episodio::getTemporada,
                        Collectors.averagingDouble(Episodio::getAvaliacao)));
        System.out.println(avaliacoesPorTemporada);
    }
}