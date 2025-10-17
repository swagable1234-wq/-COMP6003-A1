package com.application.panels;

import com.GA.GeneticAlgorithm;
import com.GA.crossover.BlendCrossover;
import com.GA.crossover.CrossoverFunction;
import com.GA.crossover.EnsembleCrossoverFunction;
import com.GA.crossover.PixelwiseRGBCrossover;
import com.GA.fitness.*;
import com.GA.fitness.adjustment.FitnessAdjustment;
import com.GA.fitness.adjustment.NormalisationAdjustment;
import com.GA.generation.GenerationFunction;
import com.GA.generation.RandomBitmapGeneration;
import com.GA.mutation.*;
import com.GA.selection.RouletteWheelSelection;
import com.GA.selection.SelectionFunction;
import com.application.Application;
import com.utils.BitMapImage;
import com.utils.ImageRW;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ImageScreen extends JPanel {

    public static DrawingPanel drawingPanel;

    public static final boolean UPSCALE = true;
    public static final int UPSCALE_FACTOR = 6;

    public static int currentImageHeight = 100;
    public static int currentImageWidth = 133;

    public static BitMapImage currentImage;
    public static GeneticAlgorithm currentGA;
    public static boolean halt;

    // Singleton pattern
    private static final ImageScreen instance = new ImageScreen();

    public static ImageScreen getInstance() {
        return instance;
    }

    public ImageScreen() {

        // Border layout to accommodate the central image screen and the two sidebars
        super(new BorderLayout());

        // Left sidebar
        JPanel leftPanel = LeftSidebar.getInstance();

        // Middle image panel
        drawingPanel = new DrawingPanel(currentImageHeight, currentImageWidth);
        drawingPanel.setLayout(new GridBagLayout());

        // Right sidebar
        JPanel rightPanel = RightSidebar.getInstance();

        // Create splitPanes to separate the screen into three resizable panels
        JSplitPane splitLeftCenter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, drawingPanel);
        splitLeftCenter .setResizeWeight(0.10);
        JSplitPane splitAll = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, splitLeftCenter, rightPanel);
        splitAll.setResizeWeight(0.85);

        // Create the image screen JPanel and return it
        setLayout(new BorderLayout()); // TODO: why is this here?
        add(splitAll, BorderLayout.CENTER);

    }

    public static void drawPixel(int x, int y, Color color) {
        if(ImageScreen.UPSCALE) {
            for(int xc = x*ImageScreen.UPSCALE_FACTOR; xc < x*ImageScreen.UPSCALE_FACTOR + ImageScreen.UPSCALE_FACTOR; xc++)
                for(int yc = y*ImageScreen.UPSCALE_FACTOR; yc < y*ImageScreen.UPSCALE_FACTOR + ImageScreen.UPSCALE_FACTOR; yc++) {
                    ImageScreen.drawingPanel.setPixel(xc, yc, color);
                }
        }
        else
            ImageScreen.drawingPanel.setPixel(x, y, color);
    }

    public static void redraw() {
        paintImage(ImageScreen.currentImage);
    }

    private static void paintImage(BitMapImage image) {
        int[][][] bitmap = image.getRgb();
        for(int y = 0; y < ImageScreen.currentImageHeight; y++) {
            for(int x = 0; x < ImageScreen.currentImageWidth; x++) {
                drawPixel(x, y, new Color(bitmap[y][x][0], bitmap[y][x][1], bitmap[y][x][2]));
            }
        }
    }


}
