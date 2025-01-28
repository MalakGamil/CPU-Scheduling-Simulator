package org.os;

import java.util.*;

public class SRTFScheduler implements Scheduler {

    private static final int AGING_THRESHOLD = 20; // Time threshold for aging
    private static final int AGEING_INCREMENT = 5; // How much the burst time will be reduced

    @Override
    public void schedule(List<Process> processes, int contextSwitchTime) {
        // Sort processes by arrival time
        processes.sort(Comparator.comparingInt(Process::getArrivalTime));

        // Initialize variables
        int time = 0; // Current time
        int completedProcesses = 0;
        Process currentProcess = null;
        List<String> ganttChart = new ArrayList<>();
        int contextSwitchCounter = 0;

        while (completedProcesses < processes.size()) {
            // Find the process with the shortest remaining time
            Process shortest = null;
            for (Process p : processes) {
                if (p.getRemainingTime() > 0 && p.getArrivalTime() <= time) {
                    if (shortest == null || p.getRemainingTime() < shortest.getRemainingTime()) {
                        shortest = p;
                    }
                }
            }

            // Aging: Increase the priority (decrease burst time) of processes that have been waiting too long
            for (Process process : processes) {
                if (process.getWaitingTime() > AGING_THRESHOLD) {
                    // Apply aging by reducing the burst time of long-waiting processes
                    int newBurstTime = Math.max(1, process.getRemainingTime() - AGEING_INCREMENT);
                    process.setRemainingTime(newBurstTime); // Update burst time
                    System.out.println("Aging applied to process " + process.getName() + " (new remaining burst time: " + newBurstTime + ")");
                }
            }

            // If no processes are ready, increment time (CPU idle)
            if (shortest == null) {
                time++;
                continue;
            }

            // Handle context switching if necessary
            if (currentProcess != shortest) {
                if (currentProcess != null) {
                    time += contextSwitchTime; // Add context switching time
                    contextSwitchCounter++;
                }
                currentProcess = shortest;
                ganttChart.add(currentProcess.getName()); // Add process to Gantt chart
            }

            // Execute the process
            currentProcess.decrementRemainingTime(1);
            time++;

            // Mark the process as completed
            if (currentProcess.getRemainingTime() == 0) {
                currentProcess.setTurnaroundTime(time - currentProcess.getArrivalTime());
                currentProcess.setWaitingTime(currentProcess.getTurnaroundTime() - currentProcess.getBurstTime());
                completedProcesses++;
            }
        }

        // Print Gantt Chart
        System.out.println("\nGantt Chart:");
        System.out.println(String.join(" → ", ganttChart));

        // Print statistics after scheduling
        printStatistics(processes);

        // Print total context switches
        System.out.println("Total Context Switches: " + contextSwitchCounter);

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
                time,                     // Total time of the scheduling
                "SRTF",                   // Scheduling algorithm name
                totalWaitingTime / processes.size(),    // Average waiting time
                totalTurnaroundTime / processes.size()  // Average turnaround time
        );
    }

    @Override
    public void printStatistics(List<Process> processes) {
        double totalWaitingTime = 0;
        double totalTurnaroundTime = 0;

        // Calculate total waiting time and turnaround time
        for (Process p : processes) {
            totalWaitingTime += p.getWaitingTime();
            totalTurnaroundTime += p.getTurnaroundTime();
        }

        // Print process details
        System.out.println("\nProcess Details:");
        for (Process p : processes) {
            System.out.println("Process " + p.getName() + " | Completion Time: " +
                    (p.getTurnaroundTime() + p.getArrivalTime()) +
                    " | Turnaround Time: " + p.getTurnaroundTime() +
                    " | Waiting Time: " + p.getWaitingTime());
        }

        // Print average waiting time and turnaround time
        System.out.println("\nAverage Waiting Time: " + (totalWaitingTime / processes.size()));
        System.out.println("Average Turnaround Time: " + (totalTurnaroundTime / processes.size()));
    }
}
