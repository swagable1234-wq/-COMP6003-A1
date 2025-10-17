package com.application.panels;

import com.GA.fitness.*;
import com.GA.mutation.*;

import javax.swing.*;
import java.awt.*;

public class RightSidebar extends JPanel {

    // Card layout elements visible to other application panels
    protected static final CardLayout layout = new CardLayout();

    // Singleton pattern
    private static final RightSidebar instance = new RightSidebar();

    public static RightSidebar getInstance() {
        return instance;
    }

    public RightSidebar() {
        super(layout);

        // First panel, just the "Initialise GA" button
        JPanel pnlInitialiseGA = GAInitPanel.getInstance();

        // Second panel, GA hyperparameters
        JPanel pnlGAHyperparameters = GAParametersPanel.getInstance();

        // Third panel, GA generations and control buttons
        JPanel pnlGenerations = GAGenerationsPanel.getInstance();

        // Add panels to the right sidebar card layout
        add(pnlInitialiseGA, "GA Init");
        add(pnlGAHyperparameters, "GA Params");
        add(pnlGenerations, "GA Generations");

        // Display the first card
        layout.show(this, "GA Init");

    }

}
