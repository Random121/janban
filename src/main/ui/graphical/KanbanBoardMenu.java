package ui.graphical;

import model.Card;
import model.CardType;
import model.Column;
import model.KanbanBoard;
import model.exceptions.DuplicateColumnException;
import model.exceptions.NegativeStoryPointsException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.concurrent.ThreadLocalRandom;

// This class represents the kanban board menu for the Janban graphical app.
public class KanbanBoardMenu extends JFrame {
    private static final Dimension FRAME_DIM = new Dimension(1000, 700);

    private final KanbanBoard board;

    private ColumnsScrollPane columnScrollPane;

    // EFFECTS: Creates the project selection menu with a title, kanban board,
    //          and sets up the menu.
    public KanbanBoardMenu(KanbanBoard board, JFrame parentFrame) {
        super("Janban | Viewing: " + board.getName());

        this.board = board;

        setupStyle();
        setupButtons();
        setupColumns();

        pack();

        // place frame at center of screen
        setLocationRelativeTo(parentFrame);
        setVisible(true);
    }

    // MODIFIES: this
    // EFFECTS: Configure the styling of this menu.
    private void setupStyle() {
        setLayout(new BorderLayout(10, 0));

        setPreferredSize(FRAME_DIM);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
    }

    // MODIFIES: this
    // EFFECTS: Creates and places the buttons for the menu.
    private void setupButtons() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(3, 3));

        final Dimension BUTTON_DIM = new Dimension(30, 20);

        // weird ordering of buttons to make the buttons be grouped by category column-wise
        createButton(buttonPanel, "Add Column", BUTTON_DIM, new AddColumnButtonListener());
        createButton(buttonPanel, "Add Card", BUTTON_DIM, new AddCardButtonListener());
        createButton(buttonPanel, "View Card", BUTTON_DIM, new ViewCardButtonListener());
        createButton(buttonPanel, "Edit Column", BUTTON_DIM, new EditColumnButtonListener());
        createButton(buttonPanel, "Edit Card", BUTTON_DIM, new EditCardButtonListener());
        createButton(buttonPanel, "View Statistics", BUTTON_DIM, new ViewStatisticsButtonListener());
        createButton(buttonPanel, "Remove Column", BUTTON_DIM, new RemoveColumnButtonListener());
        createButton(buttonPanel, "Remove Card", BUTTON_DIM, new RemoveCardButtonListener());
        createButton(buttonPanel, "Search", BUTTON_DIM, new SearchButtonListener());

        add(buttonPanel, BorderLayout.NORTH);
    }

    // MODIFIES: this
    // EFFECTS: Creates the column scroll pane for the menu.
    private void setupColumns() {
        columnScrollPane = new ColumnsScrollPane(board);
        columnScrollPane.setPreferredSize(FRAME_DIM);
        columnScrollPane.syncAll();

        add(columnScrollPane, BorderLayout.CENTER);
    }

    // MODIFIES: parent
    // EFFECTS: Creates a new button and places it in parent.
    private void createButton(Container parent, String text, Dimension size, ActionListener listener) {
        JButton button = new JButton(text);
        button.setMinimumSize(size);
        // removes that ugly box around the button text
        button.setFocusable(false);
        button.addActionListener(listener);

        parent.add(button);
    }

    private class AddColumnButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            Column newColumn = new Column(Integer.toString(ThreadLocalRandom.current().nextInt()));

            try {
                board.addColumn(newColumn);
            } catch (DuplicateColumnException ex) {
                throw new RuntimeException(ex);
            }

            columnScrollPane.syncAll();
        }
    }

    private class EditColumnButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {

        }
    }

    private class RemoveColumnButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            board.removeColumn(board.getColumn(board.getColumnCount() - 1));

            columnScrollPane.syncAll();
        }
    }

    private class AddCardButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                Card newCard = new Card("Leo", "", "", CardType.ISSUE, new HashSet<>(), 10);
                board.moveCard(newCard,
                               board.getColumn(ThreadLocalRandom.current().nextInt(0, board.getColumnCount())));

                columnScrollPane.syncAll();
            } catch (NegativeStoryPointsException ex) {
                throw new RuntimeException(ex);
            }

        }
    }

    private class EditCardButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {

        }
    }

    private class RemoveCardButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            Card selectedCard = columnScrollPane.getSelection();

            if (selectedCard == null) {
                return;
            }

            Column cardColumn = selectedCard.getContainingColumn();

            // this shouldn't happen but we'll just ignore it
            if (cardColumn == null) {
                return;
            }

            cardColumn.removeCard(selectedCard);

            columnScrollPane.syncAll();
        }
    }

    private class ViewCardButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            Card selection = columnScrollPane.getSelection();

            if (selection == null) {
                return;
            }

            System.out.println(selection.getTitle());
        }
    }

    private class ViewStatisticsButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {

        }
    }

    private class SearchButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {

        }
    }
}
