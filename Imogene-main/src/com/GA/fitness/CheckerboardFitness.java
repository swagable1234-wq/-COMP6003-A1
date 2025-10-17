package com.GA.fitness;

import com.GA.IndividualImage;

/**
 * A fitness function that rewards images that have high colour differences between adjacent pixels
 */
public class CheckerboardFitness extends FitnessFunction {

    @Override
    protected double fitnessCalculation(IndividualImage image) {
        int[][][] rgb = image.getImage().getRgb();
        double fitness = 0.0;

        for(int y = 0; y < rgb.length - 1; y++) {
            for(int x = 0; x < rgb[0].length; x++) {
                for(int c = 0; c < 3; c++) {
                    fitness += Math.abs(rgb[y][x][c] - rgb[y + 1][x][c]); // Difference in pixel colours between all vertical neighbours
                }
            }
        }

        for(int y = 0; y < rgb.length; y++) {
            for(int x = 0; x < rgb[0].length - 1; x++) {
                for(int c = 0; c < 3; c++) {
                    fitness += Math.abs(rgb[y][x][c] - rgb[y][x + 1][c]); // Difference in pixel colours between all horizontal neighbours
                }
            }
        }

        return fitness;
    }

    /**
     * Theoretical maximum fitness is achieved when the whole image is a black and white checkerboard
     * A difference of 255 must be achieved between all 3 colours of each pair of adjacent pixels
     *
     * @param height Height of the image
     * @param width Width of the image
     */
    protected void calculateMaxFitness(int height, int width) {
        this.theoreticalMaximumFitness = 3.0 * 255.0 * (height * (width - 1) + (height - 1) * width);
    }

}
