package model;

// This class represents a project with a name, description,
// and a corresponding kanban board.
public class Project {
    private String name;
    private String description;
    private KanbanBoard kanbanBoard;

    public Project(String name, String description, String completedColumnName) {
        this.name = name;
        this.description = description;

        this.kanbanBoard = new KanbanBoard(completedColumnName);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public KanbanBoard getKanbanBoard() {
        return kanbanBoard;
    }
}
