package com.application.panels;

import com.GA.GeneticAlgorithm;
import com.utils.BitMapImage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class ImageScreen extends JPanel {

    public static DrawingPanel drawingPanel;

    // Make upscaling customisable (no longer final)
    public static boolean UPSCALE = true;
    public static int UPSCALE_FACTOR = 6;

    public static int currentImageHeight = 100;
    public static int currentImageWidth = 133;

    public static BitMapImage currentImage;
    public static GeneticAlgorithm currentGA;
    public static boolean halt;

    // Singleton pattern
    private static final ImageScreen instance = new ImageScreen();
    public static ImageScreen getInstance() { return instance; }

    private ImageScreen() {
        super(new BorderLayout());

        // Left & right sidebars
        JPanel leftPanel = LeftSidebar.getInstance();
        JPanel rightPanel = RightSidebar.getInstance();

        // Main image drawing panel
        drawingPanel = new DrawingPanel(currentImageHeight, currentImageWidth);
        drawingPanel.setLayout(new GridBagLayout());

        // Split panes to separate the panels
        JSplitPane splitLeftCenter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, drawingPanel);
        splitLeftCenter.setResizeWeight(0.10);
        JSplitPane splitAll = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, splitLeftCenter, rightPanel);
        splitAll.setResizeWeight(0.85);

        add(splitAll, BorderLayout.CENTER);

        // ===== Add Control Panel for Customisation =====
        JPanel controlPanel = new JPanel();
        controlPanel.setBorder(BorderFactory.createTitledBorder("Image Settings"));

        // Width & Height Spinners
        JSpinner widthSpinner = new JSpinner(new SpinnerNumberModel(currentImageWidth, 10, 1000, 10));
        JSpinner heightSpinner = new JSpinner(new SpinnerNumberModel(currentImageHeight, 10, 1000, 10));

        // Upscale Factor Spinner
        JSpinner upscaleSpinner = new JSpinner(new SpinnerNumberModel(UPSCALE_FACTOR, 1, 20, 1));

        // Checkbox to toggle upscaling
        JCheckBox upscaleCheck = new JCheckBox("Upscale", UPSCALE);

        JButton applyButton = new JButton("Apply");

        // Apply new settings when clicked
        applyButton.addActionListener((ActionEvent e) -> {
            currentImageWidth = (int) widthSpinner.getValue();
            currentImageHeight = (int) heightSpinner.getValue();
            UPSCALE_FACTOR = (int) upscaleSpinner.getValue();
            UPSCALE = upscaleCheck.isSelected();

            refreshDrawingPanel();
        });

        controlPanel.add(new JLabel("Width:"));
        controlPanel.add(widthSpinner);
        controlPanel.add(new JLabel("Height:"));
        controlPanel.add(heightSpinner);
        controlPanel.add(new JLabel("Upscale x"));
        controlPanel.add(upscaleSpinner);
        controlPanel.add(upscaleCheck);
        controlPanel.add(applyButton);

        add(controlPanel, BorderLayout.SOUTH);
    }

    // ===== Recreate DrawingPanel with new size and upscale =====
    private void refreshDrawingPanel() {
        // Remove old panel and add a new one with updated settings
        remove(drawingPanel);

        drawingPanel = new DrawingPanel(currentImageHeight, currentImageWidth);
        drawingPanel.setLayout(new GridBagLayout());

        // Rebuild layout (left + new drawing panel + right)
        JPanel leftPanel = LeftSidebar.getInstance();
        JPanel rightPanel = RightSidebar.getInstance();

        JSplitPane splitLeftCenter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, drawingPanel);
        splitLeftCenter.setResizeWeight(0.10);
        JSplitPane splitAll = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, splitLeftCenter, rightPanel);
        splitAll.setResizeWeight(0.85);

        add(splitAll, BorderLayout.CENTER);
        revalidate();
        repaint();

        if (currentImage != null) {
            redraw();
        }
    }

    // ===== Drawing & Redrawing =====
    public static void drawPixel(int x, int y, Color color) {
        if (UPSCALE) {
            for (int xc = x * UPSCALE_FACTOR; xc < x * UPSCALE_FACTOR + UPSCALE_FACTOR; xc++) {
                for (int yc = y * UPSCALE_FACTOR; yc < y * UPSCALE_FACTOR + UPSCALE_FACTOR; yc++) {
                    drawingPanel.setPixel(xc, yc, color);
                }
            }
        } else {
            drawingPanel.setPixel(x, y, color);
        }
    }

    public static void redraw() {
        if (currentImage != null)
            paintImage(currentImage);
    }

    private static void paintImage(BitMapImage image) {
        int[][][] bitmap = image.getRgb();
        for (int y = 0; y < currentImageHeight; y++) {
            for (int x = 0; x < currentImageWidth; x++) {
                drawPixel(x, y, new Color(bitmap[y][x][0], bitmap[y][x][1], bitmap[y][x][2]));
            }
        }
    }
}
