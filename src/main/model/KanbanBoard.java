package model;

import model.exceptions.*;
import org.json.JSONArray;
import org.json.JSONObject;
import persistence.JsonSerializable;

import java.util.ArrayList;
import java.util.List;

// This class represents a kanban board with a name and description that contains
// columns that each represent a stage within a workflow.
public class KanbanBoard implements JsonSerializable {
    public static final String DEFAULT_BACKLOG_COLUMN_NAME = "Backlog";
    public static final String DEFAULT_WIP_COLUMN_NAME = "In Progress";

    private final String name;
    private final String description;

    private final List<Column> columns;
    private final String completedColumnName;

    // This should be updated every time a column is added or removed
    private Column completedColumn;

    // EFFECTS: constructs a KanbanBoard with a name, description, no columns,
    //          and sets the name of the completed column to completedColumnName.
    public KanbanBoard(String name, String description, String completedColumnName) {
        this.name = name;
        this.description = description;

        this.columns = new ArrayList<>();
        this.completedColumnName = completedColumnName;
        this.completedColumn = null;
    }

    // MODIFIES: this
    // EFFECTS: generates and adds the default backlog, in progress, and
    //          completed column for this board.
    //          throws an DuplicateColumnException if there is already a column with the same name.
    public void addDefaultColumns() throws DuplicateColumnException {
        Column backlog = new Column(DEFAULT_BACKLOG_COLUMN_NAME);
        Column inProgress = new Column(DEFAULT_WIP_COLUMN_NAME);

        // These two calls should never throw an exception.
        addColumn(backlog);
        addColumn(inProgress);

        Column completed = new Column(completedColumnName);

        addColumn(completed);
    }

    // MODIFIES: this
    // EFFECTS: adds a new column to this board.
    //          throws an DuplicateColumnException if there is already a column with the same name.
    public void addColumn(Column column) throws DuplicateColumnException {
        if (hasColumnWithName(column.getName())) {
            throw new DuplicateColumnException(column.getName());
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
    // EFFECTS: edits the name of an existing column.
    //          throws an DuplicateColumnException if there is already a column with the same name.
    public void editColumnName(Column column, String newName) throws DuplicateColumnException {
        if (!columns.contains(column)) {
            return;
        }

        // Set new name to default if necessary
        String newNameOrDefault = !newName.isBlank() ? newName : Column.DEFAULT_COLUMN_NAME;

        if (hasColumnWithName(newNameOrDefault)) {
            throw new DuplicateColumnException(newNameOrDefault);
        }

        if (column == completedColumn) {
            completedColumn = null;
        }

        if (newNameOrDefault.equals(completedColumnName)) {
            completedColumn = column;
        }

        column.setName(newNameOrDefault);
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

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    // EFFECTS: returns the JSON representation of this kanban board and its columns
    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("name", name);
        json.put("description", description);
        json.put("completedColumnName", completedColumnName);
        json.put("columns", columnsToJson());
        return json;
    }

    // EFFECTS: returns the JSON representation of the columns in this board
    private JSONArray columnsToJson() {
        JSONArray jsonArray = new JSONArray();

        for (Column column : columns) {
            jsonArray.put(column.toJson());
        }

        return jsonArray;
    }
}
