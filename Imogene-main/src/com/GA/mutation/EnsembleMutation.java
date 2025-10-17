package com.GA.mutation;

import com.GA.IndividualImage;
import com.utils.Util;

import java.util.ArrayList;

/**
 * An ensemble mutation function. It can store multiple mutation
 * functions and will use a random one whenever it is applied.
 *
 * New functions can be added using the addFunction method. In
 * addition to the mutation functions, their weights can be supplied.
 * The weights determine the likelihood of each function being applied.
 * For example, if there are 3 functions with weights 0.2, 0.3 and 0.5
 * than there is a 20% chance that the first one will be applied, 30%
 * the second and 50% the third. Function applications are mutually exclusive.
 *
 * When creating the functions to be used in ensemble, it is best to give
 * them all 100% chance of being applied to make sure the ensemble always
 * tries to apply one of them whenever its own probability is triggered.
 *
 */
public class EnsembleMutation extends MutationFunction {

    // Mutation functions to be used
    private ArrayList<MutationFunction> functions;

    // Weights of mutation functions, higher weight means the function is more likely to be applied
    private ArrayList<Double> weights;

    // Sum of the function weights
    private double sumWeights;

    public EnsembleMutation(double mutationProbability) {
        super(mutationProbability);
        functions = new ArrayList<MutationFunction>();
        weights = new ArrayList<Double>();
    }

    /**
     * Ads a new mutation function to the list of available functions
     *
     * @param function The new mutation function
     * @param weight The likelihood of the function being applied
     */
    public void addFunction(MutationFunction function, double weight) {
        functions.add(function);
        weights.add(weight);
        sumWeights += weight; // Sum is pre-calculated here to avoid re-calculating at each mutation
    }

    /**
     * Selects a random mutation function from the list, with probability of
     * each function being selected proportional to its weight and applies it.
     *
     * @param individualImage The image to be mutated
     * @return A mutated image
     */
    @Override
    protected IndividualImage mutationAttempt(IndividualImage individualImage) {
        double selection = Util.rng.nextDouble();
        int functionIndex = 0;
        selection -= weights.get(functionIndex);
        while(selection > 0) {
            functionIndex++;
            selection -= weights.get(functionIndex);
        }
        MutationFunction selectedFunction = functions.get(functionIndex);
        IndividualImage mutatedImage = selectedFunction.mutationAttempt(individualImage);
        return mutatedImage;
    }
}
