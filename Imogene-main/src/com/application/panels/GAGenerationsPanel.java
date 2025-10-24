package com.application.panels;

import com.API.GAConnector;
import com.application.Application;
import com.utils.BitMapImage;
import com.utils.ImageUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Map;
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
        btnRun.addActionListener(e -> {
            int generations;
            try {
                generations = Integer.parseInt(txtGenerations.getText());
            } catch (NumberFormatException ex) {
                System.out.println("Can't interpret \"" + txtGenerations.getText() + "\" as an integer");
                return;
            }
            ImageScreen.halt = false;
            btnRun.setEnabled(false);
            btnHalt.setEnabled(true);
            btnReset.setEnabled(false);
            btnApplySmoothing.setEnabled(false);
            generationsRunning = generations;

            if (RightSidebar.getInstance().isRemote()) {
                runRemote(generations, btnRun, btnHalt, btnReset, btnApplySmoothing);
            } else {
                runLocal(generations, btnRun, btnHalt, btnReset, btnApplySmoothing);
            }
        });

        btnHalt.addActionListener(e -> {
            status = "Stopping...";
            updateStatusString();
            ImageScreen.halt = true;
            btnHalt.setEnabled(false);
            btnReset.setEnabled(false);
            btnApplySmoothing.setEnabled(false);

            if (RightSidebar.getInstance().isRemote() && ImageScreen.currentSessionId != null) {
                new Thread(() -> {
                    try {
                        GAConnector.halt(ImageScreen.currentSessionId);
                    } catch (IOException | InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }).start();
            }
        });
        btnHalt.setEnabled(false);

        // Mmodify the btnReset action listener
        btnReset.addActionListener(e -> {
            if (RightSidebar.getInstance().isRemote() && ImageScreen.currentSessionId != null) {
                new Thread(() -> {
                    try {
                        GAConnector.reset(ImageScreen.currentSessionId);
                        ImageScreen.currentSessionId = null;
                    } catch (IOException | InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }).start();
            }
            ImageScreen.currentGA = null;
            RightSidebar.layout.show(RightSidebar.getInstance(), "GA Params");
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

    //running ga locally for local mode

    private void runLocal(int generations, JButton btnRun, JButton btnHalt, JButton btnReset, JButton btnApplySmoothing) {
        new Thread(() -> {
            status = "Running";
            updateStatusString();
            for (int i = 0; i < generations; i++) {
                if (ImageScreen.halt) break;
                currentGenerationNumber = i + 1;
                updateStatusString();
                ImageScreen.currentGA.gaStep();
                if (ImageScreen.halt) break;
                SwingUtilities.invokeLater(() -> {
                    ImageScreen.currentImage = ImageScreen.currentGA.best.getLast().getImage();
                    ImageScreen.redraw();
                });
            }
            btnRun.setEnabled(true);
            btnHalt.setEnabled(false);
            btnReset.setEnabled(true);
            btnApplySmoothing.setEnabled(true);
            status = "Finished";
            updateStatusString();
        }).start();
    }

    //running ga remotely for remote mode
    private void runRemote(int generations, JButton btnRun, JButton btnHalt, JButton btnReset, JButton btnApplySmoothing) {
        new Thread(() -> {
            try {
                GAConnector.run(ImageScreen.currentSessionId, generations);
                status = "Running";
                boolean isRunning = true;
                while (isRunning && !ImageScreen.halt) {
                    try {
                        Thread.sleep(1000); // Poll every second
                        Map<String, Object> statusMap = GAConnector.getStatus(ImageScreen.currentSessionId);
                        isRunning = (Boolean) statusMap.getOrDefault("running", false);
                        currentGenerationNumber = ((Number) statusMap.getOrDefault("generation", 0)).intValue();
                        updateStatusString();

                        BitMapImage bestImage = GAConnector.getBestImage(ImageScreen.currentSessionId);
                        SwingUtilities.invokeLater(() -> {
                            ImageScreen.currentImage = bestImage;
                            ImageScreen.redraw();
                        });
                    } catch (IOException | InterruptedException ex) {
                        ex.printStackTrace();
                        status = "Error";
                        isRunning = false;
                    }
                }
            } catch (IOException | InterruptedException ex) {
                ex.printStackTrace();
                status = "Error";
            } finally {
                SwingUtilities.invokeLater(() -> {
                    btnRun.setEnabled(true);
                    btnHalt.setEnabled(false);
                    btnReset.setEnabled(true);
                    btnApplySmoothing.setEnabled(true); //smoothing local only
                    if (!status.equals("Error")) {
                        status = "Finished";
                    }
                    updateStatusString();
                });
            }
        }).start();
    }

}
