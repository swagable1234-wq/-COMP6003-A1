package com.GA.fitness;

import com.GA.IndividualImage;

public abstract class FitnessFunction {

    // A flag for the normalised fitness feature
    protected boolean normalised;

    // The maximum possible raw fitness value that an image can achieve using this fitness function
    protected double theoreticalMaximumFitness;

    // An optional progress meter representing what proportion of GA generations have passed, can be used by functions that change their evaluation during GA loop
    protected double gaProgress;

    public double fitness(IndividualImage image) {
        double fitness = fitnessCalculation(image);

        if(normalised)
            return fitness / theoreticalMaximumFitness;

        return fitness;
    }

    /**
     * Raw fitness value calculation without any normalisation or adjustments
     *
     * @return Raw fitness value
     */
    protected abstract double fitnessCalculation(IndividualImage image);

    /**
     * Method for calculating the maximum possible raw fitness value for an image of a given size
     *
     * @param height Height of the image
     * @param width Width of the image
     */
    protected abstract void calculateMaxFitness(int height, int width);

    /**
     * Optionally override this method to allow fitness functions to use normalised fitness values
     * instead of raw results of fitness function calculations
     *
     * @param height Height of the image
     * @param width Width of the image
     */
    public void makeNormalised(int height, int width) {
        calculateMaxFitness(height, width);
        normalised = true;
    }

    /**
     * Assigns the value representing what proportion of GA generations have passed
     *
     * @param gaProgress Progress of the GA on a scale from 0.0 to 1.0
     */
    public void assignGAProgress(double gaProgress) {
        this.gaProgress = gaProgress;
    }

}
