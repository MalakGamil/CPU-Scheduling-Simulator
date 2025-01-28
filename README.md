# CPU Scheduling Simulator

A Java-based application to simulate different CPU scheduling algorithms. This program takes input for processes, selects a scheduling algorithm, and calculates the scheduling order, waiting time, turnaround time, and other statistics. It also provides a graphical representation of the scheduling results.

---

## Features

1. **Supported Scheduling Algorithms**:
   - **Non-Preemptive Priority Scheduling**: Schedules processes based on priority (lower number = higher priority).
   - **Non-Preemptive Shortest Job First (SJF)**: Selects the process with the shortest burst time.
   - **Shortest Remaining Time First (SRTF)**: A preemptive version of SJF.
   - **FCAI Scheduling**: A custom scheduling algorithm .

2. **Process Management**:
   - Input for process attributes: Arrival Time, Burst Time, and Priority (if applicable).
   - Handles multiple processes and calculates scheduling metrics.

3. **Context Switching**:
   - Allows users to input context switching time for more realistic simulations.

4. **Statistics**:
   - Calculates and displays:
     - Average Waiting Time
     - Average Turnaround Time
     - Gantt Chart (graphical scheduling representation)

---

## How It Works

### Input Requirements
- Processes: Each process should have the following attributes:
  - Arrival Time
  - Burst Time
  - Priority (if applicable)
- Context Switching Time: Non-negative integer.

### Steps:
1. The user provides a list of processes via the console or input handler.
2. The user selects a scheduling algorithm from the menu:
   - Non-Preemptive Priority Scheduling
   - Non-Preemptive SJF
   - SRTF
   - FCAI Scheduling
3. The scheduler processes the input and calculates scheduling order and metrics.
4. A graphical representation of the results ( Gantt Chart) is displayed.

---

## Input Format

### Process Input
Each process requires the following fields:
- **Arrival Time**: Time at which the process enters the ready queue.
- **Burst Time**: Total CPU time required by the process.
- **Priority**: An integer indicating process priority (lower number = higher priority). Required for priority-based scheduling.

### Example Input
The input can be provided interactively through the console. For example:
