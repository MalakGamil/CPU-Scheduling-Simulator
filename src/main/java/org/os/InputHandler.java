package org.os;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class InputHandler {
    public static List<Process> getProcesses(Scanner scanner) {
        List<Process> processes = new ArrayList<>();

        System.out.print("Enter the number of processes: ");
        int numProcesses = scanner.nextInt();

        for (int i = 0; i < numProcesses; i++) {
            System.out.println("Process " + (i + 1) + ":");
            System.out.print("Name: ");
            String name = scanner.next();
            System.out.print("Color (hex format, e.g., #FF0000): ");
            String colorInput = scanner.next();
            System.out.print("Arrival Time: ");
            int arrivalTime = scanner.nextInt();
            System.out.print("Burst Time: ");
            int burstTime = scanner.nextInt();
            System.out.print("Priority: ");
            int priority = scanner.nextInt();
            System.out.print("Quantum: "); // Asking for Quantum as part of the FCAI process
            int quantum = scanner.nextInt();

            // Adding validation for non-negative values
            if (arrivalTime < 0 || burstTime < 0 || priority < 0 || quantum < 0) {
                System.out.println("Arrival Time, Burst Time, Priority, and Quantum must be non-negative.");
                return processes; // Return empty list in case of invalid input
            }

            // Convert the color string (e.g., "#FF0000") to a Color object
            Color color;
            try {
                color = Color.decode(colorInput);
            } catch (NumberFormatException e) {
                System.out.println("Invalid color format: " + colorInput);
                return processes; // Return empty list in case of invalid input
            }

            // Add process with quantum
            processes.add(new Process(name, color, arrivalTime, burstTime, priority, quantum));
        }

        return processes;
    }
}
