package model;

import model.exceptions.DuplicateColumnException;
import model.exceptions.EmptyColumnNameException;

// This class represents a project with a name, description,
// and a corresponding kanban board.
public class Project {
    private final String name;
    private final String description;
    private final KanbanBoard kanbanBoard;

    // EFFECTS: constructs a new Project with a name, description, and the name of the completed column
    //          throws a DuplicateColumnException if the completedColumnName is already used
    //          throws an EmptyColumnNameException if the completedColumnName is empty
    public Project(String name,
                   String description,
                   String completedColumnName) throws DuplicateColumnException, EmptyColumnNameException {
        this.name = name;
        this.description = description;

        this.kanbanBoard = new KanbanBoard(completedColumnName);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public KanbanBoard getKanbanBoard() {
        return kanbanBoard;
    }
}
