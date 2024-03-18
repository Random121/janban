package ui.graphical;

import model.*;
import model.exceptions.DuplicateColumnException;
import model.exceptions.NegativeStoryPointsException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

// This class represents the kanban board menu for the Janban graphical app.
public class KanbanBoardMenu extends JFrame {
    private static final Dimension FRAME_DIMENSIONS = new Dimension(1000, 700);
    private static final CardType[] CARD_TYPES = {CardType.USER_STORY, CardType.ISSUE, CardType.TASK};

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

        setPreferredSize(FRAME_DIMENSIONS);

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
        createButton(buttonPanel, "Filter", BUTTON_DIM, new FilterButtonListener("Filter", "Clear Filter"));

        add(buttonPanel, BorderLayout.NORTH);
    }

    // MODIFIES: this
    // EFFECTS: Creates the column scroll pane for the menu.
    private void setupColumns() {
        columnScrollPane = new ColumnsScrollPane(board);
        columnScrollPane.setPreferredSize(FRAME_DIMENSIONS);
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

    // EFFECTS: Creates a popup window which allows the user to select a column
    //          and is returned.
    private Column createColumnSelectionPopup() {
        List<Column> columns = board.getColumns();

        if (columns.isEmpty()) {
            return null;
        }

        String[] columnNames = columns.stream().map(Column::getName).toArray(String[]::new);

        String input = (String) JOptionPane.showInputDialog(KanbanBoardMenu.this,
                                                            "Column:",
                                                            "Select a column",
                                                            JOptionPane.PLAIN_MESSAGE,
                                                            null,
                                                            columnNames,
                                                            columnNames[0]);

        if (input == null || input.isBlank()) {
            return null;
        }

        for (Column column : columns) {
            if (input.equals(column.getName())) {
                return column;
            }
        }

        return null;
    }

    // EFFECTS: parses a string of comma separated keywords
    private Set<String> parseKeywordsFromString(String keywordString) {
        // To make the search feature work properly, all tags have to be lowercase.
        String[] tagsArray = keywordString.trim().toLowerCase().split(",");
        Set<String> tags = new HashSet<>();

        // We also have to remove extra whitespace in case if the user typed
        // "tag1, tag2" instead of "tag1,tag2".
        for (String tag : tagsArray) {
            tags.add(tag.trim());
        }

        return tags;
    }

    private class AddColumnButtonListener implements ActionListener {

        // MODIFIES: this
        // EFFECTS: Creates a popup that asks for user input to create a new column.
        @Override
        public void actionPerformed(ActionEvent e) {
            JTextField nameField = new JTextField();

            JComponent[] inputs = {
                    new JLabel("Name:"), nameField
            };

            boolean success = Popup.creatingPopup(KanbanBoardMenu.this, inputs, "Create a new column");

            if (!success) {
                return;
            }

            createAndAddNewColumn(nameField.getText());
            columnScrollPane.syncAll();
        }

        // MODIFIES: this
        // EFFECTS: Creates and adds a column to the kanban board.
        private void createAndAddNewColumn(String name) {
            Column newColumn = new Column(name);

            try {
                board.addColumn(newColumn);
            } catch (DuplicateColumnException ex) {
                Popup.error(KanbanBoardMenu.this,
                            ex.getMessage(),
                            "Error while creating a new column");
            }
        }
    }

    private class EditColumnButtonListener implements ActionListener {

        // MODIFIES: this
        // EFFECTS: Creates a popup that asks for user input to edit a column.
        @Override
        public void actionPerformed(ActionEvent e) {
            Column chosenColumn = createColumnSelectionPopup();

            if (chosenColumn == null) {
                return;
            }

            createColumnEditPopup(chosenColumn);
            columnScrollPane.syncAll();
        }

        // EFFECTS: Creates a popup asking for the name of the column.
        private void createColumnEditPopup(Column column) {
            JTextField nameField = new JTextField(column.getName());

            JComponent[] inputs = {
                    new JLabel("Name:"), nameField
            };

            boolean success = Popup.editingPopup(KanbanBoardMenu.this, inputs, "Editing column");

            if (!success) {
                return;
            }

            editColumnName(column, nameField.getText());
        }

        // MODIFIES: this
        // EFFECTS: Edits the name of a column.
        private void editColumnName(Column column, String name) {
            try {
                board.editColumnName(column, name);
            } catch (DuplicateColumnException ex) {
                Popup.error(KanbanBoardMenu.this,
                            ex.getMessage(),
                            "Error while editing column");
            }
        }
    }

    private class RemoveColumnButtonListener implements ActionListener {

        // MODIFIES: this
        // EFFECTS: Removes the selected column.
        @Override
        public void actionPerformed(ActionEvent e) {
            Column chosenColumn = createColumnSelectionPopup();

            if (chosenColumn == null) {
                return;
            }

            board.removeColumn(chosenColumn);
            columnScrollPane.syncAll();
        }
    }

    private class AddCardButtonListener implements ActionListener {

        // EFFECTS: Handles the creation of a new card.
        @Override
        public void actionPerformed(ActionEvent e) {
            if (board.getColumns().isEmpty()) {
                Popup.error(KanbanBoardMenu.this,
                            "You can't add any cards when there are no columns!",
                            "Error while creating a new card");
                return;
            }

            JTextField titleField = new JTextField();

            JTextArea descriptionTextArea = new JTextArea(4, 40);
            descriptionTextArea.setWrapStyleWord(true);
            descriptionTextArea.setLineWrap(true);

            JScrollPane descriptionField = new JScrollPane(descriptionTextArea);

            JTextField assigneeField = new JTextField();

            JComboBox<CardType> cardTypeField = new JComboBox<>(CARD_TYPES);

            JTextField tagsField = new JTextField();

            SpinnerModel spinnerModel = new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1);
            JSpinner storyPointsField = new JSpinner(spinnerModel);

            createAddCardPopup(titleField,
                               descriptionTextArea, descriptionField,
                               assigneeField, cardTypeField,
                               tagsField, storyPointsField);

            columnScrollPane.syncAll();
        }

        // EFFECTS: Creates a popup that asks for all the fields needed to create a new card.
        private void createAddCardPopup(JTextField titleField,
                                        JTextArea descriptionTextArea,
                                        JScrollPane descriptionField,
                                        JTextField assigneeField,
                                        JComboBox<CardType> cardTypeField,
                                        JTextField tagsField,
                                        JSpinner storyPointsField) {
            JComponent[] inputs = {
                    new JLabel("Title:"), titleField,
                    new JLabel("Description:"), descriptionField,
                    new JLabel("Assignee:"), assigneeField,
                    new JLabel("Type:"), cardTypeField,
                    new JLabel("Tags:"), tagsField,
                    new JLabel("Story points:"), storyPointsField
            };

            boolean success = Popup.creatingPopup(KanbanBoardMenu.this, inputs, "Create a new card");

            if (!success) {
                return;
            }

            addNewCard(titleField.getText(),
                       descriptionTextArea.getText(),
                       assigneeField.getText(),
                       (CardType) cardTypeField.getSelectedItem(),
                       parseKeywordsFromString(tagsField.getText()),
                       (int) storyPointsField.getValue());
        }

        // MODIFIES: this
        // EFFECTS: Creates and adds a new card to the kanban board.
        private void addNewCard(String title,
                                String description,
                                String assignee,
                                CardType type,
                                Set<String> tags,
                                int storyPoints) {
            try {
                Card newCard = new Card(title, description, assignee, type, tags, storyPoints);
                Column firstColumn = board.getColumn(0);

                board.moveCard(newCard, firstColumn);
            } catch (NegativeStoryPointsException ex) {
                Popup.error(KanbanBoardMenu.this,
                            "Failed to create the card! " + ex.getMessage(),
                            "Error while creating a new card");
            }
        }
    }

    private class EditCardButtonListener implements ActionListener {

        // EFFECTS: Handles the editing of an existing card in the kanban board.
        @Override
        public void actionPerformed(ActionEvent e) {
            Card card = columnScrollPane.getSelection();

            if (card == null) {
                return;
            }

            JTextField titleField = new JTextField(card.getTitle());

            JTextArea descriptionTextArea = new JTextArea(card.getDescription(), 4, 40);
            descriptionTextArea.setWrapStyleWord(true);
            descriptionTextArea.setLineWrap(true);

            JScrollPane descriptionField = new JScrollPane(descriptionTextArea);

            JTextField assigneeField = new JTextField(card.getAssignee());

            JComboBox<CardType> cardTypeField = new JComboBox<>(CARD_TYPES);
            cardTypeField.setSelectedItem(card.getType());

            JTextField tagsField = new JTextField(String.join(", ", card.getTags()));

            SpinnerModel spinnerModel = new SpinnerNumberModel(card.getStoryPoints(), 0, Integer.MAX_VALUE, 1);
            JSpinner storyPointsField = new JSpinner(spinnerModel);

            String[] columnNames = board.getColumns().stream().map(Column::getName).toArray(String[]::new);
            JComboBox<String> columnField = new JComboBox<>(columnNames);
            columnField.setSelectedItem(card.getContainingColumn().getName());

            createEditCardPopup(card, titleField, descriptionTextArea, descriptionField, assigneeField, cardTypeField,
                                tagsField, storyPointsField, columnField);

            columnScrollPane.syncAll();
        }

        // EFFECTS: Creates a popup that asks for all the fields needed to edit a card.
        private void createEditCardPopup(Card card,
                                         JTextField titleField,
                                         JTextArea descriptionTextArea,
                                         JScrollPane descriptionField,
                                         JTextField assigneeField,
                                         JComboBox<CardType> cardTypeField,
                                         JTextField tagsField,
                                         JSpinner storyPointsField,
                                         JComboBox<String> columnField) {
            JComponent[] inputs = {
                    new JLabel("Title:"), titleField,
                    new JLabel("Description:"), descriptionField,
                    new JLabel("Assignee:"), assigneeField,
                    new JLabel("Type:"), cardTypeField,
                    new JLabel("Tags:"), tagsField,
                    new JLabel("Story points:"), storyPointsField,
                    new JLabel("Column:"), columnField
            };

            boolean success = Popup.editingPopup(KanbanBoardMenu.this, inputs, "Editing card");

            if (!success) {
                return;
            }

            Column newColumn = getColumnFromName((String) columnField.getSelectedItem());

            editCard(card, titleField.getText(),
                     descriptionTextArea.getText(), assigneeField.getText(),
                     (CardType) cardTypeField.getSelectedItem(), parseKeywordsFromString(tagsField.getText()),
                     (int) storyPointsField.getValue(), newColumn);
        }

        // MODIFIES: this
        // EFFECTS: Edits an existing card within the kanban board.
        private void editCard(Card card,
                              String title,
                              String description,
                              String assignee,
                              CardType type,
                              Set<String> tags,
                              int storyPoints,
                              Column column) {
            card.setTitle(title);
            card.setDescription(description);
            card.setAssignee(assignee);
            card.setType(type);
            card.setTags(tags);

            try {
                card.setStoryPoints(storyPoints);
            } catch (NegativeStoryPointsException ex) {
                Popup.error(KanbanBoardMenu.this,
                            "Failed to edit the card! " + ex.getMessage(),
                            "Error while creating a new card");
            }

            if (column != null) {
                board.moveCard(card, column);
            }
        }

        // EFFECTS: Gets a column from its name.
        private Column getColumnFromName(String name) {
            for (Column column : board.getColumns()) {
                if (name.equals(column.getName())) {
                    return column;
                }
            }

            return null;
        }
    }

    private class RemoveCardButtonListener implements ActionListener {

        // MODIFIES: this
        // EFFECTS: Removes the currently selected card.
        @Override
        public void actionPerformed(ActionEvent e) {
            Card selectedCard = columnScrollPane.getSelection();

            if (selectedCard == null) {
                return;
            }

            Column cardColumn = selectedCard.getContainingColumn();

            // this shouldn't happen so we'll just ignore it
            if (cardColumn == null) {
                return;
            }

            cardColumn.removeCard(selectedCard);

            columnScrollPane.syncAll();
        }
    }

    private class ViewCardButtonListener implements ActionListener {

        // EFFECTS: Handles displaying the currently selected card.
        @Override
        public void actionPerformed(ActionEvent e) {
            Card card = columnScrollPane.getSelection();

            if (card == null) {
                return;
            }

            JTextField titleField = createDisplayTextField(card.getTitle());

            JTextArea descriptionTextArea = new JTextArea(card.getDescription(), 4, 40);
            descriptionTextArea.setEditable(false);
            descriptionTextArea.setWrapStyleWord(true);
            descriptionTextArea.setLineWrap(true);

            JScrollPane descriptionField = new JScrollPane(descriptionTextArea);

            JTextField assigneeField = createDisplayTextField(card.getAssignee());
            JTextField cardTypeField = createDisplayTextField(card.getType().toString());
            JTextField tagsField = createDisplayTextField(String.join(", ", card.getTags()));
            JTextField storyPointsField = createDisplayTextField(Integer.toString(card.getStoryPoints()));

            createDisplayPopup(titleField, descriptionField, assigneeField, cardTypeField, tagsField, storyPointsField);
        }

        // EFFECTS: Creates a popup that displays all the fields of the currently selected card.
        private void createDisplayPopup(JTextField titleField,
                                        JScrollPane descriptionField,
                                        JTextField assigneeField,
                                        JTextField cardTypeField,
                                        JTextField tagsField,
                                        JTextField storyPointsField) {
            JComponent[] displayFields = {
                    new JLabel("Title:"), titleField,
                    new JLabel("Description:"), descriptionField,
                    new JLabel("Assignee:"), assigneeField,
                    new JLabel("Type:"), cardTypeField,
                    new JLabel("Tags:"), tagsField,
                    new JLabel("Story points:"), storyPointsField
            };

            JOptionPane.showMessageDialog(KanbanBoardMenu.this,
                                          displayFields,
                                          "Viewing card",
                                          JOptionPane.PLAIN_MESSAGE);
        }

        // EFFECTS: Creates a text field that is read only.
        private JTextField createDisplayTextField(String text) {
            JTextField field = new JTextField(text);
            field.setEditable(false);
            return field;
        }
    }

    private class ViewStatisticsButtonListener implements ActionListener {

        // EFFECTS: Creates a popup that displays some statistics about the current board.
        @Override
        public void actionPerformed(ActionEvent e) {
            int totalCardCount = board.getCardCount(true);
            int inProgressCardCount = board.getCardCount(false);
            int completedCardCount = totalCardCount - inProgressCardCount;

            int totalStoryPoints = board.getTotalStoryPoints();
            int completedStoryPoints = board.getCompletedStoryPoints();
            int inProgressStoryPoints = totalStoryPoints - completedStoryPoints;

            JComponent[] displayFields = {
                    new JLabel("Cards: " + totalCardCount),
                    new JLabel(" - In progress: " + inProgressCardCount),
                    new JLabel(" - Completed: " + completedCardCount),
                    (JComponent) Box.createRigidArea(new Dimension(0, 10)),
                    new JLabel("Story points: " + totalStoryPoints),
                    new JLabel(" - In progress: " + inProgressStoryPoints),
                    new JLabel(" - Completed: " + completedStoryPoints),
            };

            JOptionPane.showMessageDialog(KanbanBoardMenu.this,
                                          displayFields,
                                          "Board statistics",
                                          JOptionPane.PLAIN_MESSAGE);
        }
    }

    private class FilterButtonListener implements ActionListener {
        private static final String FILTER_BY_KEYWORDS = "Filter by keywords";
        private static final String FILTER_BY_TYPE = "Filter by type";

        private final String defaultText;
        private final String filteringText;

        private boolean filtering;

        // EFFECTS: Creates a new FilterButtonListener with a default text, filtering text,
        //          and a flag showing it is not filtering.
        public FilterButtonListener(String defaultText, String filteringText) {
            this.defaultText = defaultText;
            this.filteringText = filteringText;
            this.filtering = false;
        }

        // EFFECTS: Handles filtering cards of the kanban board.
        @Override
        public void actionPerformed(ActionEvent e) {
            filtering = !filtering;

            JButton button = (JButton) e.getSource();

            if (filtering) {
                boolean success = createFilterPopup();

                // the user cancelled the action of filtering
                if (!success) {
                    filtering = false;
                }
            } else {
                columnScrollPane.setFilteringGetter(null);
            }

            button.setText(filtering ? filteringText : defaultText);
            columnScrollPane.syncAll();
        }

        // EFFECTS: Creates a popup that handles the filtering.
        private boolean createFilterPopup() {
            String filterType = createFilterTypeSelectionPopup();

            switch (filterType) {
                case FILTER_BY_KEYWORDS:
                    return createFilterByKeywordsPopup();
                case FILTER_BY_TYPE:
                    return createFilterByTypePopup();
                default:
                    return false;
            }
        }

        // EFFECTS: Creates a popup to specify which type of filter to use.
        private String createFilterTypeSelectionPopup() {
            final String[] options = {FILTER_BY_KEYWORDS, FILTER_BY_TYPE};
            final String defaultOption = options[0];

            return (String) JOptionPane.showInputDialog(KanbanBoardMenu.this,
                                                        "Filter type:",
                                                        "Select a filter type",
                                                        JOptionPane.PLAIN_MESSAGE,
                                                        null,
                                                        options,
                                                        defaultOption);
        }

        // MODIFIES: this
        // EFFECTS: Sets the current filtering to be based on keywords.
        private boolean createFilterByKeywordsPopup() {
            JTextField keywordsField = new JTextField();

            JComponent[] inputs = {
                    new JLabel("Keywords:"), keywordsField
            };

            boolean success = Popup.editingPopup(KanbanBoardMenu.this, inputs, "Searching by keywords");

            if (!success) {
                return false;
            }

            Set<String> keywords = parseKeywordsFromString(keywordsField.getText());

            columnScrollPane.setFilteringGetter(column -> column.getCardsWithQuery(keywords));

            return true;
        }


        // MODIFIES: this
        // EFFECTS: Sets the current filtering to be based on the card type.
        private boolean createFilterByTypePopup() {
            JComboBox<CardType> cardTypeField = new JComboBox<>(CARD_TYPES);

            JComponent[] inputs = {
                    new JLabel("Type:"), cardTypeField
            };

            boolean success = Popup.editingPopup(KanbanBoardMenu.this, inputs, "Searching by keywords");

            if (!success) {
                return false;
            }

            columnScrollPane.setFilteringGetter(
                    column -> column.getCardsOfType((CardType) cardTypeField.getSelectedItem())
            );

            return true;
        }
    }
}
