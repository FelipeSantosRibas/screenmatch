package br.com.alura.screenmatch.view;

import br.com.alura.screenmatch.model.DadosEpisodio;
import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.model.DadosTemporada;
import br.com.alura.screenmatch.model.Episodio;

import java.util.*;
import java.util.stream.Collectors;

public class TerminalView {
    private Scanner leitura = new Scanner(System.in);

    public String perguntaNomeSerie(){
        // Pergunta nome da série
        System.out.println("Digite o nome de uma série para pesquisar:");
        return leitura.nextLine();
    }

    public void perguntaTentarNovamente(){
        System.out.println("Deseja tentar novamente? (s/n)");

        String resposta = null;
        do {
            resposta = leitura.nextLine();
        }while(!resposta.equalsIgnoreCase("s") && !resposta.equalsIgnoreCase("n"));

        if (resposta.equalsIgnoreCase("n")){
            System.exit(0);
        }
    }

    public boolean serieEncontrada(DadosSerie dados) {
        System.out.println("Série encontrada: ");
        System.out.println("Título: " + dados.titulo());
        System.out.println("Ano: " + dados.ano());
        System.out.println("Temporadas: " + dados.totalTemporadas());
        System.out.println("Era essa a série que procurava? (s/n)");
        String resposta = null;
        do {
            resposta = leitura.nextLine();
        }while(!resposta.equalsIgnoreCase("s") && !resposta.equalsIgnoreCase("n"));

        if (resposta.equalsIgnoreCase("s")){
            return false;
        } else{
            perguntaTentarNovamente();
            return true;
        }
    }

    public String perguntaEstatisticas() {
        System.out.println("Quais estatísticas deseja ver?");
        System.out.println("[0] Série");
        System.out.println("[1] Todas as temporadas");
        System.out.println("[2] Buscar por temporada");
        System.out.println("[3] Todos episódios");
        System.out.println("[4] Buscar por episódio");
        String opcao = leitura.nextLine();
        while (!opcao.matches("[0-4]")){
            System.out.println("Por favor, insira uma opção válida");
            opcao = leitura.nextLine();
        }
        return opcao;
    }

    public void estatisticasSerie(DadosSerie dadosSerie){
        System.out.println("Estatísticas da série "+dadosSerie.titulo()+":");
        System.out.println("Total de temporadas: "+dadosSerie.totalTemporadas());
        System.out.println("Avaliação no IMDB: "+dadosSerie.avaliacao());
        System.out.println("Ano: "+dadosSerie.ano());
        System.out.println("Prêmios: "+dadosSerie.premios());
    }

    public void estatisticasTodasTemporadas(List<DadosTemporada> temporadas, List<Episodio> episodios) {
        // Média de cada temporada
        Map<Integer, Double> avaliacoesPorTemporada = episodios.stream()
                .filter(e -> e.getAvaliacao() != null)
                .collect(Collectors.groupingBy(Episodio::getTemporada,
                        Collectors.averagingDouble(Episodio::getAvaliacao)));
        avaliacoesPorTemporada.replaceAll( (k, v) -> (double) Math.round(v * 10.0) / 10.0);


        System.out.println("Total de temporadas: " + temporadas.size());
        System.out.println("Médias de avaliações por temporada: " + avaliacoesPorTemporada.toString().substring(1, avaliacoesPorTemporada.toString().length() - 1));
    }

    public void estatisticasTemporada(List<DadosTemporada> temporadas, List<Episodio> episodios) {
        System.out.println("Qual temporada deseja ver ("+temporadas.size()+" temporadas)?");
        String opcao = leitura.nextLine();
        while (!opcao.matches("[1-"+temporadas.size()+"]")){
            System.out.println("Por favor, insira uma opção válida");
            opcao = leitura.nextLine();
        }

        DadosTemporada temporada = temporadas.get(Integer.valueOf(opcao)-1);
        final int NUMERO_TEMPORADA = Integer.valueOf(opcao)-1;

        // Estatísticas dos episódios da temporada
        DoubleSummaryStatistics est = episodios.stream()
                .filter(e -> e.getAvaliacao() != null)
                .filter(e -> e.getTemporada() == NUMERO_TEMPORADA)
                .collect(Collectors.summarizingDouble(Episodio::getAvaliacao));

        // Melhor episódio
        DadosEpisodio melhor = temporada.episodios().stream()
                .filter(e -> !e.avaliacao().equalsIgnoreCase("N/A"))
                .max(Comparator.comparing(DadosEpisodio::avaliacao))
                .stream().toList()
                .get(0);

        // Pior episódio
        DadosEpisodio pior = temporada.episodios().stream()
                .filter(e -> !e.avaliacao().equalsIgnoreCase("N/A"))
                .min(Comparator.comparing(DadosEpisodio::avaliacao))
                .stream().toList()
                .get(0);

        System.out.println("Média de avaliações da temporada: "+est.getAverage());
        System.out.println("Quantidade de episódios: "+temporada.episodios().size());
        System.out.println("Melhor episódio: "+melhor.toString().substring(14,melhor.toString().length()-1));
        System.out.println("Pior episódio: "+pior.toString().substring(14,pior.toString().length()-1));
    }

    public void estatisticasTodosEpisodios(List<Episodio> episodios) {

        // Estatísticas de episódios
        DoubleSummaryStatistics est = episodios.stream()
                .filter(e -> e.getAvaliacao() != null)
                .collect(Collectors.summarizingDouble(Episodio::getAvaliacao));

        // Top 10 episódios
        List<String> top10Episodios = new ArrayList<>();

        episodios.stream() // Torna stream
                .filter(e -> e.getAvaliacao() != null) // Remove avaliações N/A
                .sorted(Comparator.comparing(Episodio::getAvaliacao).reversed()) // Ordena os valores por avaliacao
                .limit(10) // Limita a 10
                .forEach(e -> top10Episodios.add(e.getTitulo())); // Imprime cada um

        //episodios.forEach(System.out::println);
        System.out.println("Total de episódios: "+episodios.size());
        System.out.println("Média de avaliações dos episódios: "+est.getAverage());
        System.out.println("Melhor nota: "+est.getMax());
        System.out.println("Pior nota: "+est.getMin());
        System.out.println("Top 10 episódios: "+top10Episodios);


    }

    public void estatisticasEpisodios(List<Episodio> episodios){
        System.out.println("Digite o nome do episódio que deseja buscar:");

        var trechoTitulo = leitura.nextLine();
        Optional<Episodio> episodioBuscado = episodios.stream()
                .filter(e -> e.getTitulo().toLowerCase().contains(trechoTitulo.toLowerCase()))
                .findFirst();
        if (episodioBuscado.isPresent()){
            System.out.println("Episódio encontrado!");
            System.out.println(episodioBuscado.get());
        } else{
            System.out.println("Episódio não encontrado");
        }

    }
}
