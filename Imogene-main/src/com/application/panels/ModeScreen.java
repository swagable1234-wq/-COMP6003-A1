package com.application.panels;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ModeScreen extends JPanel {

    // Singleton pattern
    private static final ModeScreen instance = new ModeScreen();

    public static ModeScreen getInstance() {
        return instance;
    }

    private ModeScreen() {
        super(new GridBagLayout());
        JButton btnLocal = new JButton("Run locally");
        JButton btnRemote = new JButton("Run remotely");
        btnLocal.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ApplicationWindow.layout.show(ApplicationWindow.cards, "Image");
            }
        });

        btnRemote.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ApplicationWindow.layout.show(ApplicationWindow.cards, "Connection");
            }
        });

        Dimension buttonSize = new Dimension(300, 80);
        btnLocal.setPreferredSize(buttonSize);
        btnRemote.setPreferredSize(buttonSize);

        int buttonSpacing = 20;

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 0, 5, buttonSpacing/2);
        gbc.gridx = 0;
        add(btnLocal, gbc);

        gbc.insets = new Insets(5, buttonSpacing/2, 5, 0);
        gbc.gridx = 1;
        add(btnRemote, gbc);
    }

}
