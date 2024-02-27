package persistence;

import model.*;
import model.exceptions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class KanbanJsonWriterTest extends JsonTest {
    private KanbanBoardList boards;

    @BeforeEach
    public void setup() {
        boards = new KanbanBoardList();
    }

    @Test
    public void testOpenInvalidFileException() {
        final String TEST_FILE = "./data/tests/write/invalid|file?.json";

        KanbanJsonWriter writer = new KanbanJsonWriter(TEST_FILE);

        try {
            writer.open();
            fail("An exception should have been thrown");
        } catch (IOException e) {
            // This exception should have been thrown
        }
    }

    @Test
    public void testWriteBoardsEmptyNoException() {
        final String TEST_FILE = "./data/tests/write/testWriteBoardsEmpty.json";

        KanbanJsonWriter writer = new KanbanJsonWriter(TEST_FILE);

        try {
            writer.open();
        } catch (IOException e) {
            fail("An exception should not have been thrown");
        }

        writer.writeBoards(boards);
        writer.close();

        KanbanJsonReader reader = new KanbanJsonReader(TEST_FILE);
        KanbanBoardList readBoards = null;

        try {
            readBoards = reader.read();
        } catch (IOException | CorruptedSaveDataException e) {
            fail("An exception should not have been thrown");
        }

        assertTrue(readBoards.isEmpty());
    }

    @Test
    public void testWriteBoardsNoException() {
        final String TEST_FILE = "./data/tests/write/testWriteBoards.json";

        KanbanJsonWriter writer = new KanbanJsonWriter(TEST_FILE);
        KanbanBoard board1 = new KanbanBoard("Kanban Board 1", "Kanban board description 1", "Done");
        KanbanBoard board2 = new KanbanBoard("Kanban Board 2", "Kanban board description 2", "Finished");

        try {
            Column column = new Column("A column");
            Set<String> cardTags = new HashSet<>() {{
                add("tag1");
                add("tag2");
            }};
            Card card = new Card("My card", "My card description", "John Doe", CardType.ISSUE, cardTags, 5);

            board2.addColumn(column);
            board2.moveCard(card, column);
        } catch (DuplicateColumnException | EmptyColumnNameException | EmptyCardTitleException |
                 NegativeStoryPointsException e) {
            fail("An exception should not have been thrown");
        }

        boards.addBoard(board1);
        boards.addBoard(board2);

        try {
            writer.open();
        } catch (IOException e) {
            fail("An exception should not have been thrown");
        }

        writer.writeBoards(boards);
        writer.close();

        KanbanJsonReader reader = new KanbanJsonReader(TEST_FILE);
        KanbanBoardList readBoards = null;

        try {
            readBoards = reader.read();
        } catch (IOException | CorruptedSaveDataException e) {
            fail("An exception should not have been thrown");
        }

        assertEquals(2, readBoards.size());

        KanbanBoard readBoard1 = readBoards.getBoard(0);
        KanbanBoard readBoard2 = readBoards.getBoard(1);

        assertKanbanBoardEqual(board1, readBoard1);
        assertKanbanBoardEqual(board2, readBoard2);
    }
}
