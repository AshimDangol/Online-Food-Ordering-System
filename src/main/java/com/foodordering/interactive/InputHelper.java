package com.foodordering.interactive;

import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * Utility class for safe console input handling.
 * Wraps a Scanner and provides typed read methods
 * with built-in error recovery for invalid input.
 */
public class InputHelper {
    private final Scanner scanner;

    /** Wraps the given Scanner for safe input operations. */
    public InputHelper(Scanner scanner) {
        this.scanner = scanner;
    }

    /** Reads an integer from the console, retrying on invalid input. */
    public int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                int value = scanner.nextInt();
                scanner.nextLine();
                return value;
            } catch (InputMismatchException e) {
                System.out.println("  Invalid input. Please enter a number.");
                scanner.nextLine();
            }
        }
    }

    /** Reads a double from the console, retrying on invalid input. */
    public double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                double value = scanner.nextDouble();
                scanner.nextLine();
                return value;
            } catch (InputMismatchException e) {
                System.out.println("  Invalid input. Please enter a number.");
                scanner.nextLine();
            }
        }
    }

    /** Reads a trimmed line of text from the console. */
    public String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    /** Reads a yes/no answer, retrying until valid input is provided. */
    public boolean readYesNo(String prompt) {
        while (true) {
            System.out.print(prompt + " (y/n): ");
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("y") || input.equals("yes")) return true;
            if (input.equals("n") || input.equals("no")) return false;
            System.out.println("  Please enter y or n.");
        }
    }

    /** Pauses execution until the user presses Enter. */
    public void pressEnter() {
        System.out.print("  Press Enter to continue...");
        scanner.nextLine();
    }
}
