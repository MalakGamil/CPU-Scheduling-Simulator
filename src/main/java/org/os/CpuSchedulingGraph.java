package org.os;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

public class CpuSchedulingGraph extends JPanel {

    private final List<Process> scheduledProcesses;
    private final int totalTime;
    private final String scheduleName;  // Name of the scheduling algorithm
    private final double averageWaitingTime;  // Average waiting time of the processes
    private final double averageTurnaroundTime;  // Average turnaround time of the processes

    public CpuSchedulingGraph(List<Process> scheduledProcesses, int totalTime, String scheduleName,
                              double averageWaitingTime, double averageTurnaroundTime) {
        this.scheduledProcesses = scheduledProcesses;
        this.totalTime = totalTime;
        this.scheduleName = scheduleName;
        this.averageWaitingTime = averageWaitingTime;
        this.averageTurnaroundTime = averageTurnaroundTime;
        setBackground(Color.DARK_GRAY);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int xOffset = 100;
        int yOffset = 80;
        int barHeight = 30;
        int barSpacing = 40;
        int timeUnitWidth = 40;

        // Draw the title at the top of the chart
        g2d.setColor(Color.RED);
        g2d.setFont(new Font("Arial", Font.BOLD, 30));
        g2d.drawString("CPU Scheduling Graph (" + scheduleName + ")", xOffset, 40);

        // Calculate the maximum time unit needed to draw the chart
        int maxTime = totalTime;

        // Extract unique process names for creating the rows
        Set<String> uniqueProcesses = new HashSet<>();
        for (Process process : scheduledProcesses) {
            uniqueProcesses.add(process.getName());
        }

        // Adjust the panel size based on the number of processes and execution time
        int panelWidth = xOffset + (maxTime + 1) * timeUnitWidth + 100;
        int panelHeight = yOffset + uniqueProcesses.size() * barSpacing + 200;
        setPreferredSize(new Dimension(panelWidth, panelHeight));

        // Draw gridlines and time labels for the x-axis
        g2d.setColor(new Color(100, 100, 100));
        g2d.setFont(new Font("Arial", Font.BOLD, 16));

        // Draw vertical gridlines for each time unit
        for (int i = 0; i <= maxTime; i++) {
            int xPosition = xOffset + i * timeUnitWidth;

            // Draw the gridline
            g2d.drawLine(xPosition, yOffset - 20, xPosition, yOffset + uniqueProcesses.size() * barSpacing + 20);

            // Draw the time labels (e.g., 0, 1, 2, etc.) below the gridline
            g2d.setColor(Color.WHITE);
            g2d.drawString(String.valueOf(i), xPosition - 5, yOffset + uniqueProcesses.size() * barSpacing + 30);
        }

        // Draw the process bars and corresponding labels
        int processIndex = 0;
        for (String processName : uniqueProcesses) {
            for (Process process : scheduledProcesses) {
                if (process.getName().equals(processName)) {
                    int barStartX = xOffset + process.getArrivalTime() * timeUnitWidth;
                    int yPosition = yOffset + processIndex * barSpacing;

                    // Draw a rounded process bar with the color specific to the process
                    g2d.setColor(process.getColor());
                    g2d.fill(new RoundRectangle2D.Double(barStartX, yPosition, process.getBurstTime() * timeUnitWidth,
                            barHeight, 10, 10));

                    // Draw the process name inside the bar
                    g2d.setColor(Color.BLACK);
                    g2d.drawString(process.getName(), barStartX + 5, yPosition + barHeight / 2 + 5);
                }
            }

            // Draw the process name label to the left of the bar
            g2d.setColor(Color.WHITE);
            g2d.drawString("Process: " + processName, xOffset - 90, yOffset + processIndex * barSpacing + barHeight / 2 + 5);

            processIndex++;
        }

        // Draw statistics below the chart
        g2d.setColor(Color.RED);
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.drawString("Average Waiting Time: " + String.format("%.2f", averageWaitingTime), xOffset, yOffset + uniqueProcesses.size() * barSpacing + 60);
        g2d.drawString("Average Turnaround Time: " + String.format("%.2f", averageTurnaroundTime), xOffset, yOffset + uniqueProcesses.size() * barSpacing + 90);
    }

    public static void showGraph(List<Process> processes, int totalTime, String scheduleName,
                                 double averageWaitingTime, double averageTurnaroundTime) {
        JFrame frame = new JFrame("CPU Scheduling Graph");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        CpuSchedulingGraph graph = new CpuSchedulingGraph(processes, totalTime, scheduleName, averageWaitingTime,
                averageTurnaroundTime);
        JScrollPane scrollPane = new JScrollPane(graph);
        scrollPane.setPreferredSize(new Dimension(1200, 800));

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.DARK_GRAY);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel legendPanel = createLegendPanel(processes);
        mainPanel.add(legendPanel, BorderLayout.EAST);

        frame.add(mainPanel);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setVisible(true);
    }

    private static JPanel createLegendPanel(List<Process> processes) {
        JPanel legendPanel = new JPanel(new BorderLayout());
        legendPanel.setBackground(Color.DARK_GRAY);
        legendPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 0, 20));

        JLabel titleLabel = new JLabel("Processes Information");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.RED);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        legendPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel processListPanel = new JPanel();
        processListPanel.setBackground(Color.DARK_GRAY);
        processListPanel.setLayout(new BoxLayout(processListPanel, BoxLayout.Y_AXIS));

        Set<String> uniqueProcesses = new HashSet<>();
        for (Process process : processes) {
            if (uniqueProcesses.add(process.getName())) {
                JPanel processInfoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
                processInfoPanel.setBackground(Color.DARK_GRAY);

                JLabel colorBox = new JLabel();
                colorBox.setOpaque(true);
                colorBox.setBackground(process.getColor());
                colorBox.setPreferredSize(new Dimension(20, 20));

                JLabel processLabel = new JLabel(String.format(" Name: %s | Priority: %d",
                        process.getName(), process.getPriority()));
                processLabel.setFont(new Font("Arial", Font.BOLD, 16));
                processLabel.setForeground(Color.WHITE);

                processInfoPanel.add(colorBox);
                processInfoPanel.add(processLabel);
                processListPanel.add(processInfoPanel);
            }
        }

        legendPanel.add(new JScrollPane(processListPanel), BorderLayout.CENTER);
        return legendPanel;
    }
}
