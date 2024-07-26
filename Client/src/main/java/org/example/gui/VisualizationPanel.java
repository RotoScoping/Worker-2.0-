package org.example.gui;

import org.example.model.Worker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class VisualizationPanel extends JPanel {
    private List<Worker> workers;
    private Map<Long, Color> userColors;
    private Map<Worker, Integer> animationSizes;
    private Timer animationTimer;
    private MainPanel mainPanel;

    public VisualizationPanel(List<Worker> workers, MainPanel mainPanel) {
        this.workers = workers;
        this.mainPanel = mainPanel;
        this.userColors = new ConcurrentHashMap<>();
        this.animationSizes = new ConcurrentHashMap<>();


        for (Worker worker : workers) {
            userColors.putIfAbsent(worker.getUser().getId(), generateRandomColor());
            animationSizes.put(worker, 20);
        }


        animationTimer = new Timer(1000, e -> {
            for (Worker worker : workers) {
                int currentSize = animationSizes.get(worker);
                animationSizes.put(worker, currentSize == 20 ? 30 : 20);
            }
            repaint();
        });
        animationTimer.start();


        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Worker clickedWorker = getWorkerAtPoint(e.getPoint());
                if (clickedWorker != null) {
                    showWorkerInfo(clickedWorker);
                }
            }
        });
        setLayout(new FlowLayout());

        JButton addButton = new JButton("+");
        addButton.setToolTipText("Add Worker");
        addButton.addActionListener(e -> mainPanel.showAddDialog());
        add(addButton);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);


        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;


        drawAxes(g, centerX, centerY);

        for (Worker worker : workers) {
            drawWorker(g, worker, centerX, centerY);
        }
    }

    private void drawAxes(Graphics g, int centerX, int centerY) {
        g.setColor(Color.BLACK);
        g.drawLine(0, centerY, getWidth(), centerY);
        g.drawLine(centerX, 0, centerX, getHeight());
    }

    private void drawWorker(Graphics g, Worker worker, int centerX, int centerY) {
        Color color = userColors.get(worker.getUser().getId());
        g.setColor(color);
        int x = centerX + worker.getCoordinates().getX().intValue();
        int y = centerY - worker.getCoordinates().getY().intValue();
        int size = animationSizes.get(worker);

        g.fillOval(x - size / 2, y - size / 2, size, size);
    }

    private Worker getWorkerAtPoint(Point point) {
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;

        for (Worker worker : workers) {
            int x = centerX + worker.getCoordinates().getX().intValue();
            int y = centerY - worker.getCoordinates().getY().intValue();
            int size = animationSizes.get(worker);
            Rectangle rect = new Rectangle(x - size / 2, y - size / 2, size, size);
            if (rect.contains(point)) {
                return worker;
            }
        }
        return null;
    }

    private void showWorkerInfo(Worker worker) {
        JPanel panel = new JPanel(new GridLayout(0, 2));
        panel.add(new JLabel("Name:"));
        panel.add(new JLabel(worker.getName()));
        panel.add(new JLabel("Coordinates:"));
        panel.add(new JLabel(worker.getCoordinates().toString()));
        panel.add(new JLabel("Salary:"));
        panel.add(new JLabel(String.valueOf(worker.getSalary())));
        panel.add(new JLabel("Start Date:"));
        panel.add(new JLabel(worker.getStartDate().toString()));
        panel.add(new JLabel("Position:"));
        panel.add(new JLabel(worker.getPosition().toString()));
        panel.add(new JLabel("Status:"));
        panel.add(new JLabel(worker.getStatus().toString()));
        panel.add(new JLabel("Organization:"));
        panel.add(new JLabel(worker.getOrganization() != null ? worker.getOrganization().getFullName() : ""));

        int result = JOptionPane.showOptionDialog(
                this, panel, "Worker Info",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                null, new Object[]{"Update", "Delete", "Close"}, null);

        if (result == 0) {
            mainPanel.showUpdateDialog(worker);
        } else if (result == 1) {
            mainPanel.deleteWorker(worker);
        }
    }

    public void updateWorkers(List<Worker> newWorkers) {
        this.workers = newWorkers;
        for (Worker worker : newWorkers) {
            userColors.putIfAbsent(worker.getUser().getId(), generateRandomColor());
            animationSizes.putIfAbsent(worker, 20);
        }
        repaint();
    }

    private Color generateRandomColor() {
        return new Color((int) (Math.random() * 0x1000000));
    }
}
