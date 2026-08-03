package com.foodordering.interactive;

/**
 * Central ANSI color/style definitions for the futuristic console theme.
 * All colors are automatically disabled when the terminal does not
 * support them (e.g. non-interactive output, IDEs, the NO_COLOR env var),
 * so the UI still renders cleanly with plain box-drawing characters.
 */
public final class ConsoleStyle {
    private static final boolean COLOR = supportsColor();

    public static final String RESET   = "\u001B[0m";
    public static final String BOLD    = "\u001B[1m";
    public static final String DIM     = "\u001B[2m";
    public static final String CYAN    = "\u001B[36m";
    public static final String MAGENTA = "\u001B[35m";
    public static final String GREEN   = "\u001B[32m";
    public static final String YELLOW  = "\u001B[33m";
    public static final String RED     = "\u001B[31m";
    public static final String BRIGHT_CYAN    = "\u001B[96m";
    public static final String BRIGHT_MAGENTA = "\u001B[95m";
    public static final String BRIGHT_GREEN   = "\u001B[92m";
    public static final String BRIGHT_YELLOW  = "\u001B[93m";
    public static final String BRIGHT_RED     = "\u001B[91m";
    public static final String BRIGHT_BLUE    = "\u001B[94m";
    public static final String BRIGHT_WHITE   = "\u001B[97m";

    private ConsoleStyle() {
    }

    /** Detects whether the current terminal supports ANSI colors. */
    private static boolean supportsColor() {
        if (System.getenv("NO_COLOR") != null) return false;
        return System.console() != null;
    }

    /** @return true when ANSI colors are active. */
    public static boolean colorEnabled() {
        return COLOR;
    }

    /** Wraps text in a color/style code (no-op when colors are disabled). */
    public static String paint(String code, String text) {
        return COLOR ? code + text + RESET : text;
    }

    /** Wraps text in bold (no-op when colors are disabled). */
    public static String bold(String text) {
        return COLOR ? BOLD + text + RESET : text;
    }

    /** Maps an order status to its display symbol (no ANSI codes). */
    public static String statusSymbol(String status) {
        return switch (status) {
            case "PENDING"          -> "\u25CF PENDING";
            case "CONFIRMED"        -> "\u25B6 CONFIRMED";
            case "PREPARING"        -> "\u25CE PREPARING";
            case "OUT_FOR_DELIVERY" -> "\u25C9 OUT FOR DELIVERY";
            case "DELIVERED"        -> "\u2714 DELIVERED";
            case "CANCELLED"        -> "\u2715 CANCELLED";
            default -> status;
        };
    }

    /** Maps an order status to its theme color. */
    public static String statusColor(String status) {
        return switch (status) {
            case "PENDING"          -> YELLOW;
            case "CONFIRMED"        -> CYAN;
            case "PREPARING"        -> MAGENTA;
            case "OUT_FOR_DELIVERY" -> BRIGHT_BLUE;
            case "DELIVERED"        -> GREEN;
            case "CANCELLED"        -> RED;
            default -> "";
        };
    }

    /** Returns a fully painted status badge for display in plain text lines. */
    public static String statusBadge(String status) {
        return paint(statusColor(status), statusSymbol(status));
    }
}
