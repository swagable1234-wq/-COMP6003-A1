package com.application;

import com.utils.BitMapImage;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

/**
 * Handles saving and loading images for the Imogene application.
 */
public class SaveLoad {

    /**
     * Opens a file chooser and allows the user to save the given image.
     * @param image the BufferedImage to save
     * @return true if the image was saved successfully, false otherwise
     */
    public static boolean saveImage(BitMapImage image) {
        if (image == null) {
            JOptionPane.showMessageDialog(null, "No image to save!", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Image");
        fileChooser.setSelectedFile(new File("image.png"));

        int userSelection = fileChooser.showSaveDialog(null);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();

            // Default to .png if no extension is given
            String fileName = fileToSave.getAbsolutePath();
            if (!fileName.contains(".")) {
                fileName += ".png";
                fileToSave = new File(fileName);
            }

            try {
                String format = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
                ImageIO.write((RenderedImage) image, format, fileToSave);
                JOptionPane.showMessageDialog(null, "Image saved successfully!");
                return true;
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Error saving image: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        return false;
    }

    /**
     * Opens a file chooser and allows the user to load an image.
     * @return the loaded BufferedImage, or null if cancelled or failed
     */
    public static BitMapImage loadImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Open Image");

        int userSelection = fileChooser.showOpenDialog(null);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToOpen = fileChooser.getSelectedFile();
            try {
                BufferedImage image = ImageIO.read(fileToOpen);
                if (image == null) {
                    JOptionPane.showMessageDialog(null, "Invalid image file.", "Error", JOptionPane.ERROR_MESSAGE);
                    return null;
                }
                BitMapImage finalIMG = new BitMapImage(image.getWidth(), image.getHeight());
                return finalIMG;
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Error loading image: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        return null;
    }
}
