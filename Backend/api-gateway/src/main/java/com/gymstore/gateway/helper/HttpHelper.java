package com.gymstore.gateway.helper;

import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Component
public class HttpHelper {

    private final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(6))
            .build();

    public HttpResponse<String> get(String url, String bearerToken) throws Exception {
        return client.send(
                HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Authorization", "Bearer " + bearerToken)
                        .GET()
                        .build(),
                HttpResponse.BodyHandlers.ofString());
    }

    public HttpResponse<String> postJson(String url, String bearerToken, String jsonBody) throws Exception {
        return client.send(
                HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Authorization", "Bearer " + bearerToken)
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                        .build(),
                HttpResponse.BodyHandlers.ofString());
    }

    public HttpResponse<String> putJson(String url, String bearerToken, String jsonBody) throws Exception {
        return client.send(
                HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Authorization", "Bearer " + bearerToken)
                        .header("Content-Type", "application/json")
                        .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                        .build(),
                HttpResponse.BodyHandlers.ofString());
    }

    public HttpResponse<String> postForm(String url, String formBody) throws Exception {
        return client.send(
                HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Content-Type", "application/x-www-form-urlencoded")
                        .POST(HttpRequest.BodyPublishers.ofString(formBody))
                        .build(),
                HttpResponse.BodyHandlers.ofString());
    }
}
