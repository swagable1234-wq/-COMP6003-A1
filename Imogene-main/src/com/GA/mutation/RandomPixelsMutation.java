package com.GA.mutation;

import com.GA.IndividualImage;
import com.application.Application;
import com.utils.BitMapImage;
import com.utils.Util;

/**
 * A simple mutation function that replaces some of the pixels
 * with completely random colours.
 *
 * An additional parameter is added: the probability for each
 * pixel to be altered.
 *
 * For example, if the probability is 0.1 then around 10% of the
 * pixels of the image will have their rgb values randomised.
 *
 */
public class RandomPixelsMutation extends MutationFunction {

    // Probability for each individual pixel to be altered
    private double mutationPixelProbability;

    /**
     * Constructor
     *
     * @param mutationProbability The probability of the mutation function being applied
     * @param mutationPixelProbability Probability for each individual pixel to be altered
     */
    public RandomPixelsMutation(double mutationProbability, double mutationPixelProbability) {
        super(mutationProbability);
        this.mutationPixelProbability = mutationPixelProbability;
    }

    protected IndividualImage mutationAttempt(IndividualImage individualImage) {
        int[][][] rgb = individualImage.getImage().getRgb();
        int[][][] rgbMutated = individualImage.copy().getImage().getRgb(); // TODO: strange implementation

        if(Util.rng.nextDouble(1.0) > mutationProbability)
            return new IndividualImage(new BitMapImage(rgbMutated));

        for(int y = 0; y < rgb.length; y++) {
            for(int x = 0; x < rgb[0].length; x++) {
                if(Application.rng.nextDouble(100.0) < mutationPixelProbability) {
                    rgbMutated[y][x][0] = Application.rng.nextInt(256);
                    rgbMutated[y][x][1] = Application.rng.nextInt(256);
                    rgbMutated[y][x][2] = Application.rng.nextInt(256);
                }
            }
        }

        return new IndividualImage(new BitMapImage(rgbMutated));
    }
}
