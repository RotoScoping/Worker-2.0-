package org.example.gui;

import org.example.client.Client;
import org.example.gui.event.AuthEvent;
import org.example.gui.event.Event;
import org.example.gui.event.EventType;
import org.example.model.Form;
import org.example.model.Message;
import org.example.model.Worker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Locale;


public class AuthPanel extends JPanel {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private Client clientGUI;
    private boolean isRegisterMode;
    private JLabel modeLabel;
    private JButton loginButton;
    private JButton cancelButton;
    private JLabel switchModeLabel;
    private JLabel titleLabel;

    private BaseWindow mainWindow;

    public AuthPanel(Client clientGUI, BaseWindow main) {
        this.mainWindow = main;
        this.clientGUI = clientGUI;
        this.isRegisterMode = false;
        setBorder(BorderFactory.createLineBorder(new Color(95, 184, 50), 15)); // Add orange border
        setLayout(new GridBagLayout());
        setBackground(new Color(255, 255, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        JPanel containerPanel = new JPanel();
        containerPanel.setLayout(new GridBagLayout());
        containerPanel.setBackground(new Color(255, 255, 255));
        containerPanel.setBorder(BorderFactory.createLineBorder(new Color(95, 184, 50), 5));
        GridBagConstraints containerGbc = new GridBagConstraints();
        JPanel titlePanel = new JPanel();
        titleLabel = new JLabel("<html><h1 style='color:white'>Login Form</h1></html>");
        titlePanel.setBackground(new Color(95, 184, 50));
        titlePanel.add(titleLabel);
        titlePanel.setPreferredSize(new Dimension(400, 50));
        containerGbc.insets = new Insets(0, 0, 0, 0);
        containerGbc.gridx = 0;
        containerGbc.gridy = 0;
        containerGbc.gridwidth = 2;
        containerPanel.add(titlePanel, containerGbc);

        containerGbc.insets = new Insets(10, 10, 10, 10);
        containerGbc.gridx = 0;
        containerGbc.gridy = 1;
        containerGbc.gridwidth = 1;
        containerGbc.anchor = GridBagConstraints.CENTER;
        modeLabel = new JLabel("Username:");
        modeLabel.setForeground(new Color(95, 184, 50));
        containerPanel.add(modeLabel, containerGbc);

        containerGbc.gridx = 1;
        usernameField = new JTextField(20);
        usernameField.setBorder(BorderFactory.createCompoundBorder(
                usernameField.getBorder(),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        containerPanel.add(usernameField, containerGbc);

        containerGbc.gridx = 0;
        containerGbc.gridy = 2;
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setForeground(new Color(95, 184, 50));
        containerPanel.add(passwordLabel, containerGbc);

        containerGbc.gridx = 1;
        passwordField = new JPasswordField(20);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                passwordField.getBorder(),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        containerPanel.add(passwordField, containerGbc);

        containerGbc.gridx = 0;
        containerGbc.gridy = 3;
        containerGbc.gridwidth = 2;
        containerGbc.fill = GridBagConstraints.CENTER;

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(79, 149, 45));
        loginButton = new JButton("Login");
        loginButton.setBackground(new Color(95, 184, 50));
        loginButton.setForeground(Color.WHITE);
        loginButton.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        loginButton.addActionListener(e -> handleAction());
        buttonPanel.add(loginButton);

        cancelButton = new JButton("Cancel");
        cancelButton.setBackground(Color.WHITE);
        cancelButton.setForeground(Color.BLACK);
        cancelButton.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        cancelButton.addActionListener(e -> handleCancel());
        buttonPanel.add(cancelButton);

        containerPanel.add(buttonPanel, containerGbc);
        containerGbc.gridy = 4;

        addChangeModeLink();
        containerPanel.add(switchModeLabel, containerGbc);

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(containerPanel, gbc);
    }


    private void addChangeModeLink() {
        switchModeLabel = new JLabel("<html><a href=''>click here to create a new account</a></html>");
        switchModeLabel.setForeground(Color.CYAN);
        switchModeLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        switchModeLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (isRegisterMode) {
                    switchToLoginMode();
                } else {
                    switchToRegisterMode();
                }
            }
        });


    }
    private void handleAction() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());


        if (isRegisterMode) {
            AuthEvent authEvent = new AuthEvent(EventType.REGISTER, new Form(username, password));
            Message message = clientGUI.sendPacket(authEvent);
            JOptionPane.showMessageDialog(this, message.getMessage(), "Info", JOptionPane.INFORMATION_MESSAGE);
            switchToLoginMode();
        } else {
            AuthEvent authEvent = new AuthEvent(EventType.LOGIN, new Form(username, password));
            Message message = clientGUI.sendPacket(authEvent);
            if (message.getToken() != null) {
                Message workersMessage = clientGUI.sendPacket(new Event(EventType.SHOW)); // Запрос данных после авторизации
                MainPanel mainPanel = mainWindow.switchToMainPanel(workersMessage.getWorkers());
                mainPanel.setCurrentUsername(username);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    private void handleCancel() {
        usernameField.setText("");
        passwordField.setText("");
    }

    private void switchToRegisterMode() {
        isRegisterMode = true;
        titleLabel.setText("<html><h1 style='color:white'>Register Form</h1></html>");
        modeLabel.setText("Username:");
        loginButton.setText("Register");
        switchModeLabel.setText("<html><a href=''>click here to login</a></html>");
    }

    private void switchToLoginMode() {
        isRegisterMode = false;
        titleLabel.setText("<html><h1 style='color:white'>Login Form</h1></html>");
        modeLabel.setText("Username:");
        loginButton.setText("Login");
        switchModeLabel.setText("<html><a href=''>click here to create a new account</a></html>");
    }


}