package ui.console;

import java.util.InputMismatchException;
import java.util.Optional;
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

    // EFFECTS: gets a string input from the user in the console
    public static String readString(String inputMessage, boolean toLowerCase) {
        System.out.print(inputMessage);

        String input = scanner.nextLine();

        return toLowerCase ? input.toLowerCase() : input;
    }

    // EFFECTS: gets an integer input from the user in the console
    //          and retry if it fails
    public static int readInteger(String inputMessage) {
        while (true) {
            System.out.print(inputMessage);

            try {
                int input = scanner.nextInt();

                // Clear the input buffer
                scanner.nextLine();

                return input;
            } catch (InputMismatchException e) {
                System.out.println("Input is not an integer, please try again");
            }

            // Clear the input buffer
            scanner.nextLine();
        }
    }

    // EFFECTS: gets an optional integer input from the user in the console
    public static Optional<Integer> readOptionalInteger(String inputMessage) {
        while (true) {
            System.out.print(inputMessage);

            String input = scanner.nextLine();

            // User skipping input
            if (input.isBlank()) {
                return Optional.empty();
            }

            try {
                int intInput = Integer.parseInt(input);

                return Optional.of(intInput);
            } catch (NumberFormatException e) {
                System.out.println("Input is not an integer, please try again");
            }
        }
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
