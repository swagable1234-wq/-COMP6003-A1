package com.GA.mutation;

import com.GA.IndividualImage;
import com.GA.fitness.FitnessFunction;
import com.utils.BitMapImage;
import com.utils.ImageUtils;
import com.utils.Util;

/**
 * A simple mutation that just applies the smooth filter to the
 * entire image. Can be useful as part of the ensemble mutation
 * function as it reduces the noise on large textures by sacrificing
 * some edge clarity.
 */

public class SmoothMutation extends MutationFunction{

    // TODO: add parameters for the weights in the smooth mask
    public SmoothMutation(double mutationProbability) {
        super(mutationProbability);
    }

    protected IndividualImage mutationAttempt(IndividualImage image) {
        IndividualImage smoothed = new IndividualImage(ImageUtils.smoothFilter(image.getImage()));
        return smoothed;
    }
}
