package com.GA.fitness;

import com.GA.IndividualImage;
import com.utils.BitMapImage;
import com.utils.ImageUtils;

public class LightnessLikenessFitness extends FitnessFunction {

    // Maximum possible value distances that each pixel can have from the target colour
    private double[][] lDiffs;

    private double[][] imageLightnesses;

    public LightnessLikenessFitness(BitMapImage targetImage, int height, int width) {
        BitMapImage image = ImageUtils.resize(targetImage, height, width);

        int[][][] rgb = image.getRgb();
        lDiffs = new double[height][width];
        imageLightnesses = new double[height][width];

        for(int y = 0; y < height; y++) {
            for(int x = 0; x < width; x++) {
                double pixelLightness = ImageUtils.lightness(rgb[y][x][0], rgb[y][x][1], rgb[y][x][2]);
                imageLightnesses[y][x] = pixelLightness;
                lDiffs[y][x] = Math.max(pixelLightness, 255.0 - pixelLightness);
            }
        }
    }

    @Override
    protected double fitnessCalculation(IndividualImage image) {
        double fitness = 0.0;
        int[][][] rgbImage = image.getImage().getRgb();
        for(int y = 0; y < rgbImage.length; y++) {
            for(int x = 0; x < rgbImage[0].length; x++) {
                double pixelValue = ImageUtils.lightness(rgbImage[y][x][0], rgbImage[y][x][1], rgbImage[y][x][2]);
                fitness += lDiffs[y][x] - Math.abs(imageLightnesses[y][x] - pixelValue);
            }
        }
        return fitness;
    }

    @Override
    protected void calculateMaxFitness(int height, int width) {
        double maxFitness = 0;
        for(int y = 0; y < height; y++) {
            for(int x = 0; x < width; x++) {
                maxFitness += lDiffs[y][x];
            }
        }
        this.theoreticalMaximumFitness = maxFitness;
    }
}
