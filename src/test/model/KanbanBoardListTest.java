package model;

import model.exceptions.DuplicateColumnException;
import model.exceptions.EmptyColumnNameException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class KanbanBoardListTest {
    private static final String COMPLETED_COLUMN_NAME = "Done";

    private KanbanBoard board1;
    private KanbanBoard board2;

    private KanbanBoardList list;

    @BeforeEach
    public void setup() {
        board1 = new KanbanBoard("Kanban Board 1", "Kanban board description 1", COMPLETED_COLUMN_NAME);

        try {
            board1.addDefaultColumns();
        } catch (DuplicateColumnException | EmptyColumnNameException e) {
            fail("An exception should not have been thrown");
        }

        board2 = new KanbanBoard("Kanban Board 2", "Kanban board description 2", COMPLETED_COLUMN_NAME);

        try {
            board2.addDefaultColumns();
        } catch (DuplicateColumnException | EmptyColumnNameException e) {
            fail("An exception should not have been thrown");
        }

        list = new KanbanBoardList();
    }

    @Test
    public void testConstructor() {
        assertTrue(list.isEmpty());
    }

    @Test
    public void testAddBoardOnce() {
        list.addBoard(board1);

        assertEquals(1, list.size());
        assertTrue(list.getBoards().contains(board1));
    }

    @Test
    public void testAddBoardMultiple() {
        list.addBoard(board1);
        list.addBoard(board2);

        assertEquals(2, list.size());
        assertTrue(list.getBoards().contains(board1));
        assertTrue(list.getBoards().contains(board2));
    }

    @Test
    public void testGetBoard() {
        list.addBoard(board1);
        list.addBoard(board2);

        assertEquals(2, list.size());
        assertTrue(list.getBoards().contains(board1));
        assertTrue(list.getBoards().contains(board2));

        assertEquals(board1, list.getBoard(0));
        assertEquals(board2, list.getBoard(1));
    }

    @Test
    public void testIsEmpty() {
        assertTrue(list.isEmpty());

        list.addBoard(board1);

        assertFalse(list.isEmpty());
    }

    @Test
    public void testSize() {
        assertEquals(0, list.size());

        list.addBoard(board1);

        assertEquals(1, list.size());

        list.addBoard(board2);

        assertEquals(2, list.size());
    }
}
