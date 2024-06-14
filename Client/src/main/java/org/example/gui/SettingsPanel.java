package org.example.gui;

import org.example.client.Payload;

import javax.swing.*;
import java.awt.*;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public class SettingsPanel extends JPanel {
    private Locale currentLocale;
    private ResourceBundle bundle;
    private final MainPanel mainPanel;
    private JLabel currentUserLabel;
    private JComboBox<String> languageComboBox;
    private String currentUsername;

    public SettingsPanel(MainPanel mainPanel) {
        this.mainPanel = mainPanel;
        this.currentLocale = Locale.ENGLISH;
        this.bundle = ResourceBundle.getBundle("messages", currentLocale);
        settingsInit();
    }

    private void settingsInit() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create and add the current user label
        currentUserLabel = new JLabel();
        currentUserLabel.setAlignmentX(Component.LEFT_ALIGNMENT); // Align to the left
        add(currentUserLabel);

        // Добавляем отступ между компонентами
        add(Box.createVerticalStrut(10));

        // Create and add the language panel
        JPanel languagePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel languageLabel = new JLabel(bundle.getString("chooseLanguage"));
        languagePanel.add(languageLabel);

        String[] languages = {"English", "Русский", "Türkçe", "Català", "Español (Panamá)"};
        languageComboBox = new JComboBox<>(languages);
        languagePanel.add(languageComboBox);

        Map<String, Locale> localeMap = Map.of(
                "Русский", new Locale("ru", "RU"),
                "Türkçe", new Locale("tr", "TR"),
                "Català", new Locale("ca", "ES"),
                "Español (Panamá)", new Locale("es", "PA"),
                "English", Locale.ENGLISH
        );

        languageComboBox.addActionListener(e -> {
            String selectedLanguage = (String) languageComboBox.getSelectedItem();
            Locale selectedLocale = localeMap.getOrDefault(selectedLanguage, Locale.ENGLISH);
            mainPanel.setLocale(selectedLocale);
            updateLocale(selectedLocale);
        });

        languagePanel.setAlignmentX(Component.LEFT_ALIGNMENT); // Align to the left
        add(languagePanel);
        setCurrentUsername(currentUsername);
    }

    public void setCurrentUsername(String username) {
        this.currentUsername = username;
        if (currentUserLabel != null) {
            currentUserLabel.setText(bundle.getString("currentUser") + ": " + Payload.getLogin());
        }
    }

    public void setLocale(Locale locale) {
        this.currentLocale = locale;
        this.bundle = ResourceBundle.getBundle("messages", currentLocale);
        updateText();
    }

    private void updateLocale(Locale locale) {
        this.currentLocale = locale;
        this.bundle = ResourceBundle.getBundle("messages", currentLocale);
        updateText();
    }


    private void updateText() {
        // Обновляем текст для всех компонентов на основе новой локали
        removeAll();
        settingsInit();
        if (currentUsername != null) {
            setCurrentUsername(currentUsername);
        }
        revalidate();
        repaint();
    }
}



