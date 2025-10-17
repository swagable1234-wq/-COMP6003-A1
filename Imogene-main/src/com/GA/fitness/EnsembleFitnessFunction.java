package com.GA.fitness;

import com.GA.IndividualImage;

import java.util.ArrayList;

public class EnsembleFitnessFunction extends FitnessFunction {

    private ArrayList<FitnessFunction> functions = new ArrayList<FitnessFunction>();
    ArrayList<Double> weights = new ArrayList<Double>();

    public void addFunction(FitnessFunction fitnessFunction, double weight) {
        functions.add(fitnessFunction);
        weights.add(weight);
//        if(normalised)
//            fitnessFunction.makeNormalised(); // TODO: figure out this height/width inconsistency and fix this
    }

    @Override
    protected double fitnessCalculation(IndividualImage image) {
        double fitness = 0.0;
        for(int i = 0; i < functions.size(); i++) {
            fitness += functions.get(i).fitness(image) * weights.get(i); // TODO: this does not take into account the individual differences between fitness functions in terms of their magnitude. Additional adjustments must be made when combining fitness functions once it becomes possible to retrieve their max fitness values.
        }
        return fitness;
    }

    /**
     * Normalisiation in ensemble fitness function requires its components to also be normalised
     *
     * @param height Height of the image
     * @param width Width of the image
     */
    @Override
    public void makeNormalised(int height, int width) {
        for(int i = 0; i < functions.size(); i++) {
            functions.get(i).makeNormalised(height, width);
        }
        normalised = true;
        calculateMaxFitness(height, width);
    }

    /**
     * Theoretical maximum fitness of an ensemble fitness function is a weighted
     * sum of the maximum fitnesses of its components
     *
     * @param height Height of the image
     * @param width Width of the image
     */
    protected void calculateMaxFitness(int height, int width) {
        double maxFitness = 0.0;
        if(normalised) {
            for(int i = 0; i < functions.size(); i++) {
                maxFitness += functions.get(i).theoreticalMaximumFitness * weights.get(i);
            }
        }
        else {
            for(int i = 0; i < functions.size(); i++) {
                maxFitness += weights.get(i);
            }
        }

        this.theoreticalMaximumFitness = maxFitness;
    }
}
