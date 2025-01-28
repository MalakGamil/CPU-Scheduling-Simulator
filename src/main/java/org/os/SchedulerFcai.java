package org.os;

import java.util.List;

public interface SchedulerFcai {
    void schedule(List<Process> givenProcesses);

    void printResults();
}
