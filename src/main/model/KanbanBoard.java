package model;

import model.exceptions.*;

import java.util.ArrayList;
import java.util.List;

// This class represents a kanban board which contains
// columns that each represent a stage within a workflow.
public class KanbanBoard {
    private static final String DEFAULT_BACKLOG_COLUMN_NAME = "Backlog";
    private static final String DEFAULT_WIP_COLUMN_NAME = "In Progress";

    private final List<Column> columns;
    private final String completedColumnName;

    // This should be updated every time a column is added or removed
    private Column completedColumn;

    // EFFECTS: constructs a KanbanBoard with three default columns (backlog, in progress, and complete),
    //          and sets the name of the compelte column to what is specified.
    //          throws a DuplicateColumnException if the complete column name is a duplicate.
    //          throws a EmptyColumnNameException if no complete column name is provided.
    public KanbanBoard(String completedColumnName) throws DuplicateColumnException, EmptyColumnNameException {
        this.columns = new ArrayList<>();
        this.completedColumnName = completedColumnName;
        this.completedColumn = null;

        addDefaultColumns();
    }

    // MODIFIES: this
    // EFFECTS: generates and adds the default backlog, in progress, and
    //          completed column for this board.
    //          throws an EmptyColumnNameException if the complete column name is empty
    //          throws an DuplicateColumnException if the name of the complete column is a duplicate.
    private void addDefaultColumns() throws EmptyColumnNameException, DuplicateColumnException {
        try {
            Column backlog = new Column(DEFAULT_BACKLOG_COLUMN_NAME);
            Column inProgress = new Column(DEFAULT_WIP_COLUMN_NAME);

            addColumn(backlog);
            addColumn(inProgress);
        } catch (EmptyColumnNameException | DuplicateColumnException e) {
            // These two errors should never occur unless the code
            // has been messed up. If it does, we throw an error
            // since there is no way to recover from it.
            throw new Error(e);
        }

        Column completed = new Column(completedColumnName);

        addColumn(completed);
    }

    // MODIFIES: this
    // EFFECTS: adds a new column to this board.
    //          throws an DuplicateColumnException if there is already a column with the same name.
    public void addColumn(Column column) throws DuplicateColumnException {
        if (hasColumnWithName(column.getName())) {
            throw new DuplicateColumnException("Column with name '" + column.getName() + "' already exists");
        }

        if (column.getName().equals(completedColumnName)) {
            completedColumn = column;
        }

        columns.add(column);
    }

    // MODIFIES: this
    // EFFECTS: removes a column from this board
    public void removeColumn(Column column) {
        if (!columns.contains(column)) {
            return;
        }

        if (column == completedColumn) {
            completedColumn = null;
        }

        columns.remove(column);
    }

    // MODIFIES: this, column
    // EFFECTS: edits the name of an existing column
    public void editColumnName(Column column,
                               String newName) throws DuplicateColumnException, EmptyColumnNameException {
        if (!columns.contains(column)) {
            return;
        }

        if (hasColumnWithName(newName)) {
            throw new DuplicateColumnException("Column with name '" + newName + "' already exists");
        }

        if (column == completedColumn) {
            completedColumn = null;
        }

        if (newName.equals(completedColumnName)) {
            completedColumn = column;
        }

        column.setName(newName);
    }

    public Column getColumn(int index) {
        return columns.get(index);
    }

    public List<Column> getColumns() {
        return columns;
    }

    public int getColumnCount() {
        return columns.size();
    }

    // EFFECTS: get the column which holds completed cards
    //          of this board, if it is null then it means
    //          that there are no columns in the board
    public Column getCompletedColumn() {
        return completedColumn;
    }

    // MODIFIES: this, newColumn
    // EFFECTS: moves a card to a different column
    //          and removes it from its old column if it has one
    public void moveCard(Card card, Column newColumn) {
        if (!columns.contains(newColumn)) {
            return;
        }

        Column containingColumn = card.getContainingColumn();

        if (containingColumn != null) {
            containingColumn.removeCard(card);
        }

        newColumn.addCard(card);
    }

    // EFFECTS: returns whether a column with the given
    //          name already exists in this kanban board
    private boolean hasColumnWithName(String name) {
        for (Column column : columns) {
            if (column.getName().equals(name)) {
                return true;
            }
        }

        return false;
    }

    // EFFECTS: gets the total story points of
    //          all cards within this board
    public int getTotalStoryPoints() {
        int totalStoryPoints = 0;

        for (Column column : columns) {
            totalStoryPoints += column.getTotalStoryPoints();
        }

        return totalStoryPoints;
    }

    // EFFECTS: gets the total story points of
    //          all completed cards within this board
    public int getCompletedStoryPoints() {
        if (completedColumn == null) {
            return 0;
        }

        return completedColumn.getTotalStoryPoints();
    }

    // EFFECTS: gets the number of cards within the board
    //          with the option to include completed cards
    public int getCardCount(boolean includeCompleted) {
        int cardCount = 0;

        for (Column column : columns) {
            cardCount += column.getCards().size();
        }

        if (!includeCompleted && completedColumn != null) {
            cardCount -= completedColumn.getCards().size();
        }

        return cardCount;
    }
}
