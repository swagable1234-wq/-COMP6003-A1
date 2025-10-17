package com.GA.crossover;

import com.GA.IndividualImage;
import com.utils.BitMapImage;

/**
 * A crossover function that blends the two images together by
 * averaging the values of their corresponding pixel colours.
 *
 * Unweighted version just uses the mean of each colour component
 * and the weighted version skews the mean towards the values of
 * the more fit individuals.
 */
public class BlendCrossover extends CrossoverFunction {
    @Override
    protected IndividualImage crossoverAttempt(IndividualImage image1, IndividualImage image2) {
        return weightedCrossoverAttempt(image1, image2, 0.5, 0.5);
    }

    @Override
    protected IndividualImage weightedCrossoverAttempt(IndividualImage image1, IndividualImage image2) {
        double fitness1 = image1.getFitness();
        double fitness2 = image2.getFitness();
        double weight1 = fitness1 / (fitness1 + fitness2);
        double weight2 = fitness2 / (fitness1 + fitness2);
        return weightedCrossoverAttempt(image1, image2, weight1, weight2);
    }

    protected IndividualImage weightedCrossoverAttempt(IndividualImage image1, IndividualImage image2, double weight1, double weight2) {
        int[][][] rgb1 = image1.getImage().getRgb();
        int[][][] rgb2 = image2.getImage().getRgb();
        int[][][] rgbOut = new int[rgb1.length][rgb1[0].length][3];
        for(int y = 0; y < rgb1.length; y++) {
            for(int x = 0; x < rgb1[0].length; x++) {
                for(int c = 0; c < 3; c++) {
                    rgbOut[y][x][c] = (int) Math.round(rgb1[y][x][c] * weight1 + rgb2[y][x][c] * weight2);
                }
            }
        }
        return new IndividualImage(new BitMapImage(rgbOut));
    }
}
