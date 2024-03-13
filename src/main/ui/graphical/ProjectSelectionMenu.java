package ui.graphical;

import model.KanbanBoard;
import model.KanbanBoardList;
import persistence.KanbanJsonWriter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// This class represents the project selection menu for the Janban graphical app.
public class ProjectSelectionMenu extends JFrame {
    private static final Dimension FRAME_DIM = new Dimension(800, 600);

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

        setPreferredSize(FRAME_DIM);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
    }

    // MODIFIES: this
    // EFFECTS: Creates and places the new project and load project buttons for the menu.
    private void setupButtons() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

        JButton newProjectButton = new JButton("New Project");
        newProjectButton.setPreferredSize(new Dimension(25, 20));
        // removes that ugly box around the button text
        newProjectButton.setFocusable(false);
        newProjectButton.addActionListener(new NewProjectButtonListener());

        JButton loadProjectButton = new JButton("Load Project");
        loadProjectButton.setPreferredSize(new Dimension(25, 20));
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
        final int WIDTH = 600;
        final int HEIGHT = 400;

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
        scrollPane.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        scrollPane.setMaximumSize(new Dimension(WIDTH, HEIGHT));

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

    private int testCounter = 0;

    private class NewProjectButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            KanbanBoard newBoard = new KanbanBoard("Test" + testCounter,
                                                   "This is a description" + testCounter,
                                                   "Done");

            kanbanBoards.addBoard(newBoard);
            synchronizeBoardModels();

            testCounter++;
        }
    }

    private class LoadProjectButtonListener implements ActionListener {
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
}
