package com.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {

    public static final Random rng = new Random();

    /**
     * Converts a Map to a JSON string representation.
     * Supports nested Maps.
     */
    public static String mapToJson(Map<String, Object> map) {
        StringBuilder json = new StringBuilder();
        json.append("{");
        boolean first = true;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (!first) {
                json.append(",");
            }
            first = false;
            json.append("\"").append(entry.getKey()).append("\":");
            Object value = entry.getValue();
            if (value instanceof String) {
                json.append("\"").append(value).append("\"");
            } else if (value instanceof Map) {
                json.append(mapToJson((Map<String, Object>) value));
            } else if (value instanceof Object[]) {
                json.append(arrayToJson((Object[]) value));
            }
            else {
                json.append(value);
            }
        }
        json.append("}");
        return json.toString();
    }

    /**
     * Converts an Object array to a JSON array string.
     */
    private static String arrayToJson(Object[] array) {
        StringBuilder json = new StringBuilder();
        json.append("[");
        for (int i = 0; i < array.length; i++) {
            Object item = array[i];
            if (item instanceof String) {
                json.append("\"").append(item).append("\"");
            } else {
                json.append(item);
            }
            if (i < array.length - 1) {
                json.append(",");
            }
        }
        json.append("]");
        return json.toString();
    }


    /**
     * Parses a simple JSON string into a Map.
     * This is a basic parser and may not handle all JSON complexities.
     */
    public static Map<String, Object> jsonToMap(String json) {
        Map<String, Object> map = new HashMap<>();
        if (json == null || json.length() < 2) return map;
        json = json.substring(1, json.length() - 1).trim(); // Remove curly braces

        Pattern pattern = Pattern.compile("\"(.*?)\"\\s*:\\s*(\".*?\"|\\d+\\.?\\d*|true|false|null)");
        Matcher matcher = pattern.matcher(json);

        while (matcher.find()) {
            String key = matcher.group(1);
            String valueStr = matcher.group(2);
            Object value;
            if (valueStr.startsWith("\"")) {
                value = valueStr.substring(1, valueStr.length() - 1);
            } else if (valueStr.contains(".")) {
                value = Double.parseDouble(valueStr);
            } else if (valueStr.equals("true") || valueStr.equals("false")) {
                value = Boolean.parseBoolean(valueStr);
            } else if (valueStr.equals("null")) {
                value = null;
            } else {
                try {
                    value = Integer.parseInt(valueStr);
                } catch (NumberFormatException e) {
                    value = Long.parseLong(valueStr);
                }
            }
            map.put(key, value);
        }
        return map;
    }

    /**
     * Converts a 3D integer array to its JSON string representation.
     */
    public static String arrayToJson(int[][][] array) {
        StringBuilder json = new StringBuilder();
        json.append("[");
        for (int i = 0; i < array.length; i++) {
            json.append("[");
            for (int j = 0; j < array[i].length; j++) {
                json.append("[");
                for (int k = 0; k < array[i][j].length; k++) {
                    json.append(array[i][j][k]);
                    if (k < array[i][j].length - 1) {
                        json.append(",");
                    }
                }
                json.append("]");
                if (j < array[i].length - 1) {
                    json.append(",");
                }
            }
            json.append("]");
            if (i < array.length - 1) {
                json.append(",");
            }
        }
        json.append("]");
        return json.toString();
    }

    /**
     * Parses a JSON string representing a 3D array into an int[][][].
     */
    public static int[][][] parse3DArray(String json) {
        json = json.replaceAll("\\s", "");
        if (!json.startsWith("[[[") || !json.endsWith("]]]")) {
            throw new IllegalArgumentException("Invalid 3D array format");
        }
        json = json.substring(1, json.length() - 1); // Remove outer brackets

        List<int[][]> listY = new ArrayList<>();
        int level = 0;
        int start = 0;
        for (int i = 0; i < json.length(); i++) {
            if (json.charAt(i) == '[') level++;
            if (json.charAt(i) == ']') level--;
            if (level == 0 && json.charAt(i) == ',') {
                listY.add(parse2DArray(json.substring(start, i)));
                start = i + 1;
            }
        }
        listY.add(parse2DArray(json.substring(start)));

        return listY.toArray(new int[0][][]);
    }

    private static int[][] parse2DArray(String json) {
        json = json.substring(1, json.length() - 1); // Remove outer brackets
        List<int[]> listX = new ArrayList<>();
        String[] parts = json.split("\\],(?=\\[)");
        for (String part : parts) {
            listX.add(parse1DArray(part.replace("[", "").replace("]", "")));
        }
        return listX.toArray(new int[0][]);
    }

    private static int[] parse1DArray(String json) {
        String[] numbers = json.split(",");
        int[] result = new int[numbers.length];
        for (int i = 0; i < numbers.length; i++) {
            result[i] = Integer.parseInt(numbers[i]);
        }
        return result;
    }

    /**
     * Parses a URL query string into a Map of key-value pairs.
     */
    public static Map<String, String> queryToMap(String query) {
        Map<String, String> map = new HashMap<>();
        if (query == null || query.isEmpty()) {
            return map;
        }
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            try {
                String key = idx > 0 ? URLDecoder.decode(pair.substring(0, idx), "UTF-8") : pair;
                String value = idx > 0 && pair.length() > idx + 1 ? URLDecoder.decode(pair.substring(idx + 1), "UTF-8") : null;
                map.put(key, value);
            } catch (UnsupportedEncodingException e) {
                // Should not happen with UTF-8
                throw new RuntimeException(e);
            }
        }
        return map;
    }
}