package com.backend;

import com.GA.ImageGenerator;
import com.GA.generation.RandomColorGeneration;
import com.application.panels.ImageScreen;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.utils.BitMapImage;
import com.utils.ImageUtils;
import com.utils.Util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class BackendApplication {

    private static final Map<String, BitMapImage> gaSessions = new ConcurrentHashMap<>();
    private static final Map<String, Map<String, Object>> gaStatuses = new ConcurrentHashMap<>();

    public static void main(String[] args) throws IOException {
        int portNumber = 8080;
        HttpServer server = HttpServer.create(new InetSocketAddress(portNumber), 0);

        // Assign handlers to endpoints
        server.createContext("/", new RootHandler());
        server.createContext("/generate", new GenerationHandler());
        server.createContext("/filter", new FilterHandler());
        server.createContext("/ga/init", new GAInitHandler());
        server.createContext("/ga/", new GAHandler());

        server.setExecutor(null);
        server.start();

        System.out.println("Server started on http://localhost:" + portNumber);
    }


    //new method to extract new json params for rgb smooth and other filters


    private static String[] extractJsonParams(String json) {
        String search = "\"params\":[";
        int start = json.indexOf(search);
        if (start == -1) return new String[0];

        int arrayStart = start + search.length();
        int arrayEnd = json.indexOf(']', arrayStart);
        if (arrayEnd == -1) return new String[0];

        String paramsContent = json.substring(arrayStart, arrayEnd);
        if (paramsContent.trim().isEmpty()) return new String[0];

        // split by commas and trim whitespace
        String[] rawParams = paramsContent.split(",");
        for (int i = 0; i < rawParams.length; i++) {
            rawParams[i] = rawParams[i].trim();
        }
        return rawParams;
    }




    static class GAInitHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                    exchange.sendResponseHeaders(405, -1); // Method not allowed
                    return;
                }

                // Read request body (unused for now)
                InputStream is = exchange.getRequestBody();
                String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);

                // Generate a session id and store a minimal session image
                String sessionId = UUID.randomUUID().toString();

                // Create a small initial image so GET /ga/{id}/best returns JSON immediately
                BitMapImage initial = ImageGenerator.randomPixels(16, 16);
                gaSessions.put(sessionId, initial);

                // Return plain text session id
                byte[] respBytes = sessionId.getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=utf-8");
                exchange.sendResponseHeaders(200, respBytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(respBytes);
                }

                System.out.println("Created GA session: " + sessionId + " (body: " + body + ")");
            } catch (Exception e) {
                exchange.sendResponseHeaders(500, -1);
                e.printStackTrace();
            }
        }
    }

    static class GAHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                String method = exchange.getRequestMethod();
                String path = exchange.getRequestURI().getPath(); // e.g. /ga/{id}/best
                String prefix = "/ga/";
                if (!path.startsWith(prefix)) {
                    sendPlain(exchange, 404, "");
                    return;
                }

                // Extract parts after /ga/
                String remainder = path.substring(prefix.length()); // {id}/best or init etc.
                String[] parts = remainder.split("/");

                // Endpoint: POST /ga/init  -> create new session (optional)
                if ("POST".equalsIgnoreCase(method) && remainder.equals("init")) {
                    String sessionId = UUID.randomUUID().toString();
                    // Create an initial random image for the session (small example)
                    BitMapImage img = ImageGenerator.randomPixels(100, 100);
                    gaSessions.put(sessionId, img);
                    gaStatuses.put(sessionId, new ConcurrentHashMap<>()); // empty status
                    Map<String, Object> statusMap = gaStatuses.get(sessionId);
                    statusMap.put("running", false);
                    statusMap.put("generation", 0);

                    String json = "{\"sessionId\":\"" + sessionId + "\"}";
                    sendJson(exchange, 200, json);
                    return;
                }

                // Expect at least an id segment
                if (parts.length < 1 || parts[0].isEmpty()) {
                    sendPlain(exchange, 404, "");
                    return;
                }

                String sessionId = parts[0];

                // POST /ga/{id}/run  -> start GA run (accepts optional body, ignored here)
                if ("POST".equalsIgnoreCase(method) && remainder.endsWith("/run")) {
                    if (!gaSessions.containsKey(sessionId)) {
                        sendPlain(exchange, 404, "");
                        return;
                    }
                    Map<String, Object> statusMap = gaStatuses.computeIfAbsent(sessionId, k -> new ConcurrentHashMap<>());
                    statusMap.put("running", true);
                    statusMap.put("generation", 0);

                    // Optionally parse body for generations and start background task.
                    // For now, just acknowledge.
                    sendPlain(exchange, 200, "");
                    return;
                }

                // POST /ga/{id}/halt -> stop GA run
                if ("POST".equalsIgnoreCase(method) && remainder.endsWith("/halt")) {
                    if (!gaSessions.containsKey(sessionId)) {
                        sendPlain(exchange, 404, "");
                        return;
                    }
                    Map<String, Object> statusMap = gaStatuses.computeIfAbsent(sessionId, k -> new ConcurrentHashMap<>());
                    statusMap.put("running", false);
                    sendPlain(exchange, 200, "");
                    return;
                }

                // POST /ga/{id}/reset -> remove session
                if ("POST".equalsIgnoreCase(method) && remainder.endsWith("/reset")) {
                    gaSessions.remove(sessionId);
                    gaStatuses.remove(sessionId);
                    sendPlain(exchange, 200, "");
                    return;
                }

                // GET /ga/{id}/status -> return JSON { running: bool, generation: int }
                if ("GET".equalsIgnoreCase(method) && remainder.endsWith("/status")) {
                    if (!gaSessions.containsKey(sessionId)) {
                        sendPlain(exchange, 404, "");
                        return;
                    }
                    Map<String, Object> statusMap = gaStatuses.computeIfAbsent(sessionId, k -> {
                        Map<String, Object> m = new ConcurrentHashMap<>();
                        m.put("running", false);
                        m.put("generation", 0);
                        return m;
                    });
                    // Build JSON
                    boolean running = Boolean.TRUE.equals(statusMap.getOrDefault("running", false));
                    int generation = ((Number) statusMap.getOrDefault("generation", 0)).intValue();
                    String json = "{\"running\":" + running + ",\"generation\":" + generation + "}";
                    sendJson(exchange, 200, json);
                    return;
                }

                // GET /ga/{id}/best -> return stored image JSON
                if ("GET".equalsIgnoreCase(method) && remainder.endsWith("/best")) {
                    BitMapImage img = gaSessions.get(sessionId);
                    if (img == null) {
                        sendPlain(exchange, 404, "");
                        return;
                    }
                    String json = Util.arrayToJson(img.getRgb());
                    sendJson(exchange, 200, json);
                    return;
                }

                // Unknown GA sub-endpoint
                sendPlain(exchange, 404, "");
            } catch (Exception e) {
                e.printStackTrace();
                sendPlain(exchange, 500, "Internal error");
            }
        }
    }


    private static void sendJson(HttpExchange exchange, int status, String body) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        byte[] bytes = body == null ? new byte[0] : body.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private static void sendPlain(HttpExchange exchange, int status, String body) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=utf-8");
        byte[] bytes = body == null ? new byte[0] : body.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
    static class RootHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = "<!DOCTYPE html>\n" + // TODO: move to a separate html file
                    "<html lang=\"en\">\n" +
                    "<head>\n" +
                    "  <title>Imogene API</title>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "\t<p>\n" +
                    "\t\tWelcome to Imogene API.\n" +
                    "\t</p>\n" +
                    "</body>\n" +
                    "</html>";
            exchange.sendResponseHeaders(200, response.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }

    static class GenerationHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                if (!"GET".equals(exchange.getRequestMethod())) {
                    exchange.sendResponseHeaders(405, -1); // Method not allowed
                    return;
                }

                // Parse query parameters
                Map<String, String> params = Util.queryToMap(exchange.getRequestURI().getQuery());
                int height = Integer.parseInt(params.getOrDefault("height", "0"));
                int width = Integer.parseInt(params.getOrDefault("width", "0"));
                String type = params.getOrDefault("type", "");

                BitMapImage image = new BitMapImage(width, height);

                if(type.equalsIgnoreCase("randomBitmap"))
                    image = ImageGenerator.randomPixels(height, width);

                if(type.equalsIgnoreCase("randomColour"))
                    image = (new RandomColorGeneration()).generate(height, width).getImage();


                String json = Util.arrayToJson(image.getRgb());
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, json.getBytes().length);

                OutputStream os = exchange.getResponseBody();
                os.write(json.getBytes());
                os.close();


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    static class FilterHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                if (!"POST".equals(exchange.getRequestMethod())) {
                    exchange.sendResponseHeaders(405, -1); // Method not allowed
                    return;
                }

                // Read request body
                InputStream is = exchange.getRequestBody();
                String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);

                String type = extractJsonValue(body, "type");
                String imageString = extractJsonValue(body, "image");

                // neeed to extract the new params array
                String[] params = extractJsonParams(body);

                int[][][] rgb = Util.parse3DArray(imageString);
                BitMapImage image = new BitMapImage(rgb);

                if(type.equalsIgnoreCase("grayscale"))
                    image = ImageUtils.grayscale(image);

                if(type.equalsIgnoreCase("invert"))
                    image = ImageUtils.invert(image);

                //smoothing filters (2 x 2 Params)-----------------------
                if(type.equalsIgnoreCase("smoothSoft") ||
                        type.equalsIgnoreCase("smoothMedium") ||
                        type.equalsIgnoreCase("smoothHard")) {

                    // (alpha, beta)
                    double alpha = Double.parseDouble(params[0].trim().replace("\"", ""));
                    double beta = Double.parseDouble(params[1].trim().replace("\"", ""));
                    image = ImageUtils.smoothFilter(image, alpha, beta);
                }

                //Rebal filters (3 x 2 params)-------------------
                if(type.equalsIgnoreCase("rgbRebalance")) {
                    // Parse parameters for rgbBalancing (r, g, b ratios)
                    double r = Double.parseDouble(params[0].trim().replace("\"", ""));
                    double g = Double.parseDouble(params[1].trim().replace("\"", ""));
                    double b = Double.parseDouble(params[2].trim().replace("\"", ""));
                    image = ImageUtils.rgbBalancing(image, r, g, b);
                }

                //Spectrum Projection Filters (2 String Params) -------------------
                if(type.equalsIgnoreCase("spectralProjection")) {
                    String source = params[0].trim().replace("\"", "");
                    String destination = params[1].trim().replace("\"", "");
                    image = ImageUtils.spectralProjection(image, source, destination);
                }

                String json = Util.arrayToJson(image.getRgb());
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, json.getBytes().length);

                OutputStream os = exchange.getResponseBody();
                os.write(json.getBytes());
                os.close();

            } catch (Exception e) {
                exchange.sendResponseHeaders(500, -1);
                e.printStackTrace();
            }
        }
    }

    private static String extractJsonValue(String json, String key) {
        String search = "\"" + key + "\"";
        int start = json.indexOf(search);
        if (start == -1) return null;
        int colon = json.indexOf(':', start);
        int firstQuote = json.indexOf('"', colon + 1);
        int secondQuote = json.indexOf('"', firstQuote + 1);
        return json.substring(firstQuote + 1, secondQuote);
    }

}
