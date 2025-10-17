package com.application.panels;

import com.GA.GeneticAlgorithm;
import com.GA.crossover.*;
import com.GA.fitness.*;
import com.GA.fitness.adjustment.FitnessAdjustment;
import com.GA.fitness.adjustment.NormalisationAdjustment;
import com.GA.generation.GenerationFunction;
import com.GA.generation.RandomBitmapGeneration;
import com.GA.generation.RandomColorGeneration;
import com.GA.mutation.*;
import com.GA.selection.RouletteWheelSelection;
import com.GA.selection.SelectionFunction;
import com.GA.selection.TournamentSelection;
import com.application.Application;
import com.utils.BitMapImage;
import com.utils.ImageRW;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Arrays;

public class GAParametersPanel extends JPanel {

    // Names of all available generation functions
    private static final String GENERATION_FUNCTION_RANDOM_PIXELS = "Random Pixels";
    private static final String GENERATION_FUNCTION_RANDOM_COLOUR = "Random Colour";
    private static final String[] GENERATION_FUNCTIONS = new String[] {
            GENERATION_FUNCTION_RANDOM_PIXELS,
            GENERATION_FUNCTION_RANDOM_COLOUR
    };

    // Names of all available fitness functions
    private static final String FITNESS_FUNCTION_CHECKERBOARD = "Checkerboard";
    private static final String FITNESS_FUNCTION_IMAGE_LIKENESS_SIMPLE = "Simple image likeness";
    private static final String FITNESS_FUNCTION_IMAGE_LIKENESS_STRONG = "Strong image likeness";
    private static final String FITNESS_FUNCTION_IMAGE_LIKENESS_HUE = "Hue-only image likeness";
    private static final String FITNESS_FUNCTION_IMAGE_LIKENESS_LIGHTNESS = "Lightness-only image likeness";
    private static final String[] FITNESS_FUNCTIONS = new String[] {
            FITNESS_FUNCTION_CHECKERBOARD,
            FITNESS_FUNCTION_IMAGE_LIKENESS_SIMPLE,
            FITNESS_FUNCTION_IMAGE_LIKENESS_STRONG,
            FITNESS_FUNCTION_IMAGE_LIKENESS_HUE,
            FITNESS_FUNCTION_IMAGE_LIKENESS_LIGHTNESS
    };

    // Names of all available selection functions
    private static final String SELECTION_FUNCTION_ROULETTE_WHEEL = "Roulette Wheel";
    private static final String SELECTION_FUNCTION_TOURNAMENT = "Tournament";
    private static final String[] SELECTION_FUNCTIONS = new String[] {
            SELECTION_FUNCTION_ROULETTE_WHEEL,
            SELECTION_FUNCTION_TOURNAMENT
    };

    // Names of all available crossover functions
    private static final String CROSSOVER_FUNCTION_PIXELWISE_RGB = "Pixelwise RGB";
    private static final String CROSSOVER_FUNCTION_PIXELWISE_HSL = "Pixelwise HSL";
    private static final String CROSSOVER_FUNCTION_PIXELWISE_STRONG = "Strong Pixelwise";
    private static final String[] CROSSOVER_FUNCTIONS = new String[] {
            CROSSOVER_FUNCTION_PIXELWISE_RGB,
            CROSSOVER_FUNCTION_PIXELWISE_HSL,
            CROSSOVER_FUNCTION_PIXELWISE_STRONG
    };

    // Names of all  available mutation functions
    private static final String MUTATION_FUNCTION_RANDOM_PIXELS_RANDOMIZATION = "Random Pixels Randomization";
    private static final String MUTATION_FUNCTION_RANDOM_PIXELS_ADJUSTMENT = "Random Pixels Adjustment";
    private static final String MUTATION_FUNCTION_STRONG = "Strong pixel adjustments";
    private static final String[] MUTATION_FUNCTIONS = new String[] {
            MUTATION_FUNCTION_RANDOM_PIXELS_RANDOMIZATION,
            MUTATION_FUNCTION_RANDOM_PIXELS_ADJUSTMENT,
            MUTATION_FUNCTION_STRONG
    };

