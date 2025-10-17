package com.GA.fitness;

import com.GA.IndividualImage;
import com.utils.BitMapImage;
import com.utils.ImageUtils;

public class HueLikenessFitness extends FitnessFunction {

    private double[][] imageHues;

    public HueLikenessFitness(BitMapImage targetImage, int height, int width) {
        BitMapImage image = ImageUtils.resize(targetImage, height, width);

        int[][][] rgb = image.getRgb();
        imageHues = new double[height][width];

        for(int y = 0; y < height; y++) {
            for(int x = 0; x < width; x++) {
                double pixelHue = ImageUtils.hue(rgb[y][x][0], rgb[y][x][1], rgb[y][x][2]);
                imageHues[y][x] = pixelHue;
            }
        }
    }

    @Override
    protected double fitnessCalculation(IndividualImage image) {
        double fitness = 0.0;
        int[][][] rgbImage = image.getImage().getRgb();
        for(int y = 0; y < rgbImage.length; y++) {
            for(int x = 0; x < rgbImage[0].length; x++) {
                double pixelHue = ImageUtils.hue(rgbImage[y][x][0], rgbImage[y][x][1], rgbImage[y][x][2]);
                fitness += 180.0 - Math.min(Math.abs(pixelHue - imageHues[y][x]), 360.0 - Math.abs(imageHues[y][x] - pixelHue));
            }
        }
        return fitness;
    }

    @Override
    protected void calculateMaxFitness(int height, int width) {
        theoreticalMaximumFitness = 180.0 * height * width;
    }
}