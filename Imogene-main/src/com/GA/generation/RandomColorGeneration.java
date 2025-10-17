package com.GA.generation;

import com.GA.IndividualImage;
import com.utils.BitMapImage;
import com.utils.Util;

/**
 * A simple generation function that generates an image where
 * each pixel is the same colour. The colour is random.
 */
public class RandomColorGeneration extends GenerationFunction {

    public IndividualImage generate(int height, int width) {
        int[][][] rgb = new int[height][width][3];
        int r = Util.rng.nextInt(256);
        int g = Util.rng.nextInt(256);
        int b = Util.rng.nextInt(256);
        for(int y =  0; y < height; y++)
            for(int x =  0; x < width; x++) {
                rgb[y][x][0] = r;
                rgb[y][x][1] = g;
                rgb[y][x][2] = b;
            }
        return new IndividualImage(new BitMapImage(rgb));
    }

}
