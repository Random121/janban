package ui;

import java.util.InputMismatchException;
import java.util.Scanner;

public class ConsoleHelper {
    private static final Scanner scanner;

    static {
        scanner = new Scanner(System.in);
    }

    private ConsoleHelper() {

    }

    public static void newLine() {
        newLine(1);
    }

    public static void newLine(int count) {
        System.out.print("\n".repeat(count));
    }

    public static String takeStringInput(String inputMessage) {
        System.out.print(inputMessage);
        return scanner.nextLine();
    }

    public static int takeIntInput(String inputMessage) {
        int input;

        while (true) {
            try {
                System.out.print(inputMessage);
                input = scanner.nextInt();

                // Removes the extra new line from the buffer
                scanner.nextLine();

                // Got an input, we can now exit this
                break;
            } catch (InputMismatchException e) {
                System.out.println("Input is not an integer, please try again");

                // Clear the input buffer
                scanner.nextLine();
            }

        }

        return input;
    }

    public static void tryAgain() {
        System.out.print("Press enter to try again.");
        scanner.nextLine();
    }

    public static void pause() {
        System.out.print("Press enter to continue.");
        scanner.nextLine();
    }

    public static void unknownCommand() {
        System.out.println("Unknown input command");
        tryAgain();
    }
}
