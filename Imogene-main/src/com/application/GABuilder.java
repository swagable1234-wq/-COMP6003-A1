package com.application;

import com.GA.GeneticAlgorithm;
import com.GA.crossover.CrossoverFunction;
import com.GA.crossover.PixelwiseRGBCrossover;
import com.GA.fitness.CheckerboardFitness;
import com.GA.fitness.FitnessFunction;
import com.GA.fitness.adjustment.FitnessAdjustment;
import com.GA.fitness.adjustment.NoAdjustment;
import com.GA.generation.GenerationFunction;
import com.GA.generation.RandomBitmapGeneration;
import com.GA.mutation.MutationFunction;
import com.GA.mutation.RandomPixelsMutation;
import com.GA.selection.RouletteWheelSelection;
import com.GA.selection.SelectionFunction;
import com.application.panels.ImageScreen;

/**
 * A relatively simple factory class for genetic algorithm objects
 */
public class GABuilder {

    private int imageHeight;
    private int imageWidth;
    private int populationSize;
    private int elite;
    private int regeneration;
    private GenerationFunction generationFunction;
    private FitnessFunction fitnessFunction;
    private SelectionFunction selectionFunction;
    private CrossoverFunction crossoverFunction;
    private MutationFunction mutationFunction;
    private FitnessAdjustment fitnessAdjustment;

    public void setImageHeight(int imageHeight) {
        this.imageHeight = imageHeight;
    }

    public  void setImageWidth(int imageWidth) {
        this.imageWidth = imageWidth;
    }

    public void setPopulationSize(int populationSize) {
        this.populationSize = populationSize;
    }

    public void setElite(int elite) {
        this.elite = elite;
    }

    public void setRegeneration(int regeneration) {
        this.regeneration = regeneration;
    }

    public void setGenerationFunction(GenerationFunction generationFunction) {
        this.generationFunction = generationFunction;
    }

    public void setFitnessFunction(FitnessFunction fitnessFunction) {
        this.fitnessFunction = fitnessFunction;
    }

    public void setSelectionFunction(SelectionFunction selectionFunction) {
        this.selectionFunction = selectionFunction;
    }

    public void setCrossoverFunction(CrossoverFunction crossoverFunction) {
        this.crossoverFunction = crossoverFunction;
    }

    public void setMutationFunction(MutationFunction mutationFunction) {
        this.mutationFunction = mutationFunction;
    }

    public void setFitnessAdjustment(FitnessAdjustment fitnessAdjustment) {
        this.fitnessAdjustment = fitnessAdjustment;
    }

    /**
     * Build a GeneticAlgorithm object using specified parameter values
     * Unset and invalid parameters get replaced with default values
     *
     * @return GeneticAlgorithm object ready for use
     */
    public GeneticAlgorithm build() {

        // Correct invalid GA parameters if needed
        if(imageWidth <= 0) {
            System.out.println("Image width must be greater than 0, defaulting to " + ImageScreen.currentImageWidth);
            imageWidth = ImageScreen.currentImageWidth;
        }

        if(imageHeight <= 0) {
            System.out.println("Image height must be greater than 0, defaulting to " + ImageScreen.currentImageHeight);
            imageHeight = ImageScreen.currentImageHeight;
        }

        if(populationSize <= 0) {
            System.out.println("Can not build a genetic algorithm with population size <= 0, defaulting to 1000");
            populationSize = 1000;
        }

        if(elite < 0) {
            System.out.println("Can not build a genetic algorithm with elite < 0, defaulting to 0");
            elite = 0;
        }

        if(regeneration < 0) {
            System.out.println("Can not build a genetic algorithm with regeneration < 0, defaulting to 0");
            regeneration = 0;
        }

        if(generationFunction == null) {
            System.out.println("Can not build a genetic algorithm with generationFunction == null, defaulting to RandomBitmapGeneration");
            generationFunction = new RandomBitmapGeneration();
        }

        if(fitnessFunction == null) {
            System.out.println("Can not build a genetic algorithm with fitnessFunction == null, defaulting to CheckerboardFitness");
            fitnessFunction = new CheckerboardFitness();
        }

        if(selectionFunction == null) {
            System.out.println("Can not build a genetic algorithm with selectionFunction == null, defaulting to RouletteWheelSelection");
            selectionFunction = new RouletteWheelSelection();
        }

        if(crossoverFunction == null) {
            System.out.println("Can not build a genetic algorithm with crossoverFunction == null, defaulting to PixelwiseRGBCrossover");
            crossoverFunction = new PixelwiseRGBCrossover();
        }

        if(mutationFunction == null) {
            System.out.println("Can not build a genetic algorithm with mutationFunction == null, defaulting to RandomPixelsMutation");
            mutationFunction = new RandomPixelsMutation(1.0, 0.1);
        }

        if(fitnessAdjustment == null) {
            System.out.println("Fitness adjustment was left unset, defaulting to NoAdjustment");
            fitnessAdjustment = new NoAdjustment();
        }

        return new GeneticAlgorithm(
                imageHeight,
                imageWidth,
                populationSize,
                elite,
                0, // TODO: remove EDA feature completely
                regeneration,
                generationFunction,
                fitnessFunction,
                selectionFunction,
                crossoverFunction,
                mutationFunction,
                fitnessAdjustment
        );
    }

}
