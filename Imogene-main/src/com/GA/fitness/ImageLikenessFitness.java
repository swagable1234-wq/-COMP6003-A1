package com.GA.fitness;

import com.GA.IndividualImage;
import com.utils.BitMapImage;
import com.utils.ImageUtils;

public class ImageLikenessFitness extends FitnessFunction {

    BitMapImage targetImage;

    // Maximum possible RGB distances that each pixel can have from the target colour
    private int[][] rDiffs;
    private int[][] gDiffs;
    private int[][] bDiffs;

    // Flag for euclidean colour distance calculation instead of manhattan
    private boolean euclidean;

    // Max possible euclidean distance that each pixel can have from the target colour
    private double[][] maxEuclideanDiffs;

    public ImageLikenessFitness(BitMapImage targetImage, int height, int width, boolean euclidean) {
        this.targetImage = ImageUtils.resize(targetImage, height, width);
        this.euclidean = euclidean;

        int[][][] rgb = targetImage.getRgb();

        rDiffs = new int[height][width];
        gDiffs = new int[height][width];
        bDiffs = new int[height][width];

        for(int y = 0; y < height; y++) {
            for(int x = 0; x < width; x++) {
                rDiffs[y][x] = Math.max(rgb[y][x][0], 255 - rgb[y][x][0]);
                gDiffs[y][x] = Math.max(rgb[y][x][1], 255 - rgb[y][x][1]);
                bDiffs[y][x] = Math.max(rgb[y][x][2], 255 - rgb[y][x][2]);
            }
        }

        if(euclidean) {
            maxEuclideanDiffs = new double[height][width];
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    maxEuclideanDiffs[y][x] = Math.sqrt(Math.pow(rDiffs[y][x], 2) + Math.pow(gDiffs[y][x], 2) + Math.pow(bDiffs[y][x], 2));
                }
            }
        }

    }

    @Override
    protected double fitnessCalculation(IndividualImage image) {
        double fitness = 0.0;
        int[][][] rgbTarget = targetImage.getRgb();
        int[][][] rgbImage = image.getImage().getRgb();
        for(int y = 0; y < rgbTarget.length; y++) {
            for(int x = 0; x < rgbTarget[0].length; x++) { // TODO: uses euclidean instead of manhattan, parametrise
                if(euclidean) {
                    fitness += maxEuclideanDiffs[y][x] - Math.sqrt(Math.pow(rgbTarget[y][x][0] - rgbImage[y][x][0], 2) + Math.pow(rgbTarget[y][x][1] - rgbImage[y][x][1], 2) + Math.pow(rgbTarget[y][x][2] - rgbImage[y][x][2], 2));
                }
                else {
                    fitness += rDiffs[y][x] - Math.abs(rgbTarget[y][x][0] - rgbImage[y][x][0]);
                    fitness += gDiffs[y][x] - Math.abs(rgbTarget[y][x][1] - rgbImage[y][x][1]);
                    fitness += bDiffs[y][x] - Math.abs(rgbTarget[y][x][2] - rgbImage[y][x][2]);
                }
            }
        }
        return fitness;
    }

    @Override
    protected void calculateMaxFitness(int height, int width) {
        double maxFitness = 0;
        if(euclidean) {
            for(int y = 0; y < height; y++) {
                for(int x = 0; x < width; x++) {
                    maxFitness +=  maxEuclideanDiffs[y][x];
                }
            }
        }
        else {
            for(int y = 0; y < height; y++) {
                for(int x = 0; x < width; x++) {
                    maxFitness += rDiffs[y][x];
                    maxFitness += gDiffs[y][x];
                    maxFitness += bDiffs[y][x];
                }
            }
        }

        this.theoreticalMaximumFitness = maxFitness;
    }
}
