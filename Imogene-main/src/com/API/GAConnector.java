package com.API;

import com.application.panels.ConnectionScreen;
import com.utils.BitMapImage;
import com.utils.Util;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
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
        String remote = ConnectionScreen.getInstance().getRemote();
        String jsonBody = "{\"generations\":" + generations + "}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(remote + "/ga/" + sessionId + "/run"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        client.send(request, HttpResponse.BodyHandlers.ofString());
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

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(remote + "/ga/" + sessionId + "/best"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        int[][][] rgb = Util.parse3DArray(response.body());
        return new BitMapImage(rgb);
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