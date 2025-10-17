package com.GA.selection;

import com.GA.IndividualImage;

/**
 * Selection functions are used to select parents from a population.
 *
 * A ranked option is available that uses the rank of an individual
 * instead of its fitness value. This can prevent stagnation when
 * most individuals in the population have very similar high fitness.
 *
 *
 */
public abstract class SelectionFunction {

    // Ranked selection uses ranks instead of fitnesses
    protected boolean ranked;

    public void makeRanked() {
        ranked = true;
    }

    public abstract IndividualImage[] select(IndividualImage[] population);

}