    // Singleton pattern
    private static final GAParametersPanel instance = new GAParametersPanel();

    public static GAParametersPanel getInstance() {
        return instance;
    }

    public GAParametersPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JLabel lblPopulationSize = new JLabel("Population Size");
        JLabel lblElite = new JLabel("Elite");
        JLabel lblReGeneration = new JLabel("Re-Generation");
        JLabel lblGenerationFunction = new JLabel("Generation Function");
        JLabel lblFitnessFunction = new JLabel("Fitness Function");
        JLabel lblSelectionFunction = new JLabel("Selection Function");
        JLabel lblCrossoverFunction = new JLabel("Crossover Function");
        JLabel lblMutationFunction = new JLabel("Mutation Function");

        JTextField txtPopulationSize = new JTextField("1000");
        JTextField txtElite = new JTextField("30");
        JTextField txtReGeneration = new JTextField("50");
        JComboBox<String> cmbGenerationFunction = new JComboBox<String>(GENERATION_FUNCTIONS);
        JComboBox<String> cmbFitnessFunction = new JComboBox<String>(FITNESS_FUNCTIONS);
        JComboBox<String> cmbSelectionFunction = new JComboBox<String>(SELECTION_FUNCTIONS);
        JComboBox<String> cmbCrossoverFunction = new JComboBox<String>(CROSSOVER_FUNCTIONS);
        JComboBox<String> cmbMutationFunction = new JComboBox<String>(MUTATION_FUNCTIONS);

        // Panel for selecting image path for fitness functions that take an image as a parameter
        JPanel pnlFitnessFunctionImagePath = new JPanel();
        pnlFitnessFunctionImagePath.setLayout(new BoxLayout(pnlFitnessFunctionImagePath, BoxLayout.X_AXIS));
        JLabel lblFitnessFunctionImagePath = new JLabel("Target image path: ");
        JTextField txtFitnessFunctionImagePath = new JTextField("benchmarkingImages/cans1.png");
        pnlFitnessFunctionImagePath.add(lblFitnessFunctionImagePath);
        pnlFitnessFunctionImagePath.add(txtFitnessFunctionImagePath);

        lblFitnessFunctionImagePath.setAlignmentX(Component.CENTER_ALIGNMENT);
        txtFitnessFunctionImagePath.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblFitnessFunctionImagePath.setMaximumSize(new Dimension(Integer.MAX_VALUE, lblFitnessFunctionImagePath.getPreferredSize().height));
        txtFitnessFunctionImagePath.setMaximumSize(new Dimension(Integer.MAX_VALUE, txtFitnessFunctionImagePath.getPreferredSize().height));

        JPanel[] optionalFitnessFunctionPanels = new JPanel[] {
          pnlFitnessFunctionImagePath
        };

        // Make changes to the visibility of additional fitness function parameter fields depending on which function is selected
        cmbFitnessFunction.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                // Checkerboard fitness function does not require extra parameters
                if(cmbFitnessFunction.getSelectedItem().toString().equals(FITNESS_FUNCTION_CHECKERBOARD)) {
                    for(JPanel optionalFitnessFunctionPanel : optionalFitnessFunctionPanels) {
                        optionalFitnessFunctionPanel.setVisible(false);
                    }
                }

                // Simple image likeness fitness requires target image path
                else if(cmbFitnessFunction.getSelectedItem().toString().equals(FITNESS_FUNCTION_IMAGE_LIKENESS_SIMPLE)) {
                    for(JPanel optionalFitnessFunctionPanel : optionalFitnessFunctionPanels) {
                        optionalFitnessFunctionPanel.setVisible(false);
                    }
                    pnlFitnessFunctionImagePath.setVisible(true);
                }

