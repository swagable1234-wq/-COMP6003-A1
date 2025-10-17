package com.GA.fitness;

import com.GA.IndividualImage;

public class ColourFitness extends FitnessFunction {

    // RGB values of the target colour
    private int r;
    private int g;
    private int b;

    // Maximum possible RGB distances that a pixel can have from the target colour
    private int rDiff;
    private int gDiff;
    private int bDiff;

    // Flag for euclidean colour distance calculation instead of manhattan
    private boolean euclidean;

    // Max possible euclidean distance that a pixel can have from the target colour
    private double maxEuclideanDiff;

    public ColourFitness(int r, int g, int b, boolean euclidean) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.euclidean = euclidean;

        rDiff = Math.max(r, 255 - r);
        gDiff = Math.max(g, 255 - g);
        bDiff = Math.max(b, 255 - b);

        if(euclidean) {
            maxEuclideanDiff = Math.sqrt(Math.pow(rDiff, 2) + Math.pow(gDiff, 2) + Math.pow(bDiff, 2));
        }
    }

    @Override
    protected double fitnessCalculation(IndividualImage image) {
        double fitness = 0;
        int[][][] rgb = image.getImage().getRgb();
        for(int y = 0; y < rgb.length; y++) {
            for(int x = 0; x < rgb[0].length; x++) {
                if(euclidean) {
                    fitness += maxEuclideanDiff - Math.sqrt(Math.pow(Math.abs(rgb[y][x][0] - r), 2) + Math.pow(Math.abs(rgb[y][x][1] - g), 2) + Math.pow(Math.abs(rgb[y][x][2] - b), 2));
                }
                else {
                    fitness += rDiff - Math.abs(rgb[y][x][0] - r);
                    fitness += gDiff - Math.abs(rgb[y][x][1] - g);
                    fitness += bDiff - Math.abs(rgb[y][x][2] - b);
                }

            }
        }
        return fitness;
    }

    /**
     * Maximum distance from a colour would be achieved by an image consisting only of
     * pixels of the target colour.
     *
     * @param height Height of the image
     * @param width Width of the image
     */
    protected void calculateMaxFitness(int height, int width) {
        if(euclidean)
            theoreticalMaximumFitness = height * width * maxEuclideanDiff;
        else
            theoreticalMaximumFitness = height * width * (rDiff + gDiff + bDiff);
    }

}
