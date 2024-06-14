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

    public VisualizationPanel(List<Worker> workers) {
        this.workers = workers;
        this.userColors = new ConcurrentHashMap<>();
        this.animationSizes = new ConcurrentHashMap<>();

        // Генерация случайных цветов для пользователей
        for (Worker worker : workers) {
            userColors.putIfAbsent(worker.getUser().getId(), generateRandomColor());
            animationSizes.put(worker, 20); // Инициализация размера
        }

        // Настройка таймера для анимации
        animationTimer = new Timer(1000, e -> {
            for (Worker worker : workers) {
                int currentSize = animationSizes.get(worker);
                animationSizes.put(worker, currentSize == 20 ? 30 : 20);
            }
            repaint();
        });
        animationTimer.start();

        // Добавление обработчика событий для отображения информации о работнике при нажатии
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Worker clickedWorker = getWorkerAtPoint(e.getPoint());
                if (clickedWorker != null) {
                    showWorkerInfo(clickedWorker);
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (Worker worker : workers) {
            drawWorker(g, worker);
        }
    }

    private void drawWorker(Graphics g, Worker worker) {
        Color color = userColors.get(worker.getUser().getId());
        g.setColor(color);
        int x = worker.getCoordinates().getX().intValue();
        int y = worker.getCoordinates().getY().intValue();
        int size = animationSizes.get(worker); // Использование анимированного размера

        // Отрисовка объекта
        g.fillOval(x, y, size, size);
    }

    private Worker getWorkerAtPoint(Point point) {
        for (Worker worker : workers) {
            int x = worker.getCoordinates().getX().intValue();
            int y = worker.getCoordinates().getY().intValue();
            int size = animationSizes.get(worker);
            Rectangle rect = new Rectangle(x, y, size, size);
            if (rect.contains(point)) {
                return worker;
            }
        }
        return null;
    }

    private void showWorkerInfo(Worker worker) {
        JOptionPane.showMessageDialog(this, worker.toString(), "Worker Info", JOptionPane.INFORMATION_MESSAGE);
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
