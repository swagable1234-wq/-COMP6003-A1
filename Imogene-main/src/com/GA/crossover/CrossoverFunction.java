package com.GA.crossover;

import com.GA.IndividualImage;
import com.GA.fitness.FitnessFunction;

/**
 * Crossover functions are used to combine two individuals in
 * a genetic algorithm to produce a new individual, like a
 * child born from two parents. The child will inherit some
 * characteristics from one parent and some from the other.
 *
 * Two features are available for crossover functions:
 * - Weighted crossover takes fitness of the two parents into
 *   account and performs crossover in a manner that favours
 *   the characteristics of the parent with higher fitness.
 * - Greedy crossover will attempt to run crossover operation
 *   multiple times and select the result with the highest
 *   fitness value.
 *
 * Since weighted crossover might be used, both the weighted
 * and unweighted versions of the crossover function need to be
 * defined. For crossovers that can not take weights into account
 * just make a call to the unweighted version inside the weighted
 * method.
 *
 */
public abstract class CrossoverFunction {

    // Flags representing optional features
    protected boolean weighted;
    protected boolean greedy;

    // Additional attributes for the greedy feature
    protected int attempts;
    protected FitnessFunction fitnessFunction;

    /**
     * Enables greedy crossover in all crossover functions.
     * Greedy crossovers run a small local search where crossover is
     * applied to the parent images multiple times and the best result
     * is selected based on the fitness function. This approach is only
     * useful for stochastic crossover functions and has no effect on
     * deterministic functions
     *
     * @param attempts The number of times the crossover will be applied
     * @param fitnessFunction The fitness function that assesses the outcomes
     */
    public void makeGreedy(int attempts, FitnessFunction fitnessFunction) {
        this.greedy = true;
        this.attempts = attempts;
        this.fitnessFunction = fitnessFunction;
    }

    /**
     * Enables the weighted feature. Weighted crossover takes fitness
     * of the parents into account when deciding which parent will each
     * of the characteristics be inherited from.
     */
    public void makeWeighted() {
        weighted = true;
    }

    public IndividualImage crossover(IndividualImage image1, IndividualImage image2) {

        // Perform the crossover, weighted or unweighted
        IndividualImage bestCandidate;
        if(weighted)
            bestCandidate = weightedCrossoverAttempt(image1, image2);
        else
            bestCandidate = crossoverAttempt(image1, image2);

        // If greedy option is not enabled, return the result as is
        if(!greedy)
            return bestCandidate;

        // If greedy option is enabled, run more attempts and select the fittest result
        double bestFitness = fitnessFunction.fitness(bestCandidate);
        for(int i = 1; i < attempts; i++) {
            IndividualImage candidate;
            if(weighted)
                candidate = weightedCrossoverAttempt(image1, image2);
            else
                candidate = crossoverAttempt(image1, image2);
            double fitness = fitnessFunction.fitness(candidate);
            if(fitness > bestFitness) {
                bestFitness = fitness;
                bestCandidate = candidate;
            }
        }

        // Return the fittest crossover result
        return bestCandidate;
    }

    protected abstract IndividualImage crossoverAttempt(IndividualImage image1, IndividualImage image2);

    protected abstract IndividualImage weightedCrossoverAttempt(IndividualImage image1, IndividualImage image2);

}
