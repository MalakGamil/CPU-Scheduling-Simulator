package org.os;

import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Step 1: Input processes
        List<Process> processes = InputHandler.getProcesses(scanner);
        if (processes.isEmpty()) {
            System.out.println("No valid processes provided. Exiting.");
            System.exit(0);
        }

        // Step 2: Choose scheduling algorithm
        int choice = chooseScheduler(scanner);

        // Step 3: Choose context switching time
        int contextSwitchTime = getContextSwitchTime(scanner);

        // Step 4: Instantiate the chosen scheduler
        Scheduler scheduler = null;
        SchedulerFcai schedulerFcai = null;

        switch (choice) {
            case 1:
                scheduler = new PriorityScheduler(); // Non-Preemptive Priority Scheduling
                break;
            case 2:
                scheduler = new SJFScheduler(); // Non-Preemptive SJF
                break;
            case 3:
                scheduler = new SRTFScheduler(); // SRTF
                break;
            case 4:
                schedulerFcai = new FCAIScheduler(); // FCAI Scheduling
                break;
            default:
                System.out.println("Invalid choice! Exiting.");
                System.exit(0);
        }

        // Step 5: Schedule processes and print statistics
        if (scheduler != null) {
            // Schedule the processes using the selected scheduler
            scheduler.schedule(processes, contextSwitchTime);
            scheduler.printStatistics(processes);

            // If the scheduler is of type SRTF, Priority, or SJF, call CpuSchedulingGraph.showGraph
            if (scheduler instanceof SRTFScheduler || scheduler instanceof PriorityScheduler || scheduler instanceof SJFScheduler) {
                // Assuming the total time is the current time after scheduling
                int totalTime = processes.stream().mapToInt(Process::getArrivalTime).max().orElse(0) + 10; // Adding a small buffer
                double averageWaitingTime = processes.stream().mapToInt(Process::getWaitingTime).average().orElse(0);
                double averageTurnaroundTime = processes.stream().mapToInt(Process::getTurnaroundTime).average().orElse(0);
                // Call CpuSchedulingGraph to show the graphical representation
                CpuSchedulingGraph.showGraph(processes, totalTime, scheduler.getClass().getSimpleName(),
                        averageWaitingTime, averageTurnaroundTime);
            }
        } else if (schedulerFcai != null) {
            // Handle FCAI scheduling
            schedulerFcai.schedule(processes);
            schedulerFcai.printResults();

            // Call CpuSchedulingGraph for FCAI scheduling as well
            double totalWaitingTime = processes.stream().mapToInt(Process::getWaitingTime).average().orElse(0);
            double totalTurnaroundTime = processes.stream().mapToInt(Process::getTurnaroundTime).average().orElse(0);
            int totalTime = processes.stream().mapToInt(Process::getArrivalTime).max().orElse(0) + 10; // Adding a small buffer
            CpuSchedulingGraph.showGraph(processes, totalTime, "FCAI Scheduling",
                    totalWaitingTime, totalTurnaroundTime);
        } else {
            System.out.println("No valid scheduler was selected. Exiting.");
            System.exit(0);
        }
    }

    private static int chooseScheduler(Scanner scanner) {
        System.out.println("Select Scheduler:");
        System.out.println("1. Non-Preemptive Priority Scheduling");
        System.out.println("2. Non-Preemptive Shortest Job First (SJF)");
        System.out.println("3. Shortest Remaining Time First (SRTF)");
        System.out.println("4. FCAI Scheduling");
        System.out.print("Enter your choice: ");
        return scanner.nextInt();
    }

    private static int getContextSwitchTime(Scanner scanner) {
        System.out.print("Enter context switching time: ");
        int contextSwitchTime = scanner.nextInt();
        if (contextSwitchTime < 0) {
            System.out.println("Context switching time cannot be negative. Exiting.");
            System.exit(0);
        }
        return contextSwitchTime;
    }
}