                // Strong image likeness fitness requires target image path
                else if(cmbFitnessFunction.getSelectedItem().toString().equals(FITNESS_FUNCTION_IMAGE_LIKENESS_STRONG)) {
                    for(JPanel optionalFitnessFunctionPanel : optionalFitnessFunctionPanels) {
                        optionalFitnessFunctionPanel.setVisible(false);
                    }
                    pnlFitnessFunctionImagePath.setVisible(true);
                }

                // Hue-based image likeness fitness requires target image path
                else if(cmbFitnessFunction.getSelectedItem().toString().equals(FITNESS_FUNCTION_IMAGE_LIKENESS_HUE)) {
                    for(JPanel optionalFitnessFunctionPanel : optionalFitnessFunctionPanels) {
                        optionalFitnessFunctionPanel.setVisible(false);
                    }
                    pnlFitnessFunctionImagePath.setVisible(true);
                }

                // Lightness-based image likeness fitness requires target image path
                else if(cmbFitnessFunction.getSelectedItem().toString().equals(FITNESS_FUNCTION_IMAGE_LIKENESS_LIGHTNESS)) {
                    for(JPanel optionalFitnessFunctionPanel : optionalFitnessFunctionPanels) {
                        optionalFitnessFunctionPanel.setVisible(false);
                    }
                    pnlFitnessFunctionImagePath.setVisible(true);
                }
            }
        });

        JButton btnBeginGA = new JButton("Begin GA");
        btnBeginGA.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int populationSize = Integer.parseInt(txtPopulationSize.getText());
                int elite = Integer.parseInt(txtElite.getText());
                int regeneration = Integer.parseInt(txtReGeneration.getText());
                String generationFunctionOption = cmbGenerationFunction.getSelectedItem().toString();
                String fitnessFunctionOption = cmbFitnessFunction.getSelectedItem().toString();
                String selectionFunctionOption = cmbSelectionFunction.getSelectedItem().toString();
                String crossoverFunctionOption = cmbCrossoverFunction.getSelectedItem().toString();
                String mutationFunctionOption = cmbMutationFunction.getSelectedItem().toString();

                GenerationFunction generationFunction = createGenerationFunction(generationFunctionOption, null);

                String[] fitnessFunctionParameters = new String[] {
                        txtFitnessFunctionImagePath.getText()
                };
                FitnessFunction fitnessFunction = createFitnessFunction(fitnessFunctionOption, fitnessFunctionParameters);

                SelectionFunction selectionFunction = createSelectionFunction(selectionFunctionOption, null);
                // TODO: add parameters for selection functions, allow them to be ranked, allow tournament selection parametrisation

                CrossoverFunction crossoverFunction = createCrossoverFunction(crossoverFunctionOption, null, new Object[] {fitnessFunction});

                MutationFunction mutationFunction = createMutationFunction(mutationFunctionOption, null, new Object[] {fitnessFunction});

                FitnessAdjustment fitnessAdjustment = new NormalisationAdjustment();

                ImageScreen.currentGA = new GeneticAlgorithm(
                        ImageScreen.currentImageHeight,
                        ImageScreen.currentImageWidth,
                        populationSize,
                        elite,
                        0,
                        regeneration,
                        generationFunction,
                        fitnessFunction,
                        selectionFunction,
                        crossoverFunction,
                        mutationFunction,
                        fitnessAdjustment
                );

                RightSidebar.layout.show(RightSidebar.getInstance(), "GA Generations");
            }
        });

        add(lblPopulationSize);
        add(txtPopulationSize);
        add(lblElite);
        add(txtElite);
        add(lblReGeneration);
        add(txtReGeneration);
        add(lblGenerationFunction);
        add(cmbGenerationFunction);

        // Add fitness function selector with all extra option panels
        add(lblFitnessFunction);
        add(cmbFitnessFunction);
        for(JPanel optionalFitnessFunctionPanel : optionalFitnessFunctionPanels) {
            add(optionalFitnessFunctionPanel);
            optionalFitnessFunctionPanel.setVisible(false);
        }

        add(lblSelectionFunction);
        add(cmbSelectionFunction);
        add(lblCrossoverFunction);
        add(cmbCrossoverFunction);
        add(lblMutationFunction);
        add(cmbMutationFunction);
        add(btnBeginGA);

        lblPopulationSize.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblElite.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblReGeneration.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblGenerationFunction.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblFitnessFunction.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblSelectionFunction.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblCrossoverFunction.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblMutationFunction.setAlignmentX(Component.CENTER_ALIGNMENT);
        cmbGenerationFunction.setAlignmentX(Component.CENTER_ALIGNMENT);
        cmbFitnessFunction.setAlignmentX(Component.CENTER_ALIGNMENT);
        cmbSelectionFunction.setAlignmentX(Component.CENTER_ALIGNMENT);
        cmbCrossoverFunction.setAlignmentX(Component.CENTER_ALIGNMENT);
        cmbMutationFunction.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnBeginGA.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblPopulationSize.setMaximumSize(new Dimension(Integer.MAX_VALUE, lblPopulationSize.getPreferredSize().height));
        lblElite.setMaximumSize(new Dimension(Integer.MAX_VALUE, lblElite.getPreferredSize().height));
        lblReGeneration.setMaximumSize(new Dimension(Integer.MAX_VALUE, lblReGeneration.getPreferredSize().height));
        lblGenerationFunction.setMaximumSize(new Dimension(Integer.MAX_VALUE, lblGenerationFunction.getPreferredSize().height));
        lblFitnessFunction.setMaximumSize(new Dimension(Integer.MAX_VALUE, lblFitnessFunction.getPreferredSize().height));
        lblSelectionFunction.setMaximumSize(new Dimension(Integer.MAX_VALUE, lblSelectionFunction.getPreferredSize().height));
        lblCrossoverFunction.setMaximumSize(new Dimension(Integer.MAX_VALUE, lblCrossoverFunction.getPreferredSize().height));
        lblMutationFunction.setMaximumSize(new Dimension(Integer.MAX_VALUE, lblMutationFunction.getPreferredSize().height));
        cmbGenerationFunction.setMaximumSize(new Dimension(Integer.MAX_VALUE, cmbGenerationFunction.getPreferredSize().height));
        cmbFitnessFunction.setMaximumSize(new Dimension(Integer.MAX_VALUE, cmbFitnessFunction.getPreferredSize().height));
        cmbSelectionFunction.setMaximumSize(new Dimension(Integer.MAX_VALUE, cmbSelectionFunction.getPreferredSize().height));
        cmbCrossoverFunction.setMaximumSize(new Dimension(Integer.MAX_VALUE, cmbCrossoverFunction.getPreferredSize().height));
        cmbMutationFunction.setMaximumSize(new Dimension(Integer.MAX_VALUE, cmbMutationFunction.getPreferredSize().height));
        txtPopulationSize.setMaximumSize(new Dimension(Integer.MAX_VALUE, txtPopulationSize.getPreferredSize().height));
        txtElite.setMaximumSize(new Dimension(Integer.MAX_VALUE, txtElite.getPreferredSize().height));
        txtReGeneration.setMaximumSize(new Dimension(Integer.MAX_VALUE, txtReGeneration.getPreferredSize().height));
        btnBeginGA.setMaximumSize(new Dimension(Integer.MAX_VALUE, btnBeginGA.getPreferredSize().height));
    }

    private GenerationFunction createGenerationFunction(String functionName, String[] parameters) {
        if(functionName.equals(GENERATION_FUNCTION_RANDOM_PIXELS)) {
            GenerationFunction generationFunction = new RandomBitmapGeneration();
            return generationFunction;
        }
        if(functionName.equals(GENERATION_FUNCTION_RANDOM_COLOUR)) {
            GenerationFunction generationFunction = new RandomColorGeneration();
            return generationFunction;
        }
        if(functionName.equals(GENERATION_FUNCTION_RANDOM_COLOUR)) {
            GenerationFunction generationFunction = new RandomColorGeneration();
            return generationFunction;
        }
        System.out.println("Function not found: " + functionName); // TODO: maybe replace with an exception
        return null;
    }

    private FitnessFunction createFitnessFunction(String functionName, String[] parameters) {
        if(functionName.equals(FITNESS_FUNCTION_CHECKERBOARD)) {
            FitnessFunction fitnessFunction = new CheckerboardFitness();
            return fitnessFunction;
        }
        if(functionName.equals(FITNESS_FUNCTION_IMAGE_LIKENESS_SIMPLE)) {
            String targetImagePath = parameters[0];
            try {
                BitMapImage image = ImageRW.readImage(targetImagePath);
                FitnessFunction fitnessFunction = new ImageLikenessFitness(image, ImageScreen.currentImageHeight, ImageScreen.currentImageWidth, false);
                return fitnessFunction;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if(functionName.equals(FITNESS_FUNCTION_IMAGE_LIKENESS_STRONG)) { // TODO: cleanup
            String targetImagePath = parameters[0];
            try {
                BitMapImage image = ImageRW.readImage(targetImagePath);
                FitnessFunction fitnessFunction1 = new CheckerboardFitness();
                //FitnessFunction fitnessFunction2 = new CheckerboardFitness();
                FitnessFunction fitnessFunction3 = new CheckerboardFitness();
                FitnessFunction fitnessFunction4 = new CheckerboardFitness();
                FitnessFunction fitnessFunction5 = new CheckerboardFitness();
                try {
                    fitnessFunction1 = new HueLikenessFitness(image, ImageScreen.currentImageHeight, ImageScreen.currentImageWidth);
                    //fitnessFunction2 = new SaturationLikenessFitness(image, ImageScreen.currentImageHeight, ImageScreen.currentImageWidth);
                    fitnessFunction3 = new LightnessLikenessFitness(image, ImageScreen.currentImageHeight, ImageScreen.currentImageWidth);
                    fitnessFunction4 = new ImageLikenessFitness(image, ImageScreen.currentImageHeight, ImageScreen.currentImageWidth, true);
                    fitnessFunction5 = new ImageLikenessFitness(image, ImageScreen.currentImageHeight, ImageScreen.currentImageWidth, false);
                } catch(Exception exception) {
                    exception.printStackTrace();
                    System.exit(0);
                }
                EnsembleFitnessFunction fitnessFunction = new EnsembleFitnessFunction();
                fitnessFunction.addFunction(fitnessFunction1, 0.5);
                //fitnessFunction.addFunction(fitnessFunction2, 1.0);
                fitnessFunction.addFunction(fitnessFunction3, 0.5);
                fitnessFunction.addFunction(fitnessFunction4, 1.5);
                fitnessFunction.addFunction(fitnessFunction5, 1.5);
                fitnessFunction.makeNormalised(ImageScreen.currentImageHeight, ImageScreen.currentImageWidth);
                return fitnessFunction;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if(functionName.equals(FITNESS_FUNCTION_IMAGE_LIKENESS_HUE)) {
            String targetImagePath = parameters[0];
            try {
                BitMapImage image = ImageRW.readImage(targetImagePath);
                FitnessFunction fitnessFunction = new HueLikenessFitness(image, ImageScreen.currentImageHeight, ImageScreen.currentImageWidth);
                return fitnessFunction;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if(functionName.equals(FITNESS_FUNCTION_IMAGE_LIKENESS_LIGHTNESS)) {
            String targetImagePath = parameters[0];
            try {
                BitMapImage image = ImageRW.readImage(targetImagePath);
                FitnessFunction fitnessFunction = new LightnessLikenessFitness(image, ImageScreen.currentImageHeight, ImageScreen.currentImageWidth);
                return fitnessFunction;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println("Function not found: " + functionName); // TODO: maybe replace with an exception
        return null;
    }

    private SelectionFunction createSelectionFunction(String functionName, String[] parameters) {
        if(functionName.equals(SELECTION_FUNCTION_ROULETTE_WHEEL)) {
            SelectionFunction selectionFunction = new RouletteWheelSelection();
            selectionFunction.makeRanked(); // TODO: make parametrised
            return selectionFunction;
        }
        if(functionName.equals(SELECTION_FUNCTION_TOURNAMENT)) {
            SelectionFunction selectionFunction = new TournamentSelection(5); // TODO: introduce parametrisation here
            return selectionFunction;
        }
        System.out.println("Function not found: " + functionName); // TODO: maybe replace with an exception
        return null;
    }

    public CrossoverFunction createCrossoverFunction(String functionName, String[] parameters, Object[] refs) {
        if(functionName.equals(CROSSOVER_FUNCTION_PIXELWISE_RGB)) {
            CrossoverFunction crossoverFunction = new PixelwiseRGBCrossover();
            return crossoverFunction;
        }
        if(functionName.equals(CROSSOVER_FUNCTION_PIXELWISE_HSL)) {
            CrossoverFunction crossoverFunction = new PixelwiseHSLCrossover();
            return crossoverFunction;
        }
        if(functionName.equals(CROSSOVER_FUNCTION_PIXELWISE_STRONG)) {
            FitnessFunction fitnessFunction = (FitnessFunction) refs[0];
            CrossoverFunction crossOverFunction1 = new PixelwiseRGBCrossover();
            crossOverFunction1.makeWeighted();
            crossOverFunction1.makeGreedy(3, fitnessFunction);
            CrossoverFunction crossOverFunction2 = new BlendCrossover();
            crossOverFunction2.makeWeighted();
            EnsembleCrossoverFunction crossoverFunction = new EnsembleCrossoverFunction();
            crossoverFunction.addFunction(crossOverFunction1, 1.0);
            crossoverFunction.addFunction(crossOverFunction2, 0.01);
            return crossoverFunction;
        }
        System.out.println("Function not found: " + functionName); // TODO: maybe replace with an exception
        return null;
    }

    public MutationFunction createMutationFunction(String functionName, String[] parameters, Object[] refs) {
        if(functionName.equals(MUTATION_FUNCTION_RANDOM_PIXELS_RANDOMIZATION)) {
            MutationFunction mutationFunction = new RandomPixelsMutation(ImageScreen.currentImageHeight, ImageScreen.currentImageWidth); // TODO: why does this constructor require dimensions anyway?
            return mutationFunction;
        }
        if(functionName.equals(MUTATION_FUNCTION_RANDOM_PIXELS_ADJUSTMENT)) {
            MutationFunction mutationFunction = new RandomPixelAdjustmentsMutation(0.3, 0.1, 5);
            return mutationFunction;
        }
        if(functionName.equals(MUTATION_FUNCTION_STRONG)) {
            FitnessFunction fitnessFunction = (FitnessFunction) refs[0];
            EnsembleMutation mutationFunction = new EnsembleMutation(1.0);
            MutationFunction mutationFunction1 = new RandomPixelAdjustmentsMutation(1.0, 0.1, 3);
            MutationFunction mutationFunction2 = new SmoothMutation(1.0);
            MutationFunction mutationFunction3 = new RandomPixelsMutation(1.0, 0.1);
            mutationFunction1.makeGreedy(5, fitnessFunction);
            mutationFunction1.makeNoHarm(fitnessFunction);
            mutationFunction2.makeNoHarm(fitnessFunction);
            mutationFunction3.makeGreedy(5, fitnessFunction);
            mutationFunction3.makeNoHarm(fitnessFunction);
            mutationFunction.addFunction(mutationFunction1, 1.0);
            mutationFunction.addFunction(mutationFunction2, 1.0);
            mutationFunction.addFunction(mutationFunction3, 1.0);
            mutationFunction.makeNoHarm(fitnessFunction);
            return mutationFunction;
        }
        System.out.println("Function not found: " + functionName); // TODO: maybe replace with an exception
        return null;
    }

}
