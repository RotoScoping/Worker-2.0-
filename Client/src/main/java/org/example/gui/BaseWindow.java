package org.example.gui;

import org.example.client.Client;
import org.example.model.Worker;

import javax.swing.*;
import java.awt.*;
import java.util.Collections;
import java.util.List;

public class BaseWindow extends JFrame {
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private AuthPanel authPanel;
    private MainPanel mainPanelContent;

    private Client client;


    public BaseWindow(Client client) {
        this.client = client;
        setup();
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        authPanel = new AuthPanel(client , this);
        mainPanel.add(authPanel, "Auth");
        mainPanelContent = new MainPanel(client, Collections.emptyList());
        mainPanel.add(mainPanelContent, "Main");
        add(mainPanel);
        cardLayout.show(mainPanel, "Auth");
    }


    private void setup() {
        setTitle("Worker");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
    }

    public MainPanel switchToMainPanel(List<Worker> workers) {
        mainPanelContent = new MainPanel(client, workers);
        getContentPane().removeAll();
        getContentPane().add(mainPanelContent);
        revalidate();
        repaint();
        return mainPanelContent;
    }

}

