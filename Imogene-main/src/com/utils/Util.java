package com.utils;

import java.util.*;

public class Util {

    public static Random rng = new Random();

    public static int[] randomIndexes(int n, int size) {
        int[] indexes = new int[n];
        for(int i = 0; i < n; i++) {
           indexes[i] = rng.nextInt(size);
           boolean alreadyUsed = false;
           for(int j = 0; j < i; j++) {
               if(indexes[i] == indexes[j]) {
                   i--;
                   alreadyUsed = true;
                   break;
               }
           }
           if(alreadyUsed) {
               i--;
               continue;
           }
        }
        return indexes;
    }

    public static Map<String, String> queryToMap(String query) {
        Map<String, String> result = new HashMap<>();
        if (query == null) return result;
        for (String param : query.split("&")) {
            String[] entry = param.split("=");
            if (entry.length > 1) {
                result.put(entry[0], entry[1]);
            }
        }
        return result;
    }

    public static String arrayToJson(int[][][] array) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int y = 0; y < array.length; y++) {
            sb.append("[");
            for (int x = 0; x < array[y].length; x++) {
                sb.append("[")
                        .append(array[y][x][0]).append(",")
                        .append(array[y][x][1]).append(",")
                        .append(array[y][x][2]).append("]");
                if (x < array[y].length - 1) sb.append(",");
            }
            sb.append("]");
            if (y < array.length - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }

    public static int[][][] parse3DArray(String json) {
        List<List<List<Integer>>> list3D = new ArrayList<>();
        List<List<Integer>> currentRow = null;
        List<Integer> currentPixel = null;
        StringBuilder numBuffer = new StringBuilder();

        for (char c : json.toCharArray()) {
            if (Character.isDigit(c) || c == '-') {
                numBuffer.append(c);
            } else if (c == ',') {
                if (numBuffer.length() > 0 && currentPixel != null) {
                    currentPixel.add(Integer.parseInt(numBuffer.toString()));
                    numBuffer.setLength(0);
                }
            } else if (c == '[') {
                // new level
                if (currentRow == null) {
                    currentRow = new ArrayList<>();
                } else if (currentPixel == null) {
                    currentPixel = new ArrayList<>();
                }
            } else if (c == ']') {
                if (numBuffer.length() > 0) {
                    currentPixel.add(Integer.parseInt(numBuffer.toString()));
                    numBuffer.setLength(0);
                }

                if (currentPixel != null) {
                    currentRow.add(currentPixel);
                    currentPixel = null;
                } else if (currentRow != null) {
                    list3D.add(currentRow);
                    currentRow = null;
                }
            }
        }

        // Convert to int[][][]
        int[][][] result = new int[list3D.size()][][];
        for (int y = 0; y < list3D.size(); y++) {
            List<List<Integer>> row = list3D.get(y);
            result[y] = new int[row.size()][3];
            for (int x = 0; x < row.size(); x++) {
                List<Integer> pixel = row.get(x);
                for (int c = 0; c < 3 && c < pixel.size(); c++) {
                    result[y][x][c] = pixel.get(c);
                }
            }
        }

        return result;
    }

}
