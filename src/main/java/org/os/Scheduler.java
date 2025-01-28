package org.os;
// Scheduler.java
import java.util.List;


public interface Scheduler {
    void schedule(List<Process> processes, int contextSwitchTime);
    void printStatistics(List<Process> processes);
}
