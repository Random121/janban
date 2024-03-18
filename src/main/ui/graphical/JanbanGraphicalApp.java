package ui.graphical;

import model.KanbanBoardList;
import model.exceptions.CorruptedSaveDataException;
import org.json.JSONException;
import persistence.KanbanJsonReader;
import persistence.KanbanJsonWriter;
import ui.RunnableApp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

// This class represents the main menu for the Janban graphical app.
public class JanbanGraphicalApp extends JFrame implements RunnableApp {
    private static final Dimension FRAME_DIMENSIONS = new Dimension(800, 600);
    private static final String SAVE_DATA_FILE = "./data/save.json";
    private static final String LOGO_IMAGE_FILE = "./assets/logo.png";
    private static final Dimension LOGO_DIMENSIONS = new Dimension(200, 134);

    private final KanbanJsonWriter kanbanJsonWriter;
    private final KanbanJsonReader kanbanJsonReader;

    // EFFECTS: Creates the graphical app with a title, json writer, json reader,
    //          and sets up the menu.
    public JanbanGraphicalApp() {
        super("Janban");

        this.kanbanJsonWriter = new KanbanJsonWriter(SAVE_DATA_FILE);
        this.kanbanJsonReader = new KanbanJsonReader(SAVE_DATA_FILE);

        setupStyle();
        setupLogo();
        setupButtons();
    }

    // MODIFIES: this
    // EFFECTS: Runs and displays the main menu for the Janban graphical app.
    @Override
    public void run() {
        pack();

        // place frame at center of screen
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // MODIFIES: this
    // EFFECTS: Configure the styling of the main menu JFrame.
    private void setupStyle() {
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        setPreferredSize(FRAME_DIMENSIONS);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
    }

    // MODIFIES: this
    // EFFECTS: Creates and places the logo for Janban on the main menu JFrame.
    private void setupLogo() {
        JPanel logoPanel = new JPanel();
        logoPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        logoPanel.setMaximumSize(LOGO_DIMENSIONS);
        logoPanel.setBorder(BorderFactory.createEmptyBorder(50, 0, 0, 0));

        ImageIcon image = getScaledImageIcon(LOGO_IMAGE_FILE, LOGO_DIMENSIONS.width, LOGO_DIMENSIONS.height);
        JLabel imageLabel = new JLabel(image);

        logoPanel.add(imageLabel);

        add(logoPanel);
    }

    // MODIFIES: this
    // EFFECTS: Creates and places the load and skip loading buttons for Janban on the main menu JFrame.
    private void setupButtons() {
        final Dimension BUTTON_DIMENSIONS = new Dimension(100, 30);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));

        JButton newProjectButton = new JButton("Load Existing Projects");

        // removes that ugly box around the button text
        newProjectButton.setFocusable(false);
        newProjectButton.setPreferredSize(BUTTON_DIMENSIONS);
        newProjectButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        newProjectButton.addActionListener(new LoadProjectsButtonListener());

        JButton loadProjectButton = new JButton("Start From Scratch");

        // removes that ugly box around the button text
        loadProjectButton.setFocusable(false);
        loadProjectButton.setPreferredSize(BUTTON_DIMENSIONS);
        loadProjectButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loadProjectButton.addActionListener(new SkipLoadingButtonListener());

        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(newProjectButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(loadProjectButton);
        buttonPanel.add(Box.createVerticalGlue());

        add(buttonPanel);
    }

    // EFFECTS: Creates an ImageIcon from an image file with the
    //          specified dimensions.
    private ImageIcon getScaledImageIcon(String imagePath, int width, int height) {
        ImageIcon image = new ImageIcon(imagePath);
        Image scaled = image.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);

        image.setImage(scaled);

        return image;
    }

    // MODIFIES: this
    // EFFECTS: Opens the project selection menu window and closes
    //          the current one.
    private void openProjectSelectionMenu(KanbanBoardList boards) {
        new ProjectSelectionMenu(boards, kanbanJsonWriter, this);

        // close the current window
        dispose();
    }

    private class LoadProjectsButtonListener implements ActionListener {

        // EFFECTS: Opens a project selection menu for the previously saved projects.
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                KanbanBoardList kanbanBoards = kanbanJsonReader.read();

                openProjectSelectionMenu(kanbanBoards);
            } catch (IOException ex) {
                Popup.error(JanbanGraphicalApp.this,
                            "Failed to open the save file!",
                            "Error while loading");
            } catch (CorruptedSaveDataException | JSONException ex) {
                Popup.error(JanbanGraphicalApp.this,
                            "Failed to read the save data!",
                            "Error while loading");
            }
        }
    }

    private class SkipLoadingButtonListener implements ActionListener {

        // EFFECTS: Opens a project selection menu for a new list of projects.
        @Override
        public void actionPerformed(ActionEvent e) {
            openProjectSelectionMenu(new KanbanBoardList());
        }
    }
}
