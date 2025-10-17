package com.GA.selection;

import com.GA.IndividualImage;
import com.utils.Util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Tournament selection is a conventional selection technique that
 * tends to be less biased towards very high fitness values, thus
 * potentially preventing early overfitting.
 *
 * This one can be instantiated as either a deterministic tournament,
 * where the fittest individual always wins the tournament, or as a
 * probabilistic tournament, where the probability of individual
 * winning is correlated with their rank in the tournament.
 *
 * Ranked option does not impact this selection method as it already
 * only uses the ranks of the individuals for selection.
 *
 */

public class TournamentSelection extends SelectionFunction{

    private boolean deterministic;
    private int tournamentSize;
    private double p;

    public TournamentSelection(int tournamentSize) {
        this.tournamentSize = tournamentSize;
        this.deterministic = true;
    }

    public TournamentSelection(int tournamentSize, double p) {
        this.tournamentSize = tournamentSize;
        this.p = p;
        this.deterministic = false;

    }

    public IndividualImage[] select(IndividualImage[] population) {
        IndividualImage parent1 = runTournament(population);
        IndividualImage parent2 = runTournament(population);
        while(parent1 == parent2)
            parent2 = runTournament(population);

        return new IndividualImage[] {parent1, parent2};
    }

    private IndividualImage runTournamentDeterministic(IndividualImage[] population) {
        IndividualImage best = null;
        for (int i = 0; i < tournamentSize; i++) {
            IndividualImage candidate = population[Util.rng.nextInt(population.length)];
            if (best == null || candidate.getFitness() > best.getFitness()) {
                best = candidate;
            }
        }
        return best;
    }

    private IndividualImage runTournament(IndividualImage[] population) {
        if(deterministic)
            return runTournamentDeterministic(population);
        else
            return runTournamentProbabilistic(population);
    }

    private IndividualImage runTournamentProbabilistic(IndividualImage[] population) {
        // Randomly sample tournamentSize individuals
        List<IndividualImage> tournament = new ArrayList<>();
        for (int i = 0; i < tournamentSize; i++) {
            tournament.add(population[Util.rng.nextInt(population.length)]);
        }

        // Sort descending by fitness
        tournament.sort(Comparator.comparingDouble(IndividualImage::getFitness).reversed());

        // Assign probabilities by rank
        double r = Util.rng.nextDouble();
        double cumulative = 0.0;
        for (int i = 0; i < tournament.size(); i++) {
            double prob = p * Math.pow(1 - p, i);
            cumulative += prob;
            if (r < cumulative) {
                return tournament.get(i);
            }
        }

        return tournament.getLast();
    }


}
