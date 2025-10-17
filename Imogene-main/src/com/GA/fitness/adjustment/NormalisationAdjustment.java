package com.GA.fitness.adjustment;

import com.GA.IndividualImage;

/**
 * A simple fitness normalisation adjustment that projects the fitness values onto
 * the (0; 1] range. It completely disregards the magnitude of the raw fitness values
 * by subtracting the lowest fitness value in the population from each individual,
 * then divides each fitness value by the range between the max and min fitness.
 *
 * As a result, all fitness values lie on the range between 0.0 and 1.0, with the
 * most fit individual having the value of 1.0.
 *
 */
public class NormalisationAdjustment extends FitnessAdjustment {

    public void adjust(IndividualImage[] population) {

        // Find minimmum and maximum fitness in the population
        double minFitness = population[0].getFitness();
        double maxFitness = population[0].getFitness();
        for(IndividualImage image: population) {
            if(image.getFitness() < minFitness)
                minFitness = image.getFitness();
            if(image.getFitness() > maxFitness)
                maxFitness = image.getFitness();
        }

        // Subtract min-1 from each fitness
        for(IndividualImage image: population) {
            image.assignFitness(image.getFitness() - minFitness + 1);
        }

        // Divide all fitnesses by (max - min + 1) to project them onto (0; 1] range
        double denominator = maxFitness - minFitness + 1; // TODO: 1 here creates an issue if the fitness function is normalised
        for(IndividualImage image: population) {
            image.assignFitness(image.getFitness() / denominator);
        }
    }
}
