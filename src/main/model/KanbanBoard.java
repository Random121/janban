package model;

import model.exceptions.DuplicateColumnException;

import java.util.ArrayList;
import java.util.List;

// This class represents a kanban board which contains
// columns that each represent a stage within a workflow.
public class KanbanBoard {
    private List<Column> columns;
    private String completedColumnName;
    private Column completedColumn; // This should be updated every time a column is added or removed

    public KanbanBoard(String completedColumnName) {
        this.columns = new ArrayList<>();
        this.completedColumnName = completedColumnName;

        // TODO
        // generate default columns

        // TODO: set this later to be the generated completed column
        this.completedColumn = null;
    }

    // MODIFIES: this
    // EFFECTS: adds a new column to this board,
    //          there must not already be a column
    //          with the same name
    public void addColumn(Column column) {
        // TODO
    }

    // MODIFIES: this
    // EFFECTS: removes a new column to this board
    public void removeColumn(Column column) {
        // TODO
    }

    // EFFECTS: get the columns of this board
    public void getColumns(Column column) {
        // TODO
    }

    // MODIFIES: this
    // EFFECTS: moves an existing card to a different
    //          column
    public void moveCard(Card card, Column newColumn) {
        // TODO
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
        // TODO
        return 0;
    }

    // EFFECTS: gets the total number of completed cards within the board
    public int getCompletedCardCount() {
        return 0;
    }

    // EFFECTS: gets the total number of cards within the board
    public int getTotalCardCount() {
        return 0;
    }
}
