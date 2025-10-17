package com.GA.mutation;

import com.GA.IndividualImage;
import com.utils.ImageUtils;
import com.utils.Util;

/**
 * A mutation function that applies a paintcan operation to the image.
 *
 * It selects a random pixel on the image and uses paintcan tool
 * to change the colour of the areas that are the same colour as
 * the pixel that was selected that are connected to that pixel.
 * It replaces all of those pixels with the same random colour.
 */

// TODO: add sensitivity parameter that would allow the paintcan to paint over more pixels that aren't exactly like the one selected
public class PaintCanMutation extends MutationFunction {

    /**
     * Generic constructor
     *
     * @param mutationProbability The probability of the mutation function being applied
     */
    protected PaintCanMutation(double mutationProbability) {
        super(mutationProbability);
    }

    public IndividualImage mutationAttempt(IndividualImage image) {
        int r = Util.rng.nextInt(256);
        int g = Util.rng.nextInt(256);
        int b = Util.rng.nextInt(256);
        int y = Util.rng.nextInt(image.getImage().getHeight());
        int x = Util.rng.nextInt(image.getImage().getWidth());
        IndividualImage out = new IndividualImage(ImageUtils.paintCan(image.getImage(), x, y, r, g, b));
        return out;
    }
}
