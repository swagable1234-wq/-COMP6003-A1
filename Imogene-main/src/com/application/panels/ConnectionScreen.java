package com.application.panels;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ConnectionScreen extends JPanel {

    // Backend link
    private String remote = "http://localhost:8080";

    // Singleton pattern
    private static final ConnectionScreen instance = new ConnectionScreen();

    public static ConnectionScreen getInstance() {
        return instance;
    }

    public ConnectionScreen() {
        super(new GridBagLayout());
        JTextField txtRemote = new JTextField(remote);
        JButton btnRemoteStart = new JButton("Run");
        btnRemoteStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                remote = txtRemote.getText();
                LeftSidebar.getInstance().setRemote(true);
                //RightSidebar.getInstance().setRemote(true);
                ApplicationWindow.layout.show(ApplicationWindow.cards, "Image");
            }
        });

        Dimension buttonSize = new Dimension(300, 80);
        txtRemote.setPreferredSize(buttonSize);
        btnRemoteStart.setPreferredSize(buttonSize);

        int spacing = 20;

        // Copy-pasted from mode screen TODO: clean up
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 0, 5, spacing/2);
        gbc.gridx = 0;
        add(txtRemote, gbc);

        gbc.insets = new Insets(5, spacing/2, 5, 0);
        gbc.gridx = 1;
        add(btnRemoteStart, gbc);
    }

    public String getRemote() {
        return remote;
    }

}
