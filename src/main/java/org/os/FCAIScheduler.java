package org.os;

import java.util.*;

public class FCAIScheduler implements SchedulerFcai {
    private List<Process> processes;
    private double V1, V2;
    private Map<String, List<Integer>> quantumHistory;
    private double averageWaitingTime;
    private double averageTurnaroundTime;
    private Deque<Process> readyQueue;
    private List<String> executionOrder;

    public FCAIScheduler() {
        readyQueue = new LinkedList<>();
        executionOrder = new ArrayList<>();
        quantumHistory = new HashMap<>();
    }

    @Override
    public void schedule(List<Process> givenProcesses) {
        initializeProcesses(givenProcesses); // sort by arrival time
        calculateV1AndV2();

        int curTime = 0;
        int execTime = 0;
        int processIndex = 0;
        int contextSwitchTime = 0;
        Process curProcess = null;

        Set<Process> completedProcesses = new HashSet<>();
        int totalProcesses = processes.size();

        while (completedProcesses.size() < totalProcesses) {
            List<Process> newProcesses = new ArrayList<>();
            while (processIndex < totalProcesses) {
                Process process = processes.get(processIndex);
                if (process.getArrivalTime() > curTime)
                    break;
                process.calculateFCAIFactor(V1, V2);
                newProcesses.add(process);
                processIndex++;
            }
            newProcesses
                    .sort(Comparator.comparingInt(Process::getArrivalTime).thenComparingDouble(Process::getFCAIFactor));
            readyQueue.addAll(newProcesses);

            if (readyQueue.isEmpty() && processIndex == processes.size()) {
                // If all processes are processed and the ready queue is empty, exit
                System.out.println("Idle from time " + curTime + " to end of simulation");
                break;
            } else if (readyQueue.isEmpty()) {
                // No process ready, CPU is idle
                System.out.printf("Idle from time %d to %d\n", curTime, curTime + 1);
                curTime++;
                continue;
            }

            if (execTime > 0) {
                // Select the process with the lowest FCAI factor
                Process first = readyQueue.getFirst();
                curProcess = readyQueue.stream()
                        .min(Comparator.comparingDouble(Process::getFCAIFactor))
                        .orElse(null);

                if (curProcess != first) {
                    first.calculateFCAIFactor(V1, V2);
                    int unusedTime = first.getQuantum() - execTime;
                    first.setQuantum(first.getQuantum() + unusedTime);
                    quantumHistory.get(first.getName()).add(first.getQuantum());
                    System.out.printf(
                            "Time %d: Context switching: %s preempts %s, runs for %d units, remaining burst = %d.\n",
                            curTime, curProcess.getName(), first.getName(), execTime, curProcess.getRemainingTime());
                    curTime += contextSwitchTime;
                    readyQueue.removeFirst();
                    readyQueue.remove(curProcess);
                    readyQueue.add(first);
                    readyQueue.addFirst(curProcess);
                    execTime = 0;
                }
            }

            if (execTime == 0) {
                curProcess = readyQueue.getFirst();
                execTime = Math.min((int) Math.ceil(0.4 * curProcess.getQuantum()), curProcess.getRemainingTime());
                curProcess.setRemainingTime(curProcess.getRemainingTime() - execTime);
                System.out.printf("Time %d: %s starts execution, runs for %d units, remaining burst = %d.\n",
                        curTime, curProcess.getName(), execTime, curProcess.getRemainingTime());
                curTime += execTime;
            } else if (execTime < curProcess.getQuantum()) {
                // Continue executing the current process
                execTime++;
                curTime++;
                curProcess.setRemainingTime(curProcess.getRemainingTime() - 1);
            } else {
                // Update FCAI factor and adjust quantum time
                curProcess.calculateFCAIFactor(V1, V2);
                curProcess.setQuantum(curProcess.getQuantum() + 2);
                quantumHistory.get(curProcess.getName()).add(curProcess.getQuantum());
                readyQueue.addLast(readyQueue.removeFirst());
                if (curProcess != readyQueue.getFirst()) {
                    System.out.printf(
                            "Time %d: Context switching: %s preempts %s, runs for %d units, remaining burst = %d.\n",
                            curTime, readyQueue.getFirst().getName(), curProcess.getName(), execTime,
                            curProcess.getRemainingTime());
                    curTime += contextSwitchTime;
                    execTime = 0;
                }
            }

            if (curProcess.getRemainingTime() == 0) {
                // Process completes execution
                System.out.printf("Time %d: Process %s has completed execution\n", curTime, curProcess.getName());
                finalizeProcess(curProcess, curTime);
                completedProcesses.add(curProcess);
                readyQueue.removeFirst();
                execTime = 0;
            }
        }

        // Calculate and print results
        calculateAndPrintResults(completedProcesses);

        // Call the graph to visualize the scheduling
        double totalWaitingTime = 0;
        double totalTurnaroundTime = 0;
        for (Process p : completedProcesses) {
            totalWaitingTime += p.getWaitingTime();
            totalTurnaroundTime += p.getTurnaroundTime();
        }

        // Show the graph with the scheduling information
        CpuSchedulingGraph.showGraph(
                processes,                // List of processes
                curTime,                  // Total time of the scheduling
                "FCAI Scheduling",        // Scheduling algorithm name
                totalWaitingTime / processes.size(),    // Average waiting time
                totalTurnaroundTime / processes.size()  // Average turnaround time
        );
    }

