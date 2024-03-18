package ui.graphical;

import javax.swing.*;
import java.awt.*;

// This is a utility class for displaying popups.
public class Popup {

    // EFFECTS: Displays a popup window signifying an error.
    public static void error(Component parent, Object message, String title) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.ERROR_MESSAGE);
    }

    // MODIFIES: inputs
    // EFFECTS: Displays a popup window specifically for editing values
    //          and returns whether it was successful.
    public static boolean editingPopup(Component parent, JComponent[] inputs, String title) {
        final String[] options = {"Apply", "Cancel"};
        final String defaultOption = options[0];

        int result = JOptionPane.showOptionDialog(parent,
                                                  inputs,
                                                  title,
                                                  JOptionPane.OK_CANCEL_OPTION,
                                                  JOptionPane.PLAIN_MESSAGE,
                                                  null,
                                                  options,
                                                  defaultOption);

        return result == JOptionPane.OK_OPTION;
    }

    // MODIFIES: inputs
    // EFFECTS: Displays a popup window specifically for creating values
    //          and returns whether it was successful.
    public static boolean creatingPopup(Component parent, JComponent[] inputs, String title) {
        final String[] options = {"Create", "Cancel"};
        final String defaultOption = options[0];

        int result = JOptionPane.showOptionDialog(parent,
                                                  inputs,
                                                  title,
                                                  JOptionPane.OK_CANCEL_OPTION,
                                                  JOptionPane.PLAIN_MESSAGE,
                                                  null,
                                                  options,
                                                  defaultOption);

        return result == JOptionPane.OK_OPTION;
    }
}
