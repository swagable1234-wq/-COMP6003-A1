package com.GA.mutation;

import com.GA.IndividualImage;
import com.GA.fitness.FitnessFunction;
import com.utils.Util;

/**
 * Mutation functions are used to make changes in the generated
 * individuals, regardless of their fitness. Conventional
 * mutation functions will have some probabilistic elements,
 * such as the probability that the mutation will be applied,
 * or probability that certain elements will be affected.
 *
 * Greedy option is available. It makes the mutation run not once
 * but several times, returning only the fittest result. This is
 * generally against the spirit of GA although it may be useful
 * in tasks where defining suitable mutation functions is difficult.
 *
 * Do no harm option is available. This one will prevent the mutation
 * from happening if it will result in a lower fitness value. This is
 * generally against the spirit of GA, for the same reason.
 */
public abstract class MutationFunction {

    // The probability of the mutation function being applied
    protected double mutationProbability; // 0.0 for 0%, 1.0 for 100%

    // Flags representing optional features
    protected boolean greedy;
    protected boolean doNoHarm;

    // Additional attributes for greedy and no harm features
    protected int attempts;
    protected FitnessFunction fitnessFunction;

    /**
     * Generic constructor
     * @param mutationProbability The probability of the mutation function being applied
     */
    protected MutationFunction(double mutationProbability) {
        this.mutationProbability = mutationProbability;
    }

    /**
     * Enables greedy mutations in all mutation functions.
     * Greedy mutations run a small local search where mutation is
     * applied to the original image multiple times and the best result
     * is selected based on the fitness function. This approach is only
     * useful for stochastic mutation functions and has no effect on
     * deterministic functions
     *
     * @param attempts The number of times the mutation will be applied
     * @param fitnessFunction The fitness function that assesses the outcomes
     */
    public void makeGreedy(int attempts, FitnessFunction fitnessFunction) {
        this.greedy = true;
        this.attempts = attempts;
        this.fitnessFunction = fitnessFunction;
    }

    /**
     * Enables no harm mutations which prevent mutated images
     * from having lower fitness than the original image. If
     * mutation fails to produce an improvement, the function
     * will return the original image instead.
     *
     * @param fitnessFunction The fitness function that assesses the outcomes
     */
    public void makeNoHarm(FitnessFunction fitnessFunction) {
        this.doNoHarm = true;
        this.fitnessFunction = fitnessFunction;
    }

    /**
     * Perform the mutation process, including any optional features
     *
     * @param image Image to be mutated
     * @return The mutated image
     */
    public final IndividualImage mutate(IndividualImage image) {

        // Apply random mutation probability. If it fail, just return a copy of the original.
        if(Util.rng.nextDouble() < mutationProbability)
            return image.copy();

        // Calculate the fitness of the original image
        double originalFitness = fitnessFunction.fitness(image); // TODO: no need to recalculate fitness here if the image is already evaluated

        // Create a mutated version
        IndividualImage bestCandidate = mutationAttempt(image);
        double bestFitness = fitnessFunction.fitness(bestCandidate);

        // If greedy option is enabled, continue making mutations and store the fittest one
        if(greedy) {
            for(int i = 1; i < attempts; i++) {
                IndividualImage candidate = mutationAttempt(image);
                double fitness = fitnessFunction.fitness(candidate);
                if(fitness > bestFitness) {
                    bestFitness = fitness;
                    bestCandidate = candidate;
                }
            }
        }

        // If do no harm option is enabled and the mutated image is worse than the original, just return the original
        if(doNoHarm)
            if(originalFitness > bestFitness)
                return image.copy();

        // Return the mutated image
        return bestCandidate;
    }

    /**
     * The method that performs mutation on an image, does not need to check for mutation probability
     *
     * @param image The image to be mutated
     * @return A mutated image
     */
    protected abstract IndividualImage mutationAttempt(IndividualImage image);

}
