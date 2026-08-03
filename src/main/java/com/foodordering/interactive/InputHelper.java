package com.foodordering.interactive;

import java.util.InputMismatchException;
import java.util.NoSuchElementException;
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
            System.out.print(paintPrompt(prompt));
            try {
                int value = scanner.nextInt();
                scanner.nextLine();
                return value;
            } catch (InputMismatchException e) {
                System.out.println("  Invalid input. Please enter a number.");
                scanner.nextLine();
            } catch (NoSuchElementException e) {
                exitOnEof();
            }
        }
    }

    /** Reads a double from the console, retrying on invalid input. */
    public double readDouble(String prompt) {
        while (true) {
            System.out.print(paintPrompt(prompt));
            try {
                double value = scanner.nextDouble();
                scanner.nextLine();
                return value;
            } catch (InputMismatchException e) {
                System.out.println("  Invalid input. Please enter a number.");
                scanner.nextLine();
            } catch (NoSuchElementException e) {
                exitOnEof();
            }
        }
    }

    /** Reads a trimmed line of text from the console. */
    public String readLine(String prompt) {
        System.out.print(paintPrompt(prompt));
        try {
            return scanner.nextLine().trim();
        } catch (NoSuchElementException e) {
            return exitOnEof();
        }
    }

    /** Reads a yes/no answer, retrying until valid input is provided. */
    public boolean readYesNo(String prompt) {
        while (true) {
            System.out.print(paintPrompt(prompt + " (y/n): "));
            String input;
            try {
                input = scanner.nextLine().trim().toLowerCase();
            } catch (NoSuchElementException e) {
                exitOnEof();
                return false;
            }
            if (input.equals("y") || input.equals("yes")) return true;
            if (input.equals("n") || input.equals("no")) return false;
            System.out.println("  Please enter y or n.");
        }
    }

    /** Pauses execution until the user presses Enter. */
    public void pressEnter() {
        System.out.print(ConsoleStyle.paint(ConsoleStyle.DIM, "  Press Enter to continue..."));
        try {
            scanner.nextLine();
        } catch (NoSuchElementException e) {
            exitOnEof();
        }
    }

    /** Colors an input prompt with the theme accent (no-op without color support). */
    private String paintPrompt(String prompt) {
        return ConsoleStyle.paint(ConsoleStyle.CYAN, prompt);
    }

    /** Terminates gracefully when console input is closed (Ctrl+Z / EOF). */
    private String exitOnEof() {
        System.out.println("\n  Input closed. Goodbye!");
        System.exit(0);
        return null;
    }
}
