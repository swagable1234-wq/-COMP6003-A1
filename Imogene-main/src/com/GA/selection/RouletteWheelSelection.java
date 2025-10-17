package com.GA.selection;

import com.GA.IndividualImage;
import com.GA.fitness.FitnessFunction;
import com.utils.Util;

/**
 * Roulette wheel selection is the most conventional selection
 * algorithm for GA. Each individual has a chance to be selected
 * proportional to their fitness.
 *
 * When used with ranked option, the rank of the individual is
 * used instead of its fitness. For example, in a population of
 * 100, the fittest individual will have a rank of 100, second
 * fittest will have 99, third will have 98 and so on.
 */

public class RouletteWheelSelection extends SelectionFunction {

    public IndividualImage[] select(IndividualImage[] population) {
        double totalFitness = 0.0;
        for(int i = 0; i < population.length; i++) {
            if(ranked)
                totalFitness += population.length - i;
            else
                totalFitness += population[i].getFitness();
        }

        double selection = Util.rng.nextDouble(totalFitness);
        int selectedIndex1 = 0;
        for(int i = 0; i < population.length; i++) {
            if(ranked)
                selection -= population.length - i;
            else
                selection -= population[i].getFitness();
            if(selection < 0)
                break;
            selectedIndex1++;
        }

        if(ranked)
            totalFitness -= population.length - selectedIndex1;
        else
            totalFitness -= population[selectedIndex1].getFitness();

        selection = Util.rng.nextDouble(totalFitness);
        int selectedIndex2 = 0;
        for(int i = 0; i < population.length; i++) {
            if(i == selectedIndex1)
                continue;
            if(ranked)
                selection -= population.length - i;
            else
                selection -= population[i].getFitness();
            if(selection < 0)
                break;
            selectedIndex2++;
        }

        IndividualImage selected1 = population[selectedIndex1];
        IndividualImage selected2 = population[selectedIndex2];

        return new  IndividualImage[]{selected1, selected2};
    }

}
