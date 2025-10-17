package com.GA.crossover;

import com.GA.IndividualImage;
import com.utils.Util;

import java.util.ArrayList;

public class EnsembleCrossoverFunction extends CrossoverFunction {

    ArrayList<CrossoverFunction> functions = new ArrayList<CrossoverFunction>();
    ArrayList<Double> weights = new ArrayList<Double>();
    double sumWeights;

    public EnsembleCrossoverFunction() {
        functions = new ArrayList<CrossoverFunction>();
        weights = new ArrayList<Double>();
    }

    /**
     * Adds a new crossover function to the list of available functions
     *
     * @param function The new crossover function
     * @param weight The likelihood of the function being applied
     */
    public void addFunction(CrossoverFunction function, double weight) {
        functions.add(function);
        weights.add(weight);
        sumWeights += weight; // Sum is pre-calculated here to avoid re-calculating at each crossover
    }

    /**
     * Selects a random crossover function from the list, with probability of
     * each function being selected proportional to its weight and applies it.
     *
     * @param image1 First parent image
     * @param image2 Second parent image
     * @return The child image
     */
    protected IndividualImage crossoverAttempt(IndividualImage image1, IndividualImage image2) {
        double selection = Util.rng.nextDouble();
        int functionIndex = 0;
        selection -= weights.get(functionIndex);
        while(selection > 0) {
            functionIndex++;
            selection -= weights.get(functionIndex);
        }
        CrossoverFunction selectedFunction = functions.get(functionIndex);
        IndividualImage childImage = selectedFunction.crossoverAttempt(image1, image2);
        return childImage;
    }

    /**
     * Selects a random crossover function from the list, with probability of
     * each function being selected proportional to its weight and applies it.
     *
     * Uses weighted crossover based on image fitness.
     *
     * @param image1 First parent image
     * @param image2 Second parent image
     * @return The child image
     */
    protected IndividualImage weightedCrossoverAttempt(IndividualImage image1, IndividualImage image2) {
        double selection = Util.rng.nextDouble();
        int functionIndex = 0;
        selection -= weights.get(functionIndex);
        while(selection > 0) {
            functionIndex++;
            selection -= weights.get(functionIndex);
        }
        CrossoverFunction selectedFunction = functions.get(functionIndex);
        IndividualImage childImage = selectedFunction.weightedCrossoverAttempt(image1, image2); // TODO: performs weighted crossover regardless of whether the function passed in was originally weighted or not, maybe resolve this somehow?
        return childImage;
    }
}
