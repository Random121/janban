package persistence;

import model.*;
import model.exceptions.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

public class KanbanJsonReaderTest extends JsonTest {

    @Test
    public void testReadBoardsInvalidFile() {
        final String TEST_FILE = "./data/tests/read/thisFileDoesNotExist.json";

        KanbanJsonReader reader = new KanbanJsonReader(TEST_FILE);

        try {
            reader.read();
            fail("An exception should have been thrown");
        } catch (IOException e) {
            // This exception should have been thrown
        } catch (CorruptedSaveDataException e) {
            fail("Wrong exception thrown");
        }
    }

    @Test
    public void testReadBoardsEmpty() {
        final String TEST_FILE = "./data/tests/read/testReadBoardsEmpty.json";

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
    public void testReadBoardsNoException() {
        final String TEST_FILE = "./data/tests/read/testReadBoards.json";

        KanbanJsonReader reader = new KanbanJsonReader(TEST_FILE);

        KanbanBoardList boards = new KanbanBoardList();

        KanbanBoard board1 = new KanbanBoard("Kanban Board 1", "Kanban board description 1", "Finished");
        KanbanBoard board2 = new KanbanBoard("Kanban Board 2", "Kanban board description 2", "Completed");

        try {
            board1.addDefaultColumns();

            Column column1 = new Column("Column 1");
            Column column2 = new Column("Column 2");

            Card card1 = new Card("My card",
                                  "My card description",
                                  "John Doe and Jane Doe",
                                  CardType.ISSUE,
                                  new HashSet<>() {{
                                      add("tag1");
                                      add("tag2");
                                  }},
                                  5);

            Card card2 = new Card("Another card",
                                  "Another card description",
                                  "Jane Doe",
                                  CardType.USER_STORY,
                                  new HashSet<>() {{
                                      add("tag3");
                                      add("tag4");
                                  }},
                                  666);

            board2.addColumn(column1);
            board2.addColumn(column2);

            board2.moveCard(card1, column1);
            board2.moveCard(card2, column1);
        } catch (DuplicateColumnException | EmptyColumnNameException | EmptyCardTitleException |
                 NegativeStoryPointsException e) {
            fail("An exception should not have been thrown");
        }

        boards.addBoard(board1);
        boards.addBoard(board2);

        try {
            KanbanBoardList readBoards = reader.read();

            assertEquals(2, readBoards.size());

            KanbanBoard readBoard1 = readBoards.getBoard(0);
            KanbanBoard readBoard2 = readBoards.getBoard(1);

            assertKanbanBoardEqual(board1, readBoard1);
            assertKanbanBoardEqual(board2, readBoard2);
        } catch (IOException | CorruptedSaveDataException e) {
            fail("An exception should not have been thrown");
        }
    }

    @Test
    public void testReadBoardsCorruptColumnEmptyName() {
        final String TEST_FILE = "./data/tests/read/testReadBoardsCorruptColumnEmptyName.json";

        KanbanJsonReader reader = new KanbanJsonReader(TEST_FILE);

        try {
            reader.read();
            fail("An exception should have been thrown");
        } catch (IOException e) {
            fail("Wrong exception thrown");
        } catch (CorruptedSaveDataException e) {
            // This exception should have been thrown
        }
    }

    @Test
    public void testReadBoardsCorruptCardNegativeStoryPoints() {
        final String TEST_FILE = "./data/tests/read/testReadBoardsCorruptCardNegativeStoryPoints.json";

        KanbanJsonReader reader = new KanbanJsonReader(TEST_FILE);

        try {
            reader.read();
            fail("An exception should have been thrown");
        } catch (IOException e) {
            fail("Wrong exception thrown");
        } catch (CorruptedSaveDataException e) {
            // This exception should have been thrown
        }
    }

    @Test
    public void testReadBoardsCorruptCardEmptyTitle() {
        final String TEST_FILE = "./data/tests/read/testReadBoardsCorruptCardEmptyTitle.json";

        KanbanJsonReader reader = new KanbanJsonReader(TEST_FILE);

        try {
            reader.read();
            fail("An exception should have been thrown");
        } catch (IOException e) {
            fail("Wrong exception thrown");
        } catch (CorruptedSaveDataException e) {
            // This exception should have been thrown
        }
    }

    @Test
    public void testReadBoardsCorruptBoardDuplicateColumn() {
        final String TEST_FILE = "./data/tests/read/testReadBoardsCorruptBoardDuplicateColumn.json";

        KanbanJsonReader reader = new KanbanJsonReader(TEST_FILE);

        try {
            reader.read();
            fail("An exception should have been thrown");
        } catch (IOException e) {
            fail("Wrong exception thrown");
        } catch (CorruptedSaveDataException e) {
            // This exception should have been thrown
        }
    }
}
