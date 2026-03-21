package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.DadosEpisodio;
import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.model.DadosTemporada;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.service.ConsumoAPI;
import br.com.alura.screenmatch.service.ConverteDados;
import br.com.alura.screenmatch.view.TerminalView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class Principal {
    private Scanner leitura = new Scanner(System.in);
    private ConsumoAPI consumo = new ConsumoAPI();
    private ConverteDados conversor = new ConverteDados();
    private TerminalView view = new TerminalView();

    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=6585022c";

    public void exibeMenu(){

        DadosSerie dados = null;
        String json = null;
        String nomeSerie = null;

        // Ciclo até encontrar série
        var prosseguir = true;
        while(prosseguir) {
            // Pergunta nome da série
            nomeSerie = view.perguntaNomeSerie();

            // Verificações da série
            try {
                // Procura série e desserializa
                json = consumo.obterDados(ENDERECO + nomeSerie.replaceAll(" ", "+") + API_KEY);
                dados = conversor.obterDados(json, DadosSerie.class);

                // Verifica erros
                if (!dados.tipo().equalsIgnoreCase("series")) { // Título não é uma série
                    System.out.println("Título encontrado não é uma série");
                    view.perguntaTentarNovamente();

                } else { // Série encontrada
                    prosseguir = view.serieEncontrada(dados);
                }

            } catch (Exception e){ // Série não encontrada
                System.out.println("Não foi possível encontrar a série");
                view.perguntaTentarNovamente();
            }
        }

        // Dados temporadas
        List<DadosTemporada> temporadas = new ArrayList<>();
        for (int i = 1; i <= dados.totalTemporadas(); i++){
            json = consumo.obterDados(ENDERECO + nomeSerie.replaceAll(" ", "+") + "&season="+i + API_KEY);
            DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
            //System.out.println(dadosTemporada);
            temporadas.add(dadosTemporada);
        }

        // Dados temporadas -> Dados episódios
        List<DadosEpisodio> todosEpisodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream())
                .toList();

        // Dados episódio -> Episódios
        List<Episodio> episodios = temporadas.stream()
                .flatMap(t -> t.episodios()
                        .stream().map(d -> new Episodio(t.numero(), d)))
                .toList();
        //episodios.forEach(System.out::println);


        prosseguir = true;
        while(prosseguir) {
            var opcao = view.perguntaEstatisticas();
            switch (opcao){
                // Série
                case "0" -> view.estatisticasSerie(dados);
                // Todas as temporadas
                case "1" -> view.estatisticasTodasTemporadas(temporadas, episodios);
                // Busca por temporada
                case "2" -> view.estatisticasTemporada(temporadas, episodios);
                // Todos os episódios
                case "3" -> view.estatisticasEpisodios(episodios);
                // Busca por episódio
            }
            prosseguir = true;
        }



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


//        // Estatísticas dos episódios
//        DoubleSummaryStatistics est = episodios.stream()
//                .filter(e -> e.getAvaliacao() != null)
//                .collect(Collectors.summarizingDouble(Episodio::getAvaliacao));
//        System.out.println("Média: "+est.getAverage());
//        System.out.println("Melhor episódio: "+est.getMax());
//        System.out.println("Pior episódio: "+est.getMin());
//        System.out.println("Quantidade de avaliações: "+est.getCount());
    }
}