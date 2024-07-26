package org.example.gui;

import org.example.client.Client;
import org.example.client.Payload;
import org.example.gui.event.AddEvent;
import org.example.gui.event.Event;
import org.example.gui.event.EventType;
import org.example.gui.event.RemoveByIdEvent;
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
import java.util.*;
import java.util.List;
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


        JTabbedPane tabbedPane = new JTabbedPane();


        tabbedPane.addTab(bundle.getString("dataTable"), createDataTablePanel());
        tabbedPane.addTab(bundle.getString("visualization"), createVisualizationPanel());
        tabbedPane.addTab(bundle.getString("settings"), createSettingsPanel());

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createVisualizationPanel() {
        visualizationPanel = new VisualizationPanel(workers, this);
        return visualizationPanel;
    }

    private JPanel createDataTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());


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

        JButton sortButton = new JButton(bundle.getString("sort"));
        JButton addButton = new JButton(bundle.getString("add"));
        JButton updateButton = new JButton(bundle.getString("update"));
        JButton deleteButton = new JButton(bundle.getString("delete"));
        JButton refreshButton = new JButton(bundle.getString("refresh"));
        sortButton.addActionListener(e -> sortData(columnComboBox.getSelectedItem().toString()));

        addButton.addActionListener(e -> showAddDialog());
        updateButton.addActionListener(e -> showUpdateDialog());
        deleteButton.addActionListener(e -> deleteSelectedRow());
        refreshButton.addActionListener(e -> refreshData());
        controlsPanel.add(new JLabel(bundle.getString("filterBy")));
        controlsPanel.add(columnComboBox);
        controlsPanel.add(filterField);
        controlsPanel.add(sortButton);
        controlsPanel.add(refreshButton);
        controlsPanel.add(addButton);
        controlsPanel.add(updateButton);
        controlsPanel.add(deleteButton);

        panel.add(controlsPanel, BorderLayout.NORTH);

        return panel;
    }

    void showAddDialog() {
        Worker worker = showWorkerDialog(null);
        if (worker != null) {
            Message message = clientGUI.sendPacket(new AddEvent(EventType.ADD, worker));
            if (message.getMessage().equals("Данные записаны!")) {
                workers = getNewData();
                loadTableData(workers);
                visualizationPanel.updateWorkers(workers);
            }
        }
    }

    private void refreshData() {
        workers = getNewData();
        loadTableData(workers);
        visualizationPanel.updateWorkers(workers);
    }

    private void showUpdateDialog() {
        int selectedRow = dataTable.getSelectedRow();
        if (selectedRow != -1) {
            Worker selectedWorker = workers.get(selectedRow);
            Worker updatedWorker = showWorkerDialog(selectedWorker);
            updatedWorker.setId(selectedWorker.getId());
            if (updatedWorker != null) {
                if (selectedWorker.getUser()
                        .getUsername()
                        .equals(Payload.getLogin())) {
                    clientGUI.sendPacket(new AddEvent(EventType.UPDATE_ID, updatedWorker));
                    refreshData();
                } else {
                    JOptionPane.showMessageDialog(this, bundle.getString("selectYourWorker"), bundle.getString("error"), JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, bundle.getString("selectRowToUpdate"), bundle.getString("error"), JOptionPane.ERROR_MESSAGE);
        }
    }

    public void showUpdateDialog(Worker selectedWorker) {
        Worker updatedWorker = showWorkerDialog(selectedWorker);
        updatedWorker.setId(selectedWorker.getId());
        if (updatedWorker != null) {
            if (selectedWorker.getUser()
                    .getUsername()
                    .equals(Payload.getLogin())) {
                clientGUI.sendPacket(new AddEvent(EventType.UPDATE_ID, updatedWorker));
                refreshData();
            } else {
                JOptionPane.showMessageDialog(this, bundle.getString("selectYourWorker"), bundle.getString("error"), JOptionPane.ERROR_MESSAGE);

            }
        }
    }

    private void deleteSelectedRow() {
        int selectedRow = dataTable.getSelectedRow();
        if (selectedRow != -1) {
            Worker selectedWorker = workers.get(selectedRow);
            if (selectedWorker.getUser().getUsername().equals(Payload.getLogin())) {
                Message message = clientGUI.sendPacket(new RemoveByIdEvent(EventType.REMOVE_BY_ID, selectedWorker.getId()));
                if (message.getMessage().equals("Пользователь был удален!")) {
                    workers.remove(selectedRow);
                }
                workers = getNewData();
                loadTableData(workers);
                visualizationPanel.updateWorkers(workers);

            } else {
                JOptionPane.showMessageDialog(this, bundle.getString("selectYourWorker"), bundle.getString("error"), JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, bundle.getString("selectRowToDelete"), bundle.getString("error"), JOptionPane.ERROR_MESSAGE);
        }
    }


    public void deleteWorker(Worker worker) {
        if (worker.getUser().getUsername().equals(Payload.getLogin())) {
            Message message = clientGUI.sendPacket(new RemoveByIdEvent(EventType.REMOVE_BY_ID, worker.getId()));
            if (message.getMessage().equals("Пользователь был удален!")) {
                workers.removeIf(w -> w.getId() == worker.getId());
                workers = getNewData();
                loadTableData(workers);
                visualizationPanel.updateWorkers(workers);
            }
        } else {
            JOptionPane.showMessageDialog(this, bundle.getString("selectYourWorker"), bundle.getString("error"), JOptionPane.ERROR_MESSAGE);
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

    public List<Worker> getNewData() {
        return clientGUI.sendPacket(new Event(EventType.SHOW)).getWorkers();
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

    private void sortData(String column) {
        Comparator<Worker> comparator;
        switch (column) {
            case "ID":
                comparator = Comparator.comparingInt(Worker::getId);
                break;
            case "Name":
                comparator = Comparator.comparing(Worker::getName, String.CASE_INSENSITIVE_ORDER);
                break;
            case "Coordinates":
                comparator = Comparator.comparing(worker -> worker.getCoordinates().toString(), String.CASE_INSENSITIVE_ORDER);
                break;
            case "Creation Date":
                comparator = Comparator.comparing(Worker::getCreationDate);
                break;
            case "Salary":
                comparator = Comparator.comparingDouble(Worker::getSalary);
                break;
            case "Start Date":
                comparator = Comparator.comparing(Worker::getStartDate);
                break;
            case "Position":
                comparator = Comparator.comparing(worker -> worker.getPosition().toString(), String.CASE_INSENSITIVE_ORDER);
                break;
            case "Status":
                comparator = Comparator.comparing(worker -> worker.getStatus().toString(), String.CASE_INSENSITIVE_ORDER);
                break;
            case "Organization":
                comparator = Comparator.comparing(worker -> worker.getOrganization().toString(), String.CASE_INSENSITIVE_ORDER);
                break;
            default:
                return;
        }
        workers = workers.stream()
                .sorted(comparator)
                .collect(Collectors.toList());
        loadTableData(workers);
    }


    private Worker showWorkerDialog(Worker worker) {
        JTextField nameField = new JTextField(20);
        JTextField xField = new JTextField(5);
        JTextField yField = new JTextField(5);
        JTextField salaryField = new JTextField(10);
        JTextField startDateField = new JTextField(10);
        JComboBox<Position> positionComboBox = new JComboBox<>(Position.values());
        JComboBox<Status> statusComboBox = new JComboBox<>(Status.values());
        JTextField organizationField = new JTextField(20);

        if (worker != null) {
            nameField.setText(worker.getName());
            if (worker.getCoordinates() != null) {
                xField.setText(String.valueOf(worker.getCoordinates().getX()));
                yField.setText(String.valueOf(worker.getCoordinates().getY()));
            }
            salaryField.setText(String.valueOf(worker.getSalary()));
            startDateField.setText(new SimpleDateFormat("dd.MM.yyyy").format(worker.getStartDate()));
            positionComboBox.setSelectedItem(worker.getPosition());
            statusComboBox.setSelectedItem(worker.getStatus());
            organizationField.setText(worker.getOrganization() != null ? worker.getOrganization().getFullName() : "");
        }

        JPanel panel = new JPanel(new GridLayout(0, 2));
        panel.add(new JLabel(bundle.getString("name")));
        panel.add(nameField);
        panel.add(new JLabel(bundle.getString("coordinates") + " X"));
        panel.add(xField);
        panel.add(new JLabel(bundle.getString("coordinates") + " Y"));
        panel.add(yField);
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
            try {
                String name = nameField.getText();
                if (name == null || name.trim().isEmpty()) {
                    throw new IllegalArgumentException(bundle.getString("nameError"));
                }
                Long x = Long.parseLong(xField.getText());
                Double y = Double.parseDouble(yField.getText());
                Coordinates coordinates = Coordinates.builder()
                        .x(x)
                        .y(y)
                        .build();

                float salary = Float.parseFloat(salaryField.getText());
                if (salary <= 0) {
                    throw new IllegalArgumentException(bundle.getString("salaryError"));
                }

                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                LocalDate startDate = LocalDate.parse(startDateField.getText(), dateFormatter);

                Position position = (Position) positionComboBox.getSelectedItem();
                Status status = (Status) statusComboBox.getSelectedItem();
                String orgName = organizationField.getText();
                Organization organization = orgName.isEmpty() ? null : Organization.builder().fullName(orgName).build();

                return Worker.builder()
                        .name(name)
                        .coordinates(coordinates)
                        .salary(salary)
                        .startDate(java.sql.Date.valueOf(startDate))
                        .position(position)
                        .status(status)
                        .organization(organization)
                        .build();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, bundle.getString("numberError"), bundle.getString("error"), JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), bundle.getString("error"), JOptionPane.ERROR_MESSAGE);
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