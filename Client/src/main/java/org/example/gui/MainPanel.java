package org.example.gui;

import org.example.client.Client;
import org.example.gui.event.AddEvent;
import org.example.gui.event.EventType;
import org.example.model.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import java.util.stream.Collectors;



public class MainPanel extends JPanel {
    private JTable dataTable;
    private Client clientGUI;
    private List<Worker> workers;
    private DefaultTableModel tableModel;
    private ResourceBundle bundle;
    private Locale currentLocale;
    private VisualizationPanel visualizationPanel;

    private SettingsPanel settingsPanel;

    private String currentUsername;


    public MainPanel(Client clientGUI, List<Worker> workers) {
        this.clientGUI = clientGUI;
        this.workers = workers;
        this.currentLocale = Locale.ENGLISH;
        this.bundle = ResourceBundle.getBundle("messages", currentLocale);

        setLayout(new BorderLayout());

        // Create tabbed pane
        JTabbedPane tabbedPane = new JTabbedPane();

        // Add tabs
        tabbedPane.addTab(bundle.getString("dataTable"), createDataTablePanel());
        tabbedPane.addTab(bundle.getString("visualization"), createVisualizationPanel());
        tabbedPane.addTab(bundle.getString("settings"), createSettingsPanel());

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createVisualizationPanel() {
        visualizationPanel = new VisualizationPanel(workers);
        return visualizationPanel;
    }

    private JPanel createDataTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Define column names
        String[] columnNames = {
                bundle.getString("id"),
                bundle.getString("name"),
                bundle.getString("coordinates"),
                bundle.getString("creationDate"),
                bundle.getString("salary"),
                bundle.getString("startDate"),
                bundle.getString("position"),
                bundle.getString("status"),
                bundle.getString("organization")
        };

        // Create table model and table
        tableModel = new DefaultTableModel(columnNames, 0);
        dataTable = new JTable(tableModel);
        if (clientGUI.getToken() != null) {
            loadTableData(workers);
        }

        JScrollPane scrollPane = new JScrollPane(dataTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Add filtering controls
        JPanel controlsPanel = new JPanel(new FlowLayout());
        JComboBox<String> columnComboBox = new JComboBox<>(columnNames);
        JTextField filterField = new JTextField(20);
        JButton filterButton = new JButton(bundle.getString("filter"));

        JButton addButton = new JButton(bundle.getString("add"));
        JButton updateButton = new JButton(bundle.getString("update"));
        JButton deleteButton = new JButton(bundle.getString("delete"));

        filterButton.addActionListener(e -> applyFilter(columnComboBox.getSelectedItem().toString(), filterField.getText()));

        addButton.addActionListener(e -> showAddDialog());
        updateButton.addActionListener(e -> showUpdateDialog());
        deleteButton.addActionListener(e -> deleteSelectedRow());

        controlsPanel.add(new JLabel(bundle.getString("filterBy")));
        controlsPanel.add(columnComboBox);
        controlsPanel.add(filterField);
        controlsPanel.add(filterButton);

        controlsPanel.add(addButton);
        controlsPanel.add(updateButton);
        controlsPanel.add(deleteButton);

        panel.add(controlsPanel, BorderLayout.NORTH);

        return panel;
    }

    private void showAddDialog() {
        Worker worker = showWorkerDialog(null);
        if (worker != null) {
            workers.add(worker);
            loadTableData(workers);
            visualizationPanel.updateWorkers(workers);
            // Отправить worker на сервер для добавления
        }
    }

    private void showUpdateDialog() {
        int selectedRow = dataTable.getSelectedRow();
        if (selectedRow != -1) {
            Worker worker = workers.get(selectedRow);
            Worker updatedWorker = showWorkerDialog(worker);
            if (updatedWorker != null) {
                workers.set(selectedRow, updatedWorker);
                loadTableData(workers);
                visualizationPanel.updateWorkers(workers);
                // Отправить updatedWorker на сервер для обновления
            }
        } else {
            JOptionPane.showMessageDialog(this, bundle.getString("selectRowToUpdate"), bundle.getString("error"), JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelectedRow() {
        int selectedRow = dataTable.getSelectedRow();
        if (selectedRow != -1) {
            Worker worker = workers.remove(selectedRow);
            loadTableData(workers);
            visualizationPanel.updateWorkers(workers);
            // Отправить worker на сервер для удаления
        } else {
            JOptionPane.showMessageDialog(this, bundle.getString("selectRowToDelete"), bundle.getString("error"), JOptionPane.ERROR_MESSAGE);
        }
    }


    private JPanel createSettingsPanel() {
        settingsPanel = new SettingsPanel(this);
        return settingsPanel;
    }

    public void setLocale(Locale locale) {
        this.currentLocale = locale;
        this.bundle = ResourceBundle.getBundle("messages", currentLocale);
        updateText();
    }

    private void updateText() {
        // Update the text for all components based on the new locale
        // For simplicity, we're just resetting the main panel
        removeAll();
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab(bundle.getString("dataTable"), createDataTablePanel());
        tabbedPane.addTab(bundle.getString("visualization"), createVisualizationPanel());
        settingsPanel.setCurrentUsername(currentUsername);
        tabbedPane.addTab(bundle.getString("settings"), createSettingsPanel());
        add(tabbedPane, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private void loadTableData(List<Worker> workers) {
        tableModel.setRowCount(0); // Clear existing data
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", currentLocale);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", currentLocale);
        NumberFormat numberFormat = NumberFormat.getInstance(currentLocale);
        for (Worker worker : workers) {
            tableModel.addRow(new Object[]{
                    worker.getId(),
                    worker.getName(),
                    worker.getCoordinates(),
                    formatLocalDate(worker.getCreationDate(), dateFormatter),
                    numberFormat.format(worker.getSalary()),
                    formatDate(worker.getStartDate(), dateFormat),
                    worker.getPosition(),
                    worker.getStatus(),
                    worker.getOrganization()
            });
        }
    }
    private String formatDate(Date date, SimpleDateFormat dateFormat) {
        return date != null ? dateFormat.format(date) : "";
    }

    private String formatLocalDate(LocalDate date, DateTimeFormatter dateFormatter) {
        return date != null ? dateFormatter.format(date) : "";
    }

    private void applyFilter(String column, String filterText) {
        Predicate<Worker> predicate = switch (column) {
            case "ID" -> worker -> String.valueOf(worker.getId())
                    .contains(filterText);
            case "Name" -> worker -> worker.getName()
                    .toLowerCase()
                    .contains(filterText.toLowerCase());
            case "Coordinates" -> worker -> worker.getCoordinates()
                    .toString()
                    .toLowerCase()
                    .contains(filterText.toLowerCase());
            case "Creation Date" -> worker -> worker.getCreationDate()
                    .toString()
                    .contains(filterText);
            case "Salary" -> worker -> String.valueOf(worker.getSalary())
                    .contains(filterText);
            case "Start Date" -> worker -> worker.getStartDate()
                    .toString()
                    .contains(filterText);
            case "Position" -> worker -> worker.getPosition()
                    .toString()
                    .toLowerCase()
                    .contains(filterText.toLowerCase());
            case "Status" -> worker -> worker.getStatus()
                    .toString()
                    .toLowerCase()
                    .contains(filterText.toLowerCase());
            case "Organization" -> worker -> worker.getOrganization()
                    .toString()
                    .toLowerCase()
                    .contains(filterText.toLowerCase());
            default -> worker -> true;
        };
        List<Worker> filteredWorkers = workers.stream()
                .filter(predicate)
                .collect(Collectors.toList());
        loadTableData(filteredWorkers);
    }


    private Worker showWorkerDialog(Worker worker)  {
        JTextField idField = new JTextField(5);
        JTextField nameField = new JTextField(20);
        JTextField xField = new JTextField(5);
        JTextField yField = new JTextField(5);
        JTextField creationDateField = new JTextField(10);
        JTextField salaryField = new JTextField(10);
        JTextField startDateField = new JTextField(10);
        JComboBox<Position> positionComboBox = new JComboBox<>(Position.values());
        JComboBox<Status> statusComboBox = new JComboBox<>(Status.values());
        JTextField organizationField = new JTextField(20);

        if (worker != null) {
            idField.setText(String.valueOf(worker.getId()));
            nameField.setText(worker.getName());
            xField.setText(String.valueOf(worker.getCoordinates().getX()));
            yField.setText(String.valueOf(worker.getCoordinates().getY()));
            creationDateField.setText(worker.getCreationDate().toString());
            salaryField.setText(String.valueOf(worker.getSalary()));
            startDateField.setText(new SimpleDateFormat("yyyy-MM-dd").format(worker.getStartDate()));
            positionComboBox.setSelectedItem(worker.getPosition());
            statusComboBox.setSelectedItem(worker.getStatus());
            organizationField.setText(worker.getOrganization() != null ? worker.getOrganization().getFullName() : "");
            idField.setEditable(false);
        }

        JPanel panel = new JPanel(new GridLayout(0, 2));
        panel.add(new JLabel(bundle.getString("id")));
        panel.add(idField);
        panel.add(new JLabel(bundle.getString("name")));
        panel.add(nameField);
        panel.add(new JLabel(bundle.getString("coordinates") + " X"));
        panel.add(xField);
        panel.add(new JLabel(bundle.getString("coordinates") + " Y"));
        panel.add(yField);
        panel.add(new JLabel(bundle.getString("creationDate")));
        panel.add(creationDateField);
        panel.add(new JLabel(bundle.getString("salary")));
        panel.add(salaryField);
        panel.add(new JLabel(bundle.getString("startDate")));
        panel.add(startDateField);
        panel.add(new JLabel(bundle.getString("position")));
        panel.add(positionComboBox);
        panel.add(new JLabel(bundle.getString("status")));
        panel.add(statusComboBox);
        panel.add(new JLabel(bundle.getString("organization")));
        panel.add(organizationField);

        int result = JOptionPane.showConfirmDialog(null, panel,
                bundle.getString("workerDetails"), JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            Coordinates coordinates = Coordinates.builder()
                    .x(Long.parseLong(xField.getText()))
                    .y(Double.parseDouble(yField.getText()))
                    .build();
            Organization organization = Organization.builder()
                    .fullName(organizationField.getText())
                    .build();
            try {
                return Worker.builder()
                        .id(Integer.parseInt(idField.getText()))
                        .name(nameField.getText())
                        .coordinates(coordinates)
                        .creationDate(LocalDate.parse(creationDateField.getText()))
                        .salary(Float.parseFloat(salaryField.getText()))
                        .startDate(new SimpleDateFormat("yyyy-MM-dd").parse(startDateField.getText()))
                        .position((Position) positionComboBox.getSelectedItem())
                        .status((Status) statusComboBox.getSelectedItem())
                        .organization(organization)
                        .build();
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    public void setCurrentUsername(String username) {
        this.currentUsername = username;
        if (settingsPanel != null) {
            settingsPanel.setCurrentUsername(username);
        }
    }
}