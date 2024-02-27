package ui;

import java.util.InputMismatchException;
import java.util.Scanner;

// This class contains helper methods used for printing to the console
// or taking user inputs.
public class ConsoleHelper {
    private static final Scanner scanner;

    // EFFECTS: statically initializes the ConsoleHelper with a scanner that
    //          takes input
    static {
        scanner = new Scanner(System.in);
    }

    // EFFECTS: cannot construct ConsoleHelper since this class is static
    private ConsoleHelper() {
    }

    // EFFECTS: puts a single new line in the console
    public static void newLine() {
        newLine(1);
    }

    // EFFECTS: puts the specified number of new lines in the console
    public static void newLine(int count) {
        System.out.print("\n".repeat(count));
    }

    // EFFECTS: gets the input of the user as a string from the console
    public static String takeStringInput(String inputMessage, boolean toLowerCase) {
        System.out.print(inputMessage);
        String input = scanner.nextLine();
        return toLowerCase ? input.toLowerCase()
                           : input;
    }

    // EFFECTS: gets the input of the user as an integer from the console
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

    // EFFECTS: pauses the console output with a message
    public static void pause() {
        System.out.print("Press enter to continue.");
        scanner.nextLine();
    }

    // EFFECTS: pauses the console output with a specified message
    public static void pause(String message) {
        System.out.print(message);
        scanner.nextLine();
    }
}
