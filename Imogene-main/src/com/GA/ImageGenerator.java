package com.GA;

import com.utils.BitMapImage;

import java.util.Random;

public class ImageGenerator {

    private static Random rng = new Random(System.currentTimeMillis());

    public static BitMapImage randomPixels(int height, int width) {
        int[][][] rgb = new int[height][width][3];
        for(int y = 0; y < height; y++) {
            for(int x = 0; x < width; x++) {
                rgb[y][x][0] = rng.nextInt(0, 256);
                rgb[y][x][1] = rng.nextInt(0, 256);
                rgb[y][x][2] = rng.nextInt(0, 256);
            }
        }
        return new BitMapImage(rgb);
    }

    public static BitMapImage randomPixelsWithCenter(int width, int height, int r, int g, int b) {
        int[][][] rgb = new int[width][height][3];
        for(int y = 0; y < height; y++) {
            for(int x = 0; x < width; x++) {
                rgb[y][x][0] = rng.nextInt(0, 256);
                rgb[y][x][1] = rng.nextInt(0, 256);
                rgb[y][x][2] = rng.nextInt(0, 256);
                rgb[y][x][0] = (rgb[y][x][0] + r) / 2;
                rgb[y][x][1] = (rgb[y][x][1] + g) / 2;
                rgb[y][x][2] = (rgb[y][x][2] + b) / 2;
            }
        }
        return new BitMapImage(rgb);
    }

}
