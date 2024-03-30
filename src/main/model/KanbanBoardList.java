package model;

import org.json.JSONArray;
import org.json.JSONObject;
import persistence.JsonSerializable;

import java.util.ArrayList;
import java.util.List;

public class KanbanBoardList implements JsonSerializable {
    private final List<KanbanBoard> boards;

    // EFFECTS: constructs a new empty list of kanban boards
    public KanbanBoardList() {
        boards = new ArrayList<>();
    }

    // MODIFIES: this
    // EFFECTS: adds a new kanban board
    public void addBoard(KanbanBoard board) {
        {
            String eventDescription = String.format("Adding kanban board '%s' to list", board.getName());
            EventLog.getInstance().logEvent(new Event(eventDescription));
        }

        this.boards.add(board);
    }

    public List<KanbanBoard> getBoards() {
        return boards;
    }

    // EFFECTS: gets the kanban board at the specified index
    public KanbanBoard getBoard(int index) {
        return boards.get(index);
    }

    // EFFECTS: returns whether there are no boards
    public boolean isEmpty() {
        return boards.isEmpty();
    }

    // EFFECTS; returns how many boards there are
    public int size() {
        return boards.size();
    }

    // EFFECTS: returns the JSON representation of this kanban board list
    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("boards", boardsToJson());
        return json;
    }

    // EFFECTS: returns the JSON representation of all the kanban boards
    private JSONArray boardsToJson() {
        JSONArray jsonArray = new JSONArray();

        for (KanbanBoard board : boards) {
            jsonArray.put(board.toJson());
        }

        return jsonArray;
    }
}
