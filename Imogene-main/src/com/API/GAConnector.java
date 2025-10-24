package com.API;

import com.application.panels.ConnectionScreen;
import com.utils.BitMapImage;
import com.utils.Util;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * GAConnector handles communication with the remote Genetic Algorithm (GA) service.
 * It provides methods to initialize a GA session, run generations, get status,
 * retrieve the best image, halt, and reset the GA session.
 */

public class GAConnector {

    private static final HttpClient client = HttpClient.newHttpClient();

    public static String init(Map<String, Object> params) throws IOException, InterruptedException {
        String remote = ConnectionScreen.getInstance().getRemote();
        String jsonBody = Util.mapToJson(params);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(remote + "/ga/init"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("Failed to initialize GA session: " + response.body());
        }

        return response.body();
    }

    public static void run(String sessionId, int generations) throws IOException, InterruptedException {
        if (sessionId == null || sessionId.isBlank()) {
            throw new IllegalArgumentException("sessionId is null or empty");
        }

        //  if the sessionId contains HTML tags it's likely an error page
        if (sessionId.contains("<") || sessionId.contains(">") || sessionId.contains("DOCTYPE")) {
            throw new IOException("Invalid sessionId received (looks like an HTML response). Check GAConnector.init and backend endpoints.");
        }

        // URL-encode the sessionId to avoid illegal characters in the URI path
        String encodedSessionId = URLEncoder.encode(sessionId, StandardCharsets.UTF_8);

        String remote = ConnectionScreen.getInstance().getRemote();
        URI uri = URI.create(remote + "/ga/" + encodedSessionId + "/run");

        String jsonBody = "{\"generations\":" + generations + "}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() / 100 != 2) {
            throw new IOException("Failed to start remote GA run: HTTP " + response.statusCode() + " - " + response.body());
        }
    }

    public static Map<String, Object> getStatus(String sessionId) throws IOException, InterruptedException {
        String remote = ConnectionScreen.getInstance().getRemote();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(remote + "/ga/" + sessionId + "/status"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return Util.jsonToMap(response.body());
    }

    public static BitMapImage getBestImage(String sessionId) throws IOException, InterruptedException {
        String remote = ConnectionScreen.getInstance().getRemote();

        // Build URI and ensure sessionId is safe in path
        String encodedId = URLEncoder.encode(sessionId, StandardCharsets.UTF_8);
        URI uri = URI.create(remote + "/ga/" + encodedId + "/best");

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String body = response.body();

        // Basic validations
        if (response.statusCode() != 200) {
            throw new IOException("Backend returned status " + response.statusCode() + " for getBestImage. Response snippet: " + snippet(body));
        }

        String contentType = response.headers().firstValue("Content-Type").orElse("");
        if (contentType.contains("text/html") || body.trim().startsWith("<!DOCTYPE") || body.trim().startsWith("<html")) {
            throw new IOException("Backend returned HTML instead of JSON/array for getBestImage. Response snippet: " + snippet(body));
        }

        // Try parsing the expected 3D array and wrap parse errors with response snippet
        try {
            int[][][] rgb = Util.parse3DArray(body);
            return new BitMapImage(rgb);
        } catch (IllegalArgumentException ex) {
            throw new IOException("Invalid 3D array format received from backend. Response snippet: " + snippet(body), ex);
        }
    }

    private static String snippet(String s) {
        if (s == null) return "";
        String trimmed = s.trim();
        int max = 400;
        return trimmed.length() <= max ? trimmed : trimmed.substring(0, max) + "...";
    }

    public static void halt(String sessionId) throws IOException, InterruptedException {
        String remote = ConnectionScreen.getInstance().getRemote();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(remote + "/ga/" + sessionId + "/halt"))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public static void reset(String sessionId) throws IOException, InterruptedException {
        String remote = ConnectionScreen.getInstance().getRemote();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(remote + "/ga/" + sessionId + "/reset"))
                .POST(HttpRequest.BodyPublishers.noBody()) // Or .DELETE()
                .build();

        client.send(request, HttpResponse.BodyHandlers.ofString());
    }
}