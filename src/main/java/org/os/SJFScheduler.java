package org.os;

import java.util.*;

public class SJFScheduler implements Scheduler {

    private static final int AGING_THRESHOLD = 20; // Time threshold for aging
    private static final int AGEING_INCREMENT = 5; // How much the burst time will be reduced

    @Override
    public void schedule(List<Process> processes, int contextSwitchTime) {
        List<Process> completedProcesses = new ArrayList<>();
        int currentTime = 0;

        while (!processes.isEmpty()) {
            // Filter processes that have arrived
            List<Process> availableProcesses = new ArrayList<>();
            for (Process process : processes) {
                if (process.getArrivalTime() <= currentTime) {
                    availableProcesses.add(process);
                }
            }

            // If no processes are ready, increment time
            if (availableProcesses.isEmpty()) {
                currentTime++;
                continue;
            }

            // Aging: Increase the priority (decrease burst time) of processes that have been waiting too long
            for (Process process : availableProcesses) {
                if (process.getWaitingTime() > AGING_THRESHOLD) {
                    // Apply aging by reducing the burst time of long-waiting processes
                    int newBurstTime = Math.max(1, process.getBurstTime() - AGEING_INCREMENT);
                    process.setBurstTime(newBurstTime); // Update burst time
                    System.out.println("Aging applied to process " + process.getName() + " (new burst time: " + newBurstTime + ")");
                }
            }

            // Select the process with the shortest burst time (now considering aging)
            Process nextProcess = availableProcesses.stream()
                    .min(Comparator.comparingInt(Process::getBurstTime))
                    .orElse(null);

            assert nextProcess != null;

            // Execute the process
            System.out.println("Executing process " + nextProcess.getName() +
                    " from " + currentTime + " to " + (currentTime + nextProcess.getBurstTime()));

            // Update waiting time and turnaround time
            nextProcess.setWaitingTime(currentTime - nextProcess.getArrivalTime());
            currentTime += nextProcess.getBurstTime() + contextSwitchTime; // Add burst and context switch time
            nextProcess.setTurnaroundTime(nextProcess.getWaitingTime() + nextProcess.getBurstTime());
            completedProcesses.add(nextProcess);

            // Remove completed process
            processes.remove(nextProcess);
        }

        printStatistics(completedProcesses);

        // After scheduling, calculate averages
        double averageWaitingTime = completedProcesses.stream()
                .mapToInt(Process::getWaitingTime)
                .average()
                .orElse(0.0);

        double averageTurnaroundTime = completedProcesses.stream()
                .mapToInt(Process::getTurnaroundTime)
                .average()
                .orElse(0.0);

        // Show the graph
        CpuSchedulingGraph.showGraph(completedProcesses, currentTime, "SJF", averageWaitingTime, averageTurnaroundTime);
    }


    @Override
    public void printStatistics(List<Process> processes) {
        int totalWaitingTime = 0;
        int totalTurnaroundTime = 0;

        System.out.println("\nProcess Statistics:");
        for (Process process : processes) {
            System.out.println("Process " + process.getName() +
                    " - Waiting Time: " + process.getWaitingTime() +
                    ", Turnaround Time: " + process.getTurnaroundTime());
            totalWaitingTime += process.getWaitingTime();
            totalTurnaroundTime += process.getTurnaroundTime();
        }

        System.out.println("\nAverage Waiting Time: " + (double) totalWaitingTime / processes.size());
        System.out.println("Average Turnaround Time: " + (double) totalTurnaroundTime / processes.size());
    }
}
