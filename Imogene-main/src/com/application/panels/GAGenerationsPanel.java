package com.application.panels;

import com.application.Application;
import com.utils.BitMapImage;
import com.utils.ImageUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.function.Function;

public class GAGenerationsPanel extends JPanel {

    public static int generationsRunning;
    public static int currentGenerationNumber;
    public static String status;
    public static JLabel statusLabel;

    // Singleton pattern
    private static final GAGenerationsPanel instance = new GAGenerationsPanel();

    public static GAGenerationsPanel getInstance() {
        return instance;
    }

    private GAGenerationsPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        JLabel lblGenerations = new JLabel("Generations");
        JTextField txtGenerations = new JTextField();
        JButton btnRun = new JButton("Run");
        JButton btnHalt = new JButton("Halt");
        JButton btnReset = new JButton("Reset");
        JButton btnApplySmoothing = new JButton("Apply Smoothing");
        JButton btnSaveAsGif = new JButton("Save as GIF");
        btnRun.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int generations;
                try {
                    generations = Integer.parseInt(txtGenerations.getText());
                }
                catch (NumberFormatException ex) {
                    System.out.println("Can't interpret \"" + txtGenerations.getText() + "\" as an integer");
                    return;
                }
                ImageScreen.halt = false;
                btnRun.setEnabled(false);
                btnHalt.setEnabled(true);
                btnReset.setEnabled(false);
                btnApplySmoothing.setEnabled(false);
                generationsRunning = generations;
                new Thread(() -> {
                    status = "Running";
                    updateStatusString();
                    for(int i = 0; i < generations; i++) {
                        if(ImageScreen.halt) break;
                        currentGenerationNumber = i + 1;
                        updateStatusString();
                        ImageScreen.currentGA.gaStep();
                        if(ImageScreen.halt) break;
                        //System.out.println("Step");
                        SwingUtilities.invokeLater(() -> {
                            ImageScreen.currentImage = ImageScreen.currentGA.best.getLast().getImage();
                            ImageScreen.redraw();
                        });
                        //System.out.println("Thread painted");
                    }
                    btnRun.setEnabled(true);
                    btnHalt.setEnabled(false);
                    btnReset.setEnabled(true);
                    btnApplySmoothing.setEnabled(true);
                    status = "Finished";
                    updateStatusString();
                    //ga.finished = true; // TODO: no longer needed
                }).start();
            }
        });

        btnHalt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                status = "Stopping...";
                updateStatusString();
                ImageScreen.halt = true;
                btnHalt.setEnabled(false);
                btnReset.setEnabled(false);
                btnApplySmoothing.setEnabled(false);
            }
        });
        btnHalt.setEnabled(false);

        btnReset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RightSidebar.layout.show(RightSidebar.getInstance(), "GA Params");
            }
        });

        btnApplySmoothing.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Function<BitMapImage, BitMapImage> function = new Function<BitMapImage, BitMapImage>() {
                    @Override
                    public BitMapImage apply(BitMapImage image) {
                        return ImageUtils.smoothFilter(image, 0.8,0.025);
                    }
                };
                ImageScreen.currentGA.applyToAll(function);
                SwingUtilities.invokeLater(() -> {
                    ImageScreen.currentImage = ImageScreen.currentGA.best.getLast().getImage();
                    ImageScreen.redraw();
                });
            }
        });

        btnSaveAsGif.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Application.saveGAAsGIF();
            }
        });

        statusLabel = new JLabel("GA has been initialised");

        add(lblGenerations);
        add(txtGenerations);
        add(btnRun);
        add(btnHalt);
        add(btnReset);
        add(btnApplySmoothing);
        add(btnSaveAsGif);
        add(statusLabel);


        lblGenerations.setAlignmentX(Component.CENTER_ALIGNMENT);
        txtGenerations.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnRun.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnHalt.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnApplySmoothing.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnReset.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnSaveAsGif.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblGenerations.setMaximumSize(new Dimension(Integer.MAX_VALUE, lblGenerations.getPreferredSize().height));
        txtGenerations.setMaximumSize(new Dimension(Integer.MAX_VALUE, txtGenerations.getPreferredSize().height));
        btnRun.setMaximumSize(new Dimension(Integer.MAX_VALUE, btnRun.getPreferredSize().height));
        btnHalt.setMaximumSize(new Dimension(Integer.MAX_VALUE, btnHalt.getPreferredSize().height));
        btnApplySmoothing.setMaximumSize(new Dimension(Integer.MAX_VALUE, btnApplySmoothing.getPreferredSize().height));
        statusLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, statusLabel.getPreferredSize().height));
        btnReset.setMaximumSize(new Dimension(Integer.MAX_VALUE, btnReset.getPreferredSize().height));
        btnSaveAsGif.setMaximumSize(new Dimension(Integer.MAX_VALUE, btnSaveAsGif.getPreferredSize().height));
    }

    public static void updateStatusString() {
        String statusString = "";
        if("Running".equals(status))
            statusString = "Running, generation " + currentGenerationNumber + "/" + generationsRunning;
        else if("Finished".equals(status))
            statusString = "Finished after " + currentGenerationNumber + " generations";
        else
            statusString = status;
        statusLabel.setText(statusString);
    }

}
