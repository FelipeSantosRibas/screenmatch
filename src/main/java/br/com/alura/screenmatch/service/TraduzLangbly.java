package br.com.alura.screenmatch.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class TraduzLangbly {
    private static String apiKey = "7BPLAvY74VZnjaNf5bwLAs";

    public static String traduzir(String texto){
        // Json da requisição post
        String json = "{" +
                "\"q\": \""+texto+"\"," +
                "\"target\": \"pt\"" +
                "}";


        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.langbly.com/language/translate/v2")) // Uri da requisição
                .header("Content-Type", "application/json") // Header dizendo q requisição está em JSON
                .header("X-API-Key", apiKey) // Header contendo chave API
                .POST(HttpRequest.BodyPublishers.ofString(json)) // Define a requisição como post e adiciona o corpo
                .build();

        HttpClient client = HttpClient.newHttpClient(); // Cria um cliente HTTP

        String traducao;
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString()); // Faz a requisição

            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(response.body());

            traducao = node.at("/data/translations/0/translatedText").asText();

        } catch (Exception e) {
            return "Erro ao traduzir";
        }


        return traducao;
    }




}
