package org.os;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;

public class PriorityScheduler implements Scheduler {
    @Override
    public void schedule(List<Process> processes, int contextSwitchTime) {
        // Sort processes by priority (lower priority number means higher priority)
        Collections.sort(processes, Comparator.comparingInt(Process::getPriority));

        int currentTime = 0;
        int processCount = processes.size();
        List<String> ganttChart = new ArrayList<>();

        for (Process process : processes) {
            // If the process arrives after the current time, we have to "wait" for it
            if (process.getArrivalTime() > currentTime) {
                currentTime = process.getArrivalTime();
            }

            // Start the process at the current time
            process.setWaitingTime(currentTime - process.getArrivalTime()); // Waiting time = Start time - Arrival time
            currentTime += process.getBurstTime();  // Add burst time to current time
            process.setTurnaroundTime(currentTime - process.getArrivalTime());// Turnaround time = Completion time - Arrival time
            currentTime += contextSwitchTime;
            ganttChart.add(process.getName());  // Add process name to Gantt chart
        }

        // Print statistics after scheduling
        printStatistics(processes);

        // Call the graph to visualize the CPU scheduling
        double totalWaitingTime = 0;
        double totalTurnaroundTime = 0;
        for (Process p : processes) {
            totalWaitingTime += p.getWaitingTime();
            totalTurnaroundTime += p.getTurnaroundTime();
        }

        // Show the graph with the scheduling information
        CpuSchedulingGraph.showGraph(
                processes,                // List of processes
                currentTime,              // Total time of the scheduling
                "Priority Scheduling",    // Scheduling algorithm name
                totalWaitingTime / processes.size(),    // Average waiting time
                totalTurnaroundTime / processes.size()  // Average turnaround time
        );
    }

    @Override
    public void printStatistics(List<Process> processes) {
        int totalWaitingTime = 0;
        int totalTurnaroundTime = 0;

        // Print statistics for each process
        for (Process process : processes) {
            System.out.println("Process " + process.getName() + ":");
            System.out.println("  Waiting Time: " + process.getWaitingTime());
            System.out.println("  Turnaround Time: " + process.getTurnaroundTime());
            totalWaitingTime += process.getWaitingTime();
            totalTurnaroundTime += process.getTurnaroundTime();
        }

        // Calculate and print average waiting and turnaround times
        double avgWaitingTime = (double) totalWaitingTime / processes.size();
        double avgTurnaroundTime = (double) totalTurnaroundTime / processes.size();

        System.out.println("Average Waiting Time: " + avgWaitingTime);
        System.out.println("Average Turnaround Time: " + avgTurnaroundTime);
    }
}
