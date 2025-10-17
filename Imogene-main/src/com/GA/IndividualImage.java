package com.GA;

import com.utils.BitMapImage;

public class IndividualImage {

    private BitMapImage image;
    private boolean fitnessAssigned;
    private double fitness;

    public IndividualImage(BitMapImage image) {
        this.image = image;
    }

    public BitMapImage getImage() {
        return image;
    }

    public void setImage(BitMapImage image) {
        this.image = image;
    }

    public void assignFitness(double fitness) {
        this.fitness = fitness;
        this.fitnessAssigned = true;
    }

    public boolean fitnessAssigned() {
        return fitnessAssigned;
    }

    public double getFitness() {
        return fitness;
    }

    public void unassignFitness() {
        this.fitnessAssigned = false;
    }

    public IndividualImage copy() {
        int[][][] rgb = image.getRgb();
        int[][][] rgbCopy = new int[rgb.length][rgb[0].length][3];
        for(int y = 0; y < rgb.length; y++)
            for(int x = 0; x < rgb[0].length; x++)
                for(int c = 0; c < 3; c++)
                    rgbCopy[y][x][c] =  rgb[y][x][c];
        IndividualImage copy = new IndividualImage(image);
        if(fitnessAssigned)
            copy.assignFitness(fitness);
        return copy;
    }

}
