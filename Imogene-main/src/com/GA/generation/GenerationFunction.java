package com.GA.generation;

import com.GA.IndividualImage;
import com.utils.BitMapImage;

/**
 * Generation functions are used to generate new images. These
 * are used in genetic algorithms to create the individual for
 * the initial population, and to re-generate some new individuals
 * to replace the least fit ones in each generation.
 */
public abstract class GenerationFunction {

    public abstract IndividualImage generate(int height, int width);

}
