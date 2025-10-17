package com.GA.crossover;

import com.GA.IndividualImage;
import com.utils.BitMapImage;
import com.utils.Util;

public class PixelwiseHSLCrossover extends CrossoverFunction {

    protected IndividualImage crossoverAttempt(IndividualImage image1, IndividualImage image2) {
        return weightedCrossoverAttempt(image1, image2, 0.5, 0.5);
    }

    protected IndividualImage weightedCrossoverAttempt(IndividualImage image1, IndividualImage image2) {
        double p1 = image1.getFitness() / (image1.getFitness() + image2.getFitness());
        double p2 = image2.getFitness() / (image1.getFitness() + image2.getFitness());
        return weightedCrossoverAttempt(image1, image2, p1, p2);
    }

    private IndividualImage weightedCrossoverAttempt(IndividualImage image1, IndividualImage image2, double p1, double p2) {
        int[][][] rgb1 = image1.getImage().getRgb();
        int[][][] rgb2 = image2.getImage().getRgb();
        int[][][] rgbChild = new int[rgb1.length][rgb1[0].length][3];
        for(int y = 0; y < rgb1.length; y++) {
            for(int x = 0; x < rgb1[0].length; x++) {
                if(Util.rng.nextDouble(p1 + p2) < p1) {
                    rgbChild[y][x][0] = rgb1[y][x][0];
                    rgbChild[y][x][1] = rgb1[y][x][1];
                    rgbChild[y][x][2] = rgb1[y][x][2];// TODO: can't implement HSL stuff here, need to implement it as a blend thing
                } else {
                    rgbChild[y][x][0] = rgb2[y][x][0];
                    rgbChild[y][x][1] = rgb2[y][x][1];
                    rgbChild[y][x][2] = rgb2[y][x][2];
                }
            }
        }
        IndividualImage child = new IndividualImage(new BitMapImage(rgbChild));
        return child;
    }

}
