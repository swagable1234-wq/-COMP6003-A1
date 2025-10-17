package com.GA.fitness.adjustment;

import com.GA.IndividualImage;

/**
 * An abstract class for population-level fitness adjustments.
 *
 * Fitness adjustments may be used to create or compensate for certain fitness imbalances.
 * Fitness imbalances can occur when the whole population is very close to the hypothetical
 * maximum fitness, when the population is generated using a mix of multiple different
 * approaches, or when the components of the evolutionary algorithm are not well tuned for
 * the task.
 *
 */
public abstract class FitnessAdjustment {

    public abstract void adjust(IndividualImage[] population);

}
