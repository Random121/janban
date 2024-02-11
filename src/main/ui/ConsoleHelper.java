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

    // EFFECTS: puts a single new line
    public static void newLine() {
        newLine(1);
    }

    // EFFECTS: puts the specified number of new lines
    public static void newLine(int count) {
        System.out.print("\n".repeat(count));
    }

    // EFFECTS: gets the input of the user as a string
    public static String takeStringInput(String inputMessage) {
        System.out.print(inputMessage);
        return scanner.nextLine();
    }

    // EFFECTS: gets the input of the user as an integer
    //          and retry if it fails
    public static int takeIntInput(String inputMessage) {
        int input;

        while (true) {
            try {
                System.out.print(inputMessage);
                input = scanner.nextInt();

                // Got an input, we can now exit this
                break;
            } catch (InputMismatchException e) {
                System.out.println("Input is not an integer, please try again");

                // Clear the input buffer
                scanner.nextLine();
            }
        }

        // Removes the extra new line from the buffer
        scanner.nextLine();

        return input;
    }

    // EFFECTS: displays a message that pauses the console output and asks
    //          the user to try again
    public static void tryAgain() {
        System.out.print("Press enter to try again.");
        scanner.nextLine();
    }

    // EFFECTS: pauses the console output
    public static void pause() {
        System.out.print("Press enter to continue.");
        scanner.nextLine();
    }
}
