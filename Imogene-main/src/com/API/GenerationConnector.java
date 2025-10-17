package com.API;

import com.application.panels.ConnectionScreen;
import com.utils.BitMapImage;
import com.utils.Util;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class GenerationConnector {

    public static final String RANDOM_BITMAP = "randomBitmap";
    public static final String RANDOM_COLOUR = "randomColour";

    public static BitMapImage requestGeneration(String type, int height, int width) throws IOException, InterruptedException {

        String remote = ConnectionScreen.getInstance().getRemote();
        URL url = new URL(remote + "/generate?type=" + type + "&height=" + height +"&width=" + width);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        InputStream input = conn.getInputStream();
        Scanner scanner = new Scanner(input).useDelimiter("\\A");
        String response = scanner.hasNext() ? scanner.next() : "";
        scanner.close();

        int[][][] rgb = Util.parse3DArray(response);

        return new BitMapImage(rgb);
    }

}
