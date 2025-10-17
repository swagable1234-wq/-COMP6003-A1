package com.GA;

import com.GA.crossover.CrossoverFunction;
import com.GA.fitness.FitnessFunction;
import com.GA.fitness.adjustment.FitnessAdjustment;
import com.GA.generation.GenerationFunction;
import com.GA.mutation.MutationFunction;
import com.GA.selection.SelectionFunction;
import com.utils.BitMapImage;
import com.utils.Util;

import java.util.ArrayList;
import java.util.function.Function;

public class GeneticAlgorithm {

    // TODO: fix encapsulation in this class
    public GenerationFunction generationFunction;
    public FitnessFunction fitnessFunction;
    public SelectionFunction selectionFunction;
    public CrossoverFunction crossOverFunction;
    public MutationFunction mutationFunction;
    public FitnessAdjustment fitnessAdjustment;

    public int width = 100;
    public int height = 100;

    public int populationSize = 1000;
    public int elite = 30;
    int additionalEDA = 50;
    public int regeneration = 100;
    public IndividualImage[] population;
    public boolean finished = false;

    public boolean eda;
    public double[][][] edaMeans;

    public ArrayList<IndividualImage> best = new ArrayList<IndividualImage>();

    public GeneticAlgorithm(
            int height,
            int width,
            int populationSize,
            int elite,
            int additionalEDA,
            int regeneration,
            GenerationFunction generationFunction,
            FitnessFunction fitnessFunction,
            SelectionFunction selectionFunction,
            CrossoverFunction crossOverFunction,
            MutationFunction mutationFunction,
            FitnessAdjustment fitnessAdjustment) {

        // Initialise parameters
        this.width = width;
        this.height = height;
        this.populationSize = populationSize;
        this.elite = elite;
        this.additionalEDA = additionalEDA;
        this.regeneration = regeneration;
        this.generationFunction = generationFunction;
        this.fitnessFunction = fitnessFunction;
        this.selectionFunction = selectionFunction;
        this.crossOverFunction = crossOverFunction;
        this.mutationFunction = mutationFunction;
        this.fitnessAdjustment = fitnessAdjustment;

        // Initialise population
        population = new IndividualImage[populationSize];
        for(int i = 0; i < populationSize; i++) {
            population[i] = generationFunction.generate(height, width);
            population[i].assignFitness(fitnessFunction.fitness(population[i]));
        }
    }

    public void edaStep() {

        // Calculate means from the top quarter of the previous population
        edaMeans = new double[height][width][3];
        for(int y = 0; y < height; y++) {
            for(int x = 0; x < width; x++) {
                for(int c = 0; c < 3; c++) {
                    for (int i = 0; i < populationSize / 4; i++) {
                        edaMeans[y][x][c] += 0.0 + population[i].getImage().getRgb()[y][x][c];
                    }
                    edaMeans[y][x][c] /= 0.0 + (populationSize / 4); // TODO: parametrise the 2 here
                }
            }
        }

        // Create a new population from the distribution
        IndividualImage[] newPopulation = new IndividualImage[populationSize];

        // Elite stays the same
        for(int i = 0; i < elite; i++) {
            newPopulation[i] = population[i];
        }

        // Main population gets sampled from the means
        for(int i = elite; i < populationSize - regeneration; i++) {
            newPopulation[i] = sampleEDA();
            newPopulation[i] = mutationFunction.mutate(newPopulation[i]);
            newPopulation[i] = mutationFunction.mutate(newPopulation[i]);
            newPopulation[i] = mutationFunction.mutate(newPopulation[i]);
        }

        // Some individuals get re-generated
        for(int i = regeneration; i < populationSize; i++) {
            newPopulation[i] = generationFunction.generate(height, width);
        }

        // Recalculate fitnesses of all individuals
        for(int i = 0; i < populationSize; i++) {
            newPopulation[i].assignFitness(fitnessFunction.fitness(newPopulation[i]));
        }

        fitnessAdjustment.adjust(newPopulation);

        population = newPopulation;

        // Sort population by fitness
        for(int i = 0; i < populationSize - 1; i++) { // TODO: rewrite sorting
            for(int j = i; j < populationSize; j++) {
                if(population[i].getFitness() < population[j].getFitness()) {
                    IndividualImage temp = population[i];
                    population[i] = population[j];
                    population[j] = temp;
                }
            }
        }

        // Store the best in population
        best.add(population[0]);

//        double totalFitness = 0.0;
//        for(int i = 0; i < populationSize; i++) {
//            totalFitness += population[i].getFitness();
//        }
//
//        double meanFitness =  totalFitness / populationSize;
//        System.out.println("Mean Fitness = " + meanFitness);

    }