    private void initializeProcesses(List<Process> processes) {
        this.processes = new ArrayList<>(processes);
        this.processes.sort(Comparator.comparingInt(Process::getArrivalTime));
        for (Process process : this.processes) {
            process.setHasExecuted40(false);
            quantumHistory.put(process.getName(), new ArrayList<>(List.of(process.getQuantum())));
        }
    }

    private void calculateV1AndV2() {
        double lastArrivalTime = getLastArrivalTime();
        double maxBurstTime = getMaxBurstTime();

        this.V1 = lastArrivalTime / 10.0;
        this.V2 = maxBurstTime / 10.0;
        System.out.println("V1 and V2: " + V1 + " " + V2);
    }

    private double getLastArrivalTime() {
        return processes.stream()
                .mapToInt(Process::getArrivalTime)
                .max()
                .orElse(1);
    }

    private double getMaxBurstTime() {
        return processes.stream()
                .mapToInt(Process::getBurstTime)
                .max()
                .orElse(1);
    }

    private void finalizeProcess(Process process, int currentTime) {
        process.setCompletionTime(currentTime);
        process.setTurnaroundTime(currentTime - process.getArrivalTime());
        process.setWaitingTime(process.getTurnaroundTime() - process.getBurstTime());
        executionOrder.add(process.getName() + " completed");
    }

    private void calculateAndPrintResults(Set<Process> completedProcesses) {
        int totalWaitingTime = 0;
        int totalTurnaroundTime = 0;

        for (Process process : completedProcesses) {
            totalWaitingTime += process.getWaitingTime();
            totalTurnaroundTime += process.getTurnaroundTime();
        }

        averageWaitingTime = (double) totalWaitingTime / completedProcesses.size();
        averageTurnaroundTime = (double) totalTurnaroundTime / completedProcesses.size();

        System.out.println("\nProcess Details:");
        for (Process process : completedProcesses) {
            System.out.printf("Process %s - Waiting Time: %d, Turnaround Time: %d%n",
                    process.getName(), process.getWaitingTime(), process.getTurnaroundTime());
        }

        System.out.printf("\nAverage Waiting Time: %.2f\n", averageWaitingTime);
        System.out.printf("Average Turnaround Time: %.2f\n", averageTurnaroundTime);
    }

    public List<String> getExecutionOrder() {
        return executionOrder;
    }

    public double getAverageWaitingTime() {
        return averageWaitingTime;
    }

    public double getAverageTurnaroundTime() {
        return averageTurnaroundTime;
    }

    @Override
    public void printResults() {
        System.out.println("\nExecution Order: " + String.join(" -> ", executionOrder));
        System.out.println("\nQuantum History:");
        for (Map.Entry<String, List<Integer>> entry : quantumHistory.entrySet()) {
            System.out.printf("Process %s: %s%n ", entry.getKey(), entry.getValue());
        }
    }

    private List<Integer> executionTimes = new ArrayList<>();

    public List<Integer> getExecutionTimes() {
        return executionTimes;
    }

    public Map<String, List<Integer>> getQuantumHistory() {
        return quantumHistory;
    }
}
