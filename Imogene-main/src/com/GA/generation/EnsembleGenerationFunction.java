package com.GA.generation;

import com.GA.IndividualImage;
import com.GA.mutation.MutationFunction;
import com.utils.Util;

import java.util.ArrayList;

public class EnsembleGenerationFunction extends GenerationFunction {

    private ArrayList<GenerationFunction> functions;
    private ArrayList<Double> weights;
    private double sumWeights;

    public EnsembleGenerationFunction() {
        functions = new ArrayList<GenerationFunction>();
        weights = new ArrayList<Double>();
    }

    public void addFunction(GenerationFunction function, double weight) {
        functions.add(function);
        weights.add(weight);
        sumWeights += weight;
    }

    @Override
    public IndividualImage generate(int height, int width) {
        double selection = Util.rng.nextDouble();
        int functionIndex = 0;
        selection -= weights.get(functionIndex);
        while(selection > 0) {
            functionIndex++;
            selection -= weights.get(functionIndex);
        }
        GenerationFunction selectedFunction = functions.get(functionIndex);
        IndividualImage image = selectedFunction.generate(height, width);
        return image;
    }
}
