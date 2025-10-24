package com.application;

import com.utils.BitMapImage;
import com.utils.ImageRW;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

public class SaveLoad {

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


            String fileName = fileToSave.getAbsolutePath();
            if (!fileName.contains(".")) {
                fileName += ".png";
                fileToSave = new File(fileName);
            }

            try {
                String format = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();

                ImageRW.writeImage(image, format, fileToSave.getAbsolutePath());

                JOptionPane.showMessageDialog(null, "Image saved successfully!");
                return true;
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Error saving image: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        return false;
    }


    public static BitMapImage loadImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Open Image");

        int userSelection = fileChooser.showOpenDialog(null);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToOpen = fileChooser.getSelectedFile();
            try {
                BitMapImage image = ImageRW.readImage(fileToOpen.getAbsolutePath());
                JOptionPane.showMessageDialog(null, "Image loaded successfully!");
                return image;
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Error loading image: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        return null;
    }
}