    // TODO: Old implementation, delete
    private IndividualImage sampleEDA() {
        int[][][] rgb = new int[height][width][3];

        for(int y = 0; y < height; y++) {
            for(int x = 0; x < width; x++) {
                for(int c = 0; c < 3; c++) {
                    rgb[y][x][c] = sampleAroundMean(edaMeans[y][x][c]);
                }
            }
        }

        IndividualImage image = new IndividualImage(new BitMapImage(rgb));

        return image;
    }

    // TODO: AI-generated method, untested
    private static int sampleAroundMean(double mean) {
        double stdDev = 3.0; // tune this! larger = more spread, smaller = more concentrated
        double value;
        do {
            // Box-Muller transform
            double u1 = Util.rng.nextDouble();
            double u2 = Util.rng.nextDouble();
            double z = Math.sqrt(-2.0 * Math.log(u1)) * Math.cos(2.0 * Math.PI * u2);
            value = mean + z * stdDev;
        } while (value < 0.0 || value > 255.0);
        return (int) Math.round(value);
    }

    public void gaStep() {
        // Sort population by fitness
        for(int i = 0; i < populationSize - 1; i++) { // TODO: rewrite sorting
            for(int j = i; j < populationSize; j++) {
                if(population[i].getFitness() < population[j].getFitness()) {
                    IndividualImage temp = population[i];
                    population[i] = population[j];
                    population[j] = temp;
                }
            }
        }

        // Save the best individual in this generation
        best.add(population[0]);

        // Initialise population for the next generation
        IndividualImage[] nextPopulation = new IndividualImage[populationSize];

        // Copy over the elite
        for(int i = 0; i < elite; i++) {
            nextPopulation[i] = population[i];
        }

        // Use selection, crossover and mutation to create the next generation
        for(int i = elite; i < populationSize - (regeneration + additionalEDA); i++) {
            IndividualImage[] parents = selectionFunction.select(population);
            IndividualImage parent1 = parents[0];
            IndividualImage parent2 = parents[1];
            nextPopulation[i] = crossOverFunction.crossover(parent1, parent2);
            nextPopulation[i] = mutationFunction.mutate(nextPopulation[i]);
        }

        // Use EDA approach to generate some individuals using averages of top 50% of the population
        double[][][] edaArray = generateEdaArray((int) Math.round(populationSize * 0.5)); // TODO: 0.5 as a ratio of top individuals used should be parametrised here
        for(int i = populationSize - additionalEDA - regeneration; i < populationSize - regeneration; i++) {
            nextPopulation[i] = sampleEDA(edaArray);
        }

        // Re-generate some individuals
        for(int i = populationSize - regeneration; i < populationSize; i++) {
            nextPopulation[i] = generationFunction.generate(height, width);
        }

        // Recalculate fitnesses of all individuals
        for(int i = 0; i < populationSize; i++) {
            nextPopulation[i].assignFitness(fitnessFunction.fitness(nextPopulation[i]));
        }

        // Update the population
        population = nextPopulation;

//        double totalFitness = 0.0;
//        for(int i = 0; i < populationSize; i++) {
//            totalFitness += population[i].getFitness();
//        }
//
//        double meanFitness =  totalFitness / populationSize;
//        System.out.println("Mean Fitness = " + meanFitness);

    }

    public double[][][] generateEdaArray(int numberOfIndividuals) {
        double[][][] averageRGB = new double[height][width][3];
        for(int i = 0; i < numberOfIndividuals; i++) {
            int[][][] rgb = population[i].getImage().getRgb();
            for(int y = 0; y < height; y++) {
                for(int x = 0; x < width; x++) {
                    for(int c = 0; c < 3; c++) {
                        averageRGB[y][x][c] += rgb[y][x][c];
                    }
                }
            }
        }

        for(int y = 0; y < height; y++) {
            for(int x = 0; x < width; x++) {
                for(int c = 0; c < 3; c++) {
                    averageRGB[y][x][c] /= 0.0 + numberOfIndividuals;
                }
            }
        }

        return averageRGB;
    }

    private IndividualImage sampleEDA(double[][][] edaArray) {
        int[][][] rgb = new int[height][width][3];

        for(int y = 0; y < height; y++) {
            for(int x = 0; x < width; x++) {
                for(int c = 0; c < 3; c++) {
                    rgb[y][x][c] = sampleAroundMean(edaArray[y][x][c]);
                }
            }
        }

        IndividualImage image = new IndividualImage(new BitMapImage(rgb));

        return image;
    }

    // For use in manual adjustments between GA steps
    public void applyToAll(Function<BitMapImage, BitMapImage> function) {
        for(int i = 0; i < populationSize; i++) {
            population[i] = new IndividualImage((BitMapImage) function.apply(population[i].getImage()));
            population[i].assignFitness(fitnessFunction.fitness(population[i]));
        }
    }

}
