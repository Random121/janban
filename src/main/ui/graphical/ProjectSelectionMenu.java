package ui.graphical;

import model.KanbanBoard;
import model.KanbanBoardList;
import model.exceptions.DuplicateColumnException;
import persistence.KanbanJsonWriter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

// This class represents the project selection menu for the Janban graphical app.
public class ProjectSelectionMenu extends JFrame {
    private static final Dimension FRAME_DIMENSIONS = new Dimension(800, 600);

    private final KanbanJsonWriter jsonWriter;
    private final KanbanBoardList kanbanBoards;

    private JFrame existingKanbanBoardMenu;

    private DefaultListModel<KanbanBoard> kanbanBoardListModel;
    private JList<KanbanBoard> kanbanBoardList;

    // EFFECTS: Creates the project selection menu with a title, json writer, kanban board list,
    //          no existing kanban board menu, and sets up the menu.
    public ProjectSelectionMenu(KanbanBoardList kanbanBoards, KanbanJsonWriter jsonWriter, JFrame parentFrame) {
        super("Janban | Project Selection");

        this.kanbanBoards = kanbanBoards;
        this.jsonWriter = jsonWriter;

        this.existingKanbanBoardMenu = null;

        setupStyle();
        setupButtons();
        setupKanbanBoardList();

        pack();

        // place frame at center of screen
        setLocationRelativeTo(parentFrame);
        setVisible(true);
    }

    // MODIFIES: this
    // EFFECTS: Configure the styling of this menu.
    private void setupStyle() {
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        setPreferredSize(FRAME_DIMENSIONS);

        // we have a custom window closing behaviour to allow saving
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowCloseListener());

