package com.GA.fitness;

import com.GA.IndividualImage;
import com.utils.BitMapImage;
import com.utils.ImageUtils;

public class SaturationLikenessFitness extends FitnessFunction {

    // Maximum possible value distances that each pixel can have from the target colour
    private double[][] sDiffs;

    private double[][] imageSaturations;

    public SaturationLikenessFitness(BitMapImage targetImage, int height, int width) {
        BitMapImage image = ImageUtils.resize(targetImage, height, width);

        int[][][] rgb = image.getRgb();
        sDiffs = new double[height][width];
        imageSaturations = new double[height][width];

        for(int y = 0; y < height; y++) {
            for(int x = 0; x < width; x++) {
                double pixelSaturation = ImageUtils.saturation(rgb[y][x][0], rgb[y][x][1], rgb[y][x][2]);
                imageSaturations[y][x] = pixelSaturation;
                sDiffs[y][x] = Math.max(pixelSaturation, 1.0 - pixelSaturation);
            }
        }
    }

    @Override
    protected double fitnessCalculation(IndividualImage image) {
        double fitness = 0.0;
        int[][][] rgbImage = image.getImage().getRgb();
        for(int y = 0; y < rgbImage.length; y++) {
            for(int x = 0; x < rgbImage[0].length; x++) {
                double pixelSaturation = ImageUtils.saturation(rgbImage[y][x][0], rgbImage[y][x][1], rgbImage[y][x][2]);
                fitness += sDiffs[y][x] - Math.abs(imageSaturations[y][x] - pixelSaturation);
            }
        }
        return fitness;
    }

    @Override
    protected void calculateMaxFitness(int height, int width) {
        double maxFitness = 0;
        for(int y = 0; y < height; y++) {
            for(int x = 0; x < width; x++) {
                maxFitness += sDiffs[y][x];
            }
        }
        this.theoreticalMaximumFitness = maxFitness;
    }
}
