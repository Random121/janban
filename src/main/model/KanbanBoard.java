package model;

import model.exceptions.*;

import java.util.ArrayList;
import java.util.List;

// This class represents a kanban board which contains
// columns that each represent a stage within a workflow.
public class KanbanBoard {
    private final List<Column> columns;
    private final String completedColumnName;

    // This should be updated every time a column is added or removed
    private Column completedColumn;

    public KanbanBoard(String completedColumnName) throws FailedToGenerateDefaultColumnsException {
        this.columns = new ArrayList<>();
        this.completedColumnName = completedColumnName;

        generateDefaultColumns();
    }

    // MODIFIES: this
    // EFFECTS: generates and adds the default backlog, in progress, and
    //          completed column for this board.
    private void generateDefaultColumns() throws FailedToGenerateDefaultColumnsException {
        Column backlog;
        Column inProgress;
        Column completed;

        try {
            backlog = new Column("Backlog");
            inProgress = new Column("In Progress");
            completed = new Column(completedColumnName);
        } catch (InvalidColumnNameException e) {
            throw new FailedToGenerateDefaultColumnsException(e.getMessage());
        }

        completedColumn = completed;

        columns.add(backlog);
        columns.add(inProgress);
        columns.add(completed);
    }

    // MODIFIES: this
    // EFFECTS: adds a new column to this board, throws an exception
    //          if there is already a column with the same name
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
    // EFFECTS: removes a column to this board
    public void removeColumn(Column column) {
        if (!columns.contains(column)) {
            return;
        }

        if (column == completedColumn) {
            completedColumn = null;
        }

        columns.remove(column);
    }

    // MODIFIES: this
    // EFFECTS: edits the name of an existing column
    public void editColumnName(Column column,
                               String newName) throws DuplicateColumnException, InvalidColumnNameException {
        if (!columns.contains(column)) {
            return;
        }

        if (hasColumnWithName(newName)) {
            throw new DuplicateColumnException("Column with name '" + newName + "' already exists");
        }

        column.setName(newName);
    }

    // EFFECTS: get the columns of this board
    public List<Column> getColumns() {
        return columns;
    }

    // EFFECTS: get the column which holds completed cards
    //          of this board, if it is null then it means
    //          that there are no columns in the board
    public Column getCompletedColumn() {
        return completedColumn;
    }

    // MODIFIES: this
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
    public boolean hasColumnWithName(String name) {
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