        setResizable(false);
    }

    // MODIFIES: this
    // EFFECTS: Creates and places the new project and load project buttons for the menu.
    private void setupButtons() {
        final Dimension BUTTON_DIMENSIONS = new Dimension(25, 20);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

        JButton newProjectButton = new JButton("New Project");
        newProjectButton.setPreferredSize(BUTTON_DIMENSIONS);
        // removes that ugly box around the button text
        newProjectButton.setFocusable(false);
        newProjectButton.addActionListener(new NewProjectButtonListener());

        JButton loadProjectButton = new JButton("Load Project");
        loadProjectButton.setPreferredSize(BUTTON_DIMENSIONS);
        // removes that ugly box around the button text
        loadProjectButton.setFocusable(false);
        loadProjectButton.addActionListener(new LoadProjectButtonListener());

        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(newProjectButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        buttonPanel.add(loadProjectButton);

        add(buttonPanel);
    }

    // MODIFIES: this
    // EFFECTS: Creates and places the list of projects on the menu.
    private void setupKanbanBoardList() {
        final Dimension LIST_DIMENSIONS = new Dimension(600, 400);

        JPanel kanbanBoardsPanel = new JPanel();
        kanbanBoardsPanel.setLayout(new BoxLayout(kanbanBoardsPanel, BoxLayout.X_AXIS));

        // this is the internal representation of our kanban board list
        DefaultListModel<KanbanBoard> boardListModel = new DefaultListModel<>();

        // we need this reference to add kanban boards
        this.kanbanBoardListModel = boardListModel;

        synchronizeBoardModels();

        JList<KanbanBoard> boardList = new JList<>(boardListModel);
        boardList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        boardList.setCellRenderer(new KanbanBoardListCellRenderer());

        // we need this reference to get the currently selected kanban board
        this.kanbanBoardList = boardList;

        JScrollPane scrollPane = new JScrollPane(boardList);
        scrollPane.setPreferredSize(LIST_DIMENSIONS);
        scrollPane.setMaximumSize(LIST_DIMENSIONS);

        kanbanBoardsPanel.add(Box.createVerticalGlue());
        kanbanBoardsPanel.add(scrollPane);
        kanbanBoardsPanel.add(Box.createVerticalGlue());

        add(kanbanBoardsPanel, BorderLayout.CENTER);
    }

    // MODIFIES: this
    // EFFECTS: Synchronizes our actual KanbanBoardList model with the
    //          list model used for the view.
    private void synchronizeBoardModels() {
        kanbanBoardListModel.clear();
        kanbanBoardListModel.addAll(kanbanBoards.getBoards());
    }

    // EFFECTS: Returns the original string or defaultValue if it is blank.
    private String getOrDefault(String string, String defaultValue) {
        return string.isBlank() ? defaultValue : string;
    }

    private class NewProjectButtonListener implements ActionListener {

        // MODIFIES: this
        // EFFECTS: Opens a popup for creation of a new project.
        @Override
        public void actionPerformed(ActionEvent e) {
            JTextField nameField = new JTextField();
            JTextField descriptionField = new JTextField();
            JTextField completedColumnNameField = new JTextField("Done");

            JComponent[] inputs = {
                    new JLabel("Name:"), nameField,
                    new JLabel("Description:"), descriptionField,
                    new JLabel("Completed column name:"), completedColumnNameField
            };

            boolean success = Popup.creatingPopup(ProjectSelectionMenu.this, inputs, "Create a new project");

            if (!success) {
                return;
            }

            String nameOrDefault = getOrDefault(nameField.getText(), "Unnamed project");
            String completedColumnNameOrDefault = getOrDefault(completedColumnNameField.getText(), "Done");

            createAndAddNewProject(nameOrDefault, descriptionField.getText(), completedColumnNameOrDefault);
            synchronizeBoardModels();
        }

        // MODIFIES: this
        // EFFECTS: Adds a new project and display a popup if an error occurs.
        private void createAndAddNewProject(String name, String description, String completedColumnName) {
            KanbanBoard newBoard = new KanbanBoard(name, description, completedColumnName);

            try {
                newBoard.addDefaultColumns();
            } catch (DuplicateColumnException ex) {
                String errorMessage = String.format("Your completed column name cannot be '%s' or '%s'",
                                                    KanbanBoard.DEFAULT_BACKLOG_COLUMN_NAME,
                                                    KanbanBoard.DEFAULT_WIP_COLUMN_NAME);

                Popup.error(ProjectSelectionMenu.this,
                            errorMessage,
                            "Error while creating kanban board");

                return;
            }

            kanbanBoards.addBoard(newBoard);
        }
    }

    private class LoadProjectButtonListener implements ActionListener {

        // MODIFIES: this
        // EFFECTS: Load the currently selected project.
        @Override
        public void actionPerformed(ActionEvent e) {
            KanbanBoard selectedBoard = kanbanBoardList.getSelectedValue();

            // user hasn't selected a kanban board yet
            if (selectedBoard == null) {
                return;
            }

            // only allow one window of a kanban board to be
            // open at a time
            if (existingKanbanBoardMenu != null) {
                existingKanbanBoardMenu.dispose();
            }

            existingKanbanBoardMenu = new KanbanBoardMenu(selectedBoard, ProjectSelectionMenu.this);
        }
    }

    // This class is a window hook to perform saving when the window is closing.
    private class WindowCloseListener extends WindowAdapter {

        // EFFECTS: Provide the user with the option to save their current projects
        //          before the window is closed.
        @Override
        public void windowClosing(WindowEvent e) {
            final String[] options = {"Yes", "No", "Cancel"};
            final String defaultOption = options[2];

            int input = JOptionPane.showOptionDialog(ProjectSelectionMenu.this,
                                                     "Would you like to save all of your projects?",
                                                     "Unsaved changes",
                                                     JOptionPane.YES_NO_CANCEL_OPTION,
                                                     JOptionPane.WARNING_MESSAGE,
                                                     null,
                                                     options,
                                                     defaultOption);

            // don't do anything
            if (input == JOptionPane.CANCEL_OPTION || input == JOptionPane.CLOSED_OPTION) {
                return;
            }

            if (input == JOptionPane.YES_OPTION) {
                boolean success = performSave();

                // cancel the closing of the window to prevent losing saved changes
                if (!success) {
                    return;
                }
            }

            // close the window normally
            dispose();
            System.exit(0);
        }

        // EFFECTS: saves all the current boards and returns whether the
        //          saving process was successful
        private boolean performSave() {
            try {
                jsonWriter.open();
            } catch (IOException ex) {
                Popup.error(ProjectSelectionMenu.this,
                            "Failed to open the save file!",
                            "Error while saving");

                return false;
            }

            jsonWriter.writeBoards(kanbanBoards);
            jsonWriter.close();

            return true;
        }
    }
}
