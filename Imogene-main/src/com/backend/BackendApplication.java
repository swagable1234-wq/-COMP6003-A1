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

public class BackendApplication {

    public static void main(String[] args) throws IOException {
        int portNumber = 8080;
        HttpServer server = HttpServer.create(new InetSocketAddress(portNumber), 0);

        // Assign handlers to endpoints
        server.createContext("/", new RootHandler());
        server.createContext("/generate", new GenerationHandler());
        server.createContext("/filter", new FilterHandler());

        server.setExecutor(null);
        server.start();

        System.out.println("Server started on http://localhost:" + portNumber);
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

                int[][][] rgb = Util.parse3DArray(imageString);
                BitMapImage image = new BitMapImage(rgb);

                if(type.equalsIgnoreCase("grayscale"))
                    image = ImageUtils.grayscale(image);

                if(type.equalsIgnoreCase("invert"))
                    image = ImageUtils.invert(image);

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
