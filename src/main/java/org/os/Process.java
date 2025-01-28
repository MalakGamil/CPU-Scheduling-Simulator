package org.os;

import java.awt.*;

public class Process {
    private String name; // Process Name
    private Color color; // Color for graphical representation
    private int arrivalTime; // Arrival Time
    private int burstTime; // Burst Time
    private int remainingTime; // Remaining Burst Time
    private int priority; // Priority
    private int waitingTime; // Waiting Time
    private int turnaroundTime; // Turnaround Time
    private int quantum; // Quantum
    private int completionTime; // Completion Time
    private double fcaiFactor; // FCAI Factor for dynamic priority
    private boolean hasExecuted40; // Whether the process has executed 40% of its quantum

    // Constructor with quantum
    public Process(String name, Color color, int arrivalTime, int burstTime, int priority, int quantum) {
        this.name = name;
        this.color = color; // Store color directly as a Color object
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.remainingTime = burstTime;
        this.priority = priority;
        this.waitingTime = 0;
        this.turnaroundTime = 0;
        this.quantum = quantum;
        this.completionTime = 0; // Initialize completion time
        this.fcaiFactor = 0.0; // Initialize FCAI Factor
        this.hasExecuted40 = false; // Initialize execution status
    }

    // Constructor without quantum (default quantum = 0)
    public Process(String name, String colorHex, int arrivalTime, int burstTime, int priority) {
        this(name, Color.decode(colorHex), arrivalTime, burstTime, priority, 0); // Convert color hex to Color
    }

    // Getters and Setters
    public String getName() { return name; }

    // Use the Color object directly
    public Color getColor() { return color; }

    public int getArrivalTime() { return arrivalTime; }
    public int getBurstTime() { return burstTime; }
    public void setBurstTime(int burstTime) {
        this.burstTime = burstTime;  // Allow setting burst time directly
    }
    public int getRemainingTime() { return remainingTime; }
    public void setRemainingTime(int remainingTime) {
        if (remainingTime < 0) throw new IllegalArgumentException("Remaining time cannot be negative");
        this.remainingTime = remainingTime;
    }
    public int getPriority() { return priority; }
    public int getWaitingTime() { return waitingTime; }
    public void setWaitingTime(int waitingTime) { this.waitingTime = waitingTime; }
    public int getTurnaroundTime() { return turnaroundTime; }
    public void setTurnaroundTime(int turnaroundTime) { this.turnaroundTime = turnaroundTime; }
    public int getQuantum() { return quantum; }
    public void setQuantum(int quantum) { this.quantum = quantum; }
    public int getCompletionTime() { return completionTime; }
    public void setCompletionTime(int completionTime) { this.completionTime = completionTime; }

    // FCAI Factor
    public double getFCAIFactor() { return fcaiFactor; }

    public void calculateFCAIFactor(double V1, double V2) {
        this.fcaiFactor = (10 - this.priority) +
                (this.arrivalTime / V1) +
                (this.remainingTime / V2);
    }

    // Has Executed 40% of Quantum
    public boolean hasExecuted40() { return hasExecuted40; }

    public void setHasExecuted40(boolean hasExecuted40) {
        this.hasExecuted40 = hasExecuted40;
    }

    // Helper Methods
    public void incrementWaitingTime(int time) {
        this.waitingTime += time;
    }

    public void decrementRemainingTime(int time) {
        if (time > this.remainingTime) throw new IllegalArgumentException("Decrement exceeds remaining time");
        this.remainingTime -= time;
    }

    @Override
    public String toString() {
        return String.format(
                "Process{name='%s', color='%s', arrivalTime=%d, burstTime=%d, remainingTime=%d, priority=%d, waitingTime=%d, turnaroundTime=%d, quantum=%d, completionTime=%d, fcaiFactor=%.2f}",
                name, color.toString(), arrivalTime, burstTime, remainingTime, priority, waitingTime, turnaroundTime, quantum, completionTime, fcaiFactor
        );
    }
}
