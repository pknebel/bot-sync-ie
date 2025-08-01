package br.com.botcnpj.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import br.com.botcnpj.model.ApiCnpjResponse;

public class CnpjApiClientService {

    private static final String API_URL_BASE = "https://publica.cnpj.ws/cnpj/";
    private static final int REQUEST_DELAY_MS = 21000;

    private final HttpClient httpClient;
    private final Gson gson;
    private final Map<String, Optional<ApiCnpjResponse>> cache = new ConcurrentHashMap<>();

    public CnpjApiClientService(){
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.gson = new Gson();

    }

    public Optional<ApiCnpjResponse> fetchCnpjData(String cnpj){
        return cache.computeIfAbsent(cnpj, this::fetchFromApi);
    }

    private Optional<ApiCnpjResponse> fetchFromApi(String cnpj){
        try {

            System.out.println("Aguardando " + (REQUEST_DELAY_MS / 1000) + " segundos para respeitar o limite da API...");
            Thread.sleep(REQUEST_DELAY_MS);

            System.out.println("Consultando API para o CNPJ: " + cnpj);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL_BASE + cnpj))
                    .GET()
                    .build();
        
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if(response.statusCode() == 200){
            ApiCnpjResponse apiResponse = gson.fromJson(response.body(), ApiCnpjResponse.class);
            return Optional.ofNullable(apiResponse);
        } else {
            System.err.println("Erro na API para CNPJ: " + cnpj + ": Status " + response.statusCode());
            return Optional.empty();

        }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Consulta ao CNPJ interrompida: " + cnpj + ": " + e.getMessage());
            return Optional.empty();

        } catch (JsonSyntaxException e) {
            System.err.println("Erro ao parsear JSON para o CNPJ: " + cnpj + ": " + e.getMessage());
            return Optional.empty();

        } catch (Exception e) {
            System.err.println("Erro de conexão ao consultar o CNPJ: " + cnpj + ": " + e.getMessage());
            return Optional.empty();
        }
    }
}
