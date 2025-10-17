package com.GA.mutation;

import com.GA.IndividualImage;
import com.application.Application;
import com.utils.BitMapImage;
import com.utils.Util;

/**
 * A mutation function that slightly adjusts the colours of random pixels.
 *
 * Two additional parameters are required:
 * - The probability for each pixel to be altered
 * - The maximum ammount by which the colours will be altered
 *
 * For example, if the probability is 0.1 and max adjustment is 5, then
 * around 10% of the pixels of the image will have their rgb values changed
 * by up to 5 points, out of 255, in either positive or negative direction.
 *
 * This mutation function can be used to provide smooth changes to images
 * without completely overriding values of pixels with new colours.
 *
 */
public class RandomPixelAdjustmentsMutation extends MutationFunction {

    // Probability for each individual pixel to be altered
    private double mutationPixelProbability;

    // Maximum allowed change in each of the rgb values
    private int maxAdjustmentPerColour;

    /**
     * Constructor
     *
     * @param mutationProbability The probability of the mutation function being applied
     * @param mutationPixelProbability Probability for each individual pixel to be altered
     * @param maxAdjustmentPerColour Maximum allowed change in each of the rgb values
     */
    public RandomPixelAdjustmentsMutation(double mutationProbability, double mutationPixelProbability, int maxAdjustmentPerColour) {
        super(mutationProbability);
        this.mutationPixelProbability = mutationPixelProbability;
        this.maxAdjustmentPerColour = maxAdjustmentPerColour;
    }

    @Override
    protected IndividualImage mutationAttempt(IndividualImage individualImage) {
        int[][][] rgb = individualImage.getImage().getRgb();
        int[][][] rgbMutated = individualImage.copy().getImage().getRgb(); // TODO: strange implementation

        for(int y = 0; y < rgb.length; y++) {
            for(int x = 0; x < rgb[0].length; x++) {
                if(Application.rng.nextDouble(100.0) < mutationPixelProbability) {
                    for(int c = 0; c < 3; c++) {
                        rgbMutated[y][x][c] += (int) Math.round(maxAdjustmentPerColour - maxAdjustmentPerColour * Util.rng.nextDouble(2.0));
                        if(rgbMutated[y][x][c] < 0)
                            rgbMutated[y][x][c] = 0;
                        if(rgbMutated[y][x][c] > 255)
                            rgbMutated[y][x][c] = 255;
                    }
                }
            }
        }
        return new  IndividualImage(new BitMapImage(rgbMutated));
    }

}
