package com.busticket.util;

import java.util.Scanner;

/**
 * Console utilities – coloured banners, dividers, input helpers.
 */
public class ConsoleUtil {

    // ANSI colour codes
    public static final String RESET  = "\u001B[0m";
    public static final String BOLD   = "\u001B[1m";
    public static final String GREEN  = "\u001B[32m";
    public static final String CYAN   = "\u001B[36m";
    public static final String YELLOW = "\u001B[33m";
    public static final String RED    = "\u001B[31m";
    public static final String BLUE   = "\u001B[34m";

    private static final Scanner sc = new Scanner(System.in);

    public static void printBanner() {
        System.out.println(CYAN + BOLD);
        System.out.println("╔══════════════════════════════════════════════════╗");
        System.out.println("║       🚌  BUS TICKET BOOKING SYSTEM  🚌          ║");
        System.out.println("║          Java + JDBC + MySQL Edition             ║");
        System.out.println("╚══════════════════════════════════════════════════╝");
        System.out.println(RESET);
    }

    public static void divider() {
        System.out.println(CYAN + "─".repeat(52) + RESET);
    }

    public static void success(String msg) {
        System.out.println(GREEN + "✔  " + msg + RESET);
    }

    public static void error(String msg) {
        System.out.println(RED + "✘  " + msg + RESET);
    }

    public static void info(String msg) {
        System.out.println(YELLOW + "ℹ  " + msg + RESET);
    }

    public static void section(String title) {
        System.out.println();
        System.out.println(BLUE + BOLD + "── " + title + " " + "─".repeat(Math.max(0, 44 - title.length())) + RESET);
    }

    /** Read a non-empty trimmed string from stdin. */
    public static String readString(String prompt) {
        System.out.print(BOLD + prompt + RESET);
        String line = sc.nextLine().trim();
        while (line.isEmpty()) {
            System.out.print("  (cannot be empty) " + prompt);
            line = sc.nextLine().trim();
        }
        return line;
    }

    /** Read an int in [min, max]. */
    public static int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(BOLD + prompt + RESET);
            try {
                int v = Integer.parseInt(sc.nextLine().trim());
                if (v >= min && v <= max) return v;
                System.out.println(RED + "  Please enter a number between " + min + " and " + max + "." + RESET);
            } catch (NumberFormatException e) {
                System.out.println(RED + "  Invalid number." + RESET);
            }
        }
    }

    /** Read a positive int. */
    public static int readPositiveInt(String prompt) {
        return readInt(prompt, 1, Integer.MAX_VALUE);
    }

    public static Scanner getScanner() { return sc; }
}
