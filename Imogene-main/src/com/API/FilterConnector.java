package com.API;

import com.application.panels.ConnectionScreen;
import com.utils.BitMapImage;
import com.utils.Util;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class FilterConnector {


    // origianl filters that were already there

    public static final String FILTER_GRAYSCALE = "grayscale";
    public static final String FILTER_INVERT = "invert";
    //new constraints for missing buttons
    // smoothing filters
    public static final String FILTER_SMOOTH_SOFT = "smoothSoft";
    public static final String FILTER_SMOOTH_MEDIUM = "smoothMedium";
    public static final String FILTER_SMOOTH_HARD = "smoothHard";

    // rgb rebalance
    public static final String FILTER_RGB_REBALANCE = "rgbRebalance";

    // spectrum projections
    public static final String FILTER_SPECTRAL_PROJECTION = "spectralProjection";




    public static BitMapImage requestFilter(String type, BitMapImage image, Object... params) throws IOException, InterruptedException {

        String remote = ConnectionScreen.getInstance().getRemote();
        String imageJson = Util.arrayToJson(image.getRgb());


        // changing the json body to work with more filters
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("{");
        jsonBuilder.append("\"type\":\"").append(type).append("\",");
        jsonBuilder.append("\"image\":\"").append(imageJson).append("\"");


        if (params.length > 0) {
            jsonBuilder.append(",\"params\":[");
            for (int i = 0; i < params.length; i++) {
                Object param = params[i];


                if (param instanceof Number) {
                    jsonBuilder.append(param);
                } else {
                    jsonBuilder.append("\"").append(param).append("\"");
                }
                if (i < params.length - 1) {
                    jsonBuilder.append(",");
                }
            }
            jsonBuilder.append("]");
        }

        jsonBuilder.append("}");
        String jsonBody = jsonBuilder.toString();

        //send requestsdv
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(remote + "/filter"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        String imageString = response.body();

        int[][][] rgb = Util.parse3DArray(imageString);

        return new BitMapImage(rgb);
    }

}
