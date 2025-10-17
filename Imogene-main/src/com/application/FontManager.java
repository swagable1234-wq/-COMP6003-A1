package com.application;

import javax.swing.*;
import java.awt.*;

/**
 * Unfinished font manager
 */
public class FontManager {

    private static final int[] fontSizes = new int[] {10, 12, 14, 16, 18, 20, 24, 28, 32};
    private static int currentFontSizeIndex = 2;

    public static void configureFonts() {
        int fontSize = fontSizes[currentFontSizeIndex];

        // Buttons are 16pt Bold Arial
        Font globalButtonFont = new Font("Arial", Font.PLAIN, fontSize);
        UIManager.put("Button.font", globalButtonFont);

        Font globalTextAreaFont = new Font("Arial", Font.PLAIN, fontSize);
        UIManager.put("TextField.font", globalTextAreaFont);

        Font globalLabelFont = new Font("Arial", Font.BOLD, fontSize);
        UIManager.put("Label.font", globalLabelFont);

        Font globalComboBoxFont = new Font("Arial", Font.PLAIN, fontSize);
        UIManager.put("ComboBox.font", globalComboBoxFont);
    }

}
