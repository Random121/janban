package persistence;

import model.*;
import model.exceptions.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public class KanbanJsonReader {
    private final String sourceFile;

    // EFFECTS: constructs a JSON reader to load a list of kanban boards from file
    public KanbanJsonReader(String sourceFile) {
        this.sourceFile = sourceFile;
    }

    // EFFECTS: reads a list of kanban boards from file and returns it
    //          throws an IOException if an error occurs with reading the file
    //          throws an CorruptedSaveDataException if the save file has invalid data
    public KanbanBoardList read() throws IOException, CorruptedSaveDataException {
        String jsonString = readFile(sourceFile);
        JSONObject json = new JSONObject(jsonString);
        return readKanbanBoardList(json);
    }

    // EFFECTS: reads source file as string and returns it
    private String readFile(String source) throws IOException {
        StringBuilder contentBuilder = new StringBuilder();

        try (Stream<String> stream = Files.lines(Paths.get(source), StandardCharsets.UTF_8)) {
            stream.forEach(contentBuilder::append);
        }

        return contentBuilder.toString();
    }

    private KanbanBoardList readKanbanBoardList(JSONObject json) throws CorruptedSaveDataException {
        KanbanBoardList boards = new KanbanBoardList();
        JSONArray jsonBoards = json.getJSONArray("boards");

        for (Object jsonBoard : jsonBoards) {
            KanbanBoard board = readKanbanBoard((JSONObject) jsonBoard);
            boards.addBoard(board);
        }

        return boards;
    }

    private KanbanBoard readKanbanBoard(JSONObject json) throws CorruptedSaveDataException {
        String name = json.getString("name");
        String description = json.getString("description");
        String completedColumnName = json.getString("completedColumnName");

        KanbanBoard board = new KanbanBoard(name, description, completedColumnName);

        JSONArray jsonColumns = json.getJSONArray("columns");

        for (Object jsonColumn : jsonColumns) {
            Column column = readColumn((JSONObject) jsonColumn);

            try {
                board.addColumn(column);
            } catch (DuplicateColumnException e) {
                throw new CorruptedSaveDataException(e.getMessage());
            }
        }

        return board;
    }

    private Column readColumn(JSONObject json) throws CorruptedSaveDataException {
        String name = json.getString("name");

        Column column;

        try {
            column = new Column(name);
        } catch (EmptyColumnNameException e) {
            throw new CorruptedSaveDataException(e.getMessage());
        }

        JSONArray jsonCards = json.getJSONArray("cards");

        for (Object jsonCard : jsonCards) {
            Card card = readCard((JSONObject) jsonCard);

            column.addCard(card);
        }

        return column;
    }

    private Card readCard(JSONObject json) throws CorruptedSaveDataException {
        String title = json.getString("title");
        String description = json.getString("description");
        String assignee = json.getString("assignee");
        CardType type = json.getEnum(CardType.class, "type");
        Set<String> tags = new HashSet<>((List<String>) (Object) json.getJSONArray("tags").toList());
        int storyPoints = json.getInt("storyPoints");

        try {
            return new Card(title, description, assignee, type, tags, storyPoints);
        } catch (NegativeStoryPointsException | EmptyCardTitleException e) {
            throw new CorruptedSaveDataException(e.getMessage());
        }
    }
}
