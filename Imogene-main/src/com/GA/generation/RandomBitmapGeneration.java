package com.GA.generation;
import com.GA.IndividualImage;
import com.utils.BitMapImage;
import com.utils.Util;

/**
 * A simple generation function that generates an image where
 * each pixel is completely random.
 */
public class RandomBitmapGeneration extends GenerationFunction {

    public IndividualImage generate(int height, int width) {
        int[][][] rgb = new int[height][width][3];
        for(int y =  0; y < height; y++)
            for(int x =  0; x < width; x++) {
                rgb[y][x][0] = Util.rng.nextInt(256);
                rgb[y][x][1] = Util.rng.nextInt(256);
                rgb[y][x][2] = Util.rng.nextInt(256);
            }
        return new IndividualImage(new BitMapImage(rgb));
    }

}
