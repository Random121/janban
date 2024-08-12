package persistence;

import model.Event;
import model.EventLog;
import model.KanbanBoardList;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;

// This class represents a writer that saves a list of kanban boards
// to a local file as JSON.
public class KanbanJsonWriter {
    private static final int TAB_WIDTH = 4;
    private PrintWriter writer;
    private final String destinationFile;

    // EFFECTS: constructs a JSON writer for the destinationFile
    public KanbanJsonWriter(String destinationFile) {
        this.destinationFile = destinationFile;
    }

    // MODIFIES: this
    // EFFECTS: opens a print writer for the current file
    //          throws a IOException if the destination file cannot be opened for writing
    public void open() throws IOException {
        writer = new PrintWriter(destinationFile);
    }

    // MODIFIES: this
    // EFFECTS: closes the print writer for the current file
    public void close() {
        writer.close();
    }

    // MODIFIES: this
    // EFFECTS: writes the JSON representation of the kanban boards to file
    public void writeBoards(KanbanBoardList boards) {
        EventLog.getInstance().logEvent(new Event("Writing kanban boards to " + destinationFile));

        JSONObject json = boards.toJson();
        writeToFile(json.toString(TAB_WIDTH));
    }

    // MODIFIES: this
    // EFFECTS: writes a json string to the currently opened file
    private void writeToFile(String jsonString) {
        writer.print(jsonString);
    }
}
