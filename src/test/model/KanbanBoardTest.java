package model;

import static org.junit.jupiter.api.Assertions.*;

import model.exceptions.DuplicateColumnException;
import model.exceptions.NegativeStoryPointsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

public class KanbanBoardTest {
    private static final String COMPLETED_COLUMN_NAME = "Done";

    private KanbanBoard board;

    private Column column1;
    private Column column2;

    private Card card1;
    private Card card2;
    private Card card3;
    private Card card4;

    @BeforeEach
    public void setup() {
        board = new KanbanBoard("Kanban Board", "Kanban board description", COMPLETED_COLUMN_NAME);

        try {
            board.addDefaultColumns();
        } catch (DuplicateColumnException e) {
            fail("An exception should not have been thrown");
        }

        column1 = new Column("Column 1");
        column2 = new Column("Column 2");

        card1 = makeCardOrFail("Card 1", "Description 1", "Person 1", CardType.ISSUE, new HashSet<>(), 1);
        card2 = makeCardOrFail("Card 2", "Description 2", "Person 2", CardType.TASK, new HashSet<>(), 2);
        card3 = makeCardOrFail("Card 3", "Description 3", "Person 3", CardType.ISSUE, new HashSet<>(), 3);
        card4 = makeCardOrFail("Card 4", "Description 4", "Person 4", CardType.TASK, new HashSet<>(), 4);
    }

    @Test
    public void testConstructor() {
        KanbanBoard board = new KanbanBoard("Kanban Board", "Kanban board description", COMPLETED_COLUMN_NAME);

        assertEquals("Kanban Board", board.getName());
        assertEquals("Kanban board description", board.getDescription());

        assertTrue(board.getColumns().isEmpty());
        assertNull(board.getCompletedColumn());
    }

    @Test
    public void testAddDefaultColumnsNoException() {
        KanbanBoard board = new KanbanBoard("Kanban Board", "Kanban board description", COMPLETED_COLUMN_NAME);

        try {
            board.addDefaultColumns();
        } catch (DuplicateColumnException e) {
            fail("An exception should not have been thrown");
        }

        assertEquals(3, board.getColumns().size());

        assertEquals(KanbanBoard.DEFAULT_BACKLOG_COLUMN_NAME, board.getColumn(0).getName());
        assertEquals(KanbanBoard.DEFAULT_WIP_COLUMN_NAME, board.getColumn(1).getName());
        assertEquals(COMPLETED_COLUMN_NAME, board.getColumn(2).getName());

        assertNotNull(board.getCompletedColumn());
        assertEquals(board.getColumn(2), board.getCompletedColumn());
    }

    @Test
    public void testAddDefaultColumnsExpectDuplicateColumnNameException() {
        KanbanBoard board = new KanbanBoard("Kanban Board",
                                            "Kanban board description",
                                            KanbanBoard.DEFAULT_BACKLOG_COLUMN_NAME);

        try {
            board.addDefaultColumns();
            fail("An exception should have been thrown");
        } catch (DuplicateColumnException e) {
            // This exception should have been thrown
        }
    }

    @Test
    public void testAddColumnOnceNoException() {
        try {
            board.addColumn(column1);
        } catch (DuplicateColumnException e) {
            fail("An exception should not have been thrown");
        }

        assertEquals(4, board.getColumnCount());
        assertTrue(board.getColumns().contains(column1));
        assertEquals(column1, board.getColumn(board.getColumnCount() - 1));
        assertNotEquals(column1, board.getCompletedColumn());
    }

    @Test
    public void testAddColumnOnceExpectDuplicateColumnNameException() {
        Column duplicateColumn = new Column(KanbanBoard.DEFAULT_WIP_COLUMN_NAME);

        try {
            board.addColumn(duplicateColumn);
            fail("An exception should have been thrown");
        } catch (DuplicateColumnException e) {
            // This exception should have been thrown
        }

        assertFalse(board.getColumns().contains(duplicateColumn));
    }

    @Test
    public void testAddColumnOnceCompletedNoException() {
        Column oldCompletedColumn = board.getCompletedColumn();

        board.removeColumn(oldCompletedColumn);

        assertFalse(board.getColumns().contains(oldCompletedColumn));

        // Add a column which has the name of the completed column
        Column completedColumn = new Column(COMPLETED_COLUMN_NAME);

        try {
            board.addColumn(completedColumn);
        } catch (DuplicateColumnException e) {
            fail("An exception should not have been thrown");
        }

        assertNotNull(board.getCompletedColumn());
        assertEquals(completedColumn, board.getCompletedColumn());
        assertTrue(board.getColumns().contains(completedColumn));
    }

    @Test
    public void testAddColumnMultiple() {
        try {
            board.addColumn(column1);
        } catch (DuplicateColumnException e) {
            fail("An exception should not have been thrown");
        }

        assertEquals(4, board.getColumnCount());
        assertTrue(board.getColumns().contains(column1));
        assertEquals(column1, board.getColumn(board.getColumnCount() - 1));
        assertNotEquals(column1, board.getCompletedColumn());

        try {
            board.addColumn(column2);
        } catch (DuplicateColumnException e) {
            fail("An exception should not have been thrown");
        }

        assertEquals(5, board.getColumnCount());
        assertTrue(board.getColumns().contains(column2));
        assertEquals(column2, board.getColumn(board.getColumnCount() - 1));
        assertNotEquals(column2, board.getCompletedColumn());

    }

    @Test
    public void testRemoveColumnOnce() {
        try {
            board.addColumn(column1);
            board.addColumn(column2);
        } catch (DuplicateColumnException e) {
            fail("An exception should not have been thrown");
        }

        assertTrue(board.getColumns().contains(column1));
        assertTrue(board.getColumns().contains(column2));

        // Normal removal
        board.removeColumn(column1);

        assertEquals(4, board.getColumnCount());
        assertFalse(board.getColumns().contains(column1));
        assertNotNull(board.getCompletedColumn());

        // Board does not contain column
        board.removeColumn(column1);

        assertEquals(4, board.getColumnCount());
        assertNotNull(board.getCompletedColumn());
    }

    @Test
    public void testRemoveColumnOnceCompleted() {
        // Column is completed column
        Column oldCompletedColumn = board.getCompletedColumn();

        board.removeColumn(oldCompletedColumn);

        assertEquals(2, board.getColumnCount());
        assertNull(board.getCompletedColumn());
        assertFalse(board.getColumns().contains(oldCompletedColumn));
    }

    @Test
    public void testRemoveColumnMultiple() {
        try {
            board.addColumn(column1);
            board.addColumn(column2);
        } catch (DuplicateColumnException e) {
            fail("An exception should not have been thrown");
        }

        assertTrue(board.getColumns().contains(column1));
        assertTrue(board.getColumns().contains(column2));

        Column firstColumn = board.getColumn(0);
        board.removeColumn(firstColumn);

        assertEquals(4, board.getColumnCount());
        assertFalse(board.getColumns().contains(firstColumn));
        assertNotNull(board.getCompletedColumn());

        board.removeColumn(column2);

        assertEquals(3, board.getColumnCount());
        assertFalse(board.getColumns().contains(column2));
        assertNotNull(board.getCompletedColumn());

        Column oldCompletedColumn = board.getCompletedColumn();

        board.removeColumn(oldCompletedColumn);

        assertEquals(2, board.getColumnCount());
        assertFalse(board.getColumns().contains(oldCompletedColumn));
        assertNull(board.getCompletedColumn());
    }

    @Test
    public void testEditColumnNameNoException() {
        Column secondColumn = board.getColumn(1);

        try {
            board.editColumnName(secondColumn, "New Column 2");
        } catch (DuplicateColumnException e) {
            fail("An exception should not have been thrown");
        }

        assertEquals("New Column 2", secondColumn.getName());

        // Column is not part of the board
        String oldColumn2Name = column2.getName();

        try {
            board.editColumnName(column2, "New Column 2");
        } catch (DuplicateColumnException e) {
            fail("An exception should not have been thrown");
        }

        assertEquals(oldColumn2Name, column2.getName());
        assertNotEquals("New Column 2", column2.getName());
    }

    @Test
    public void testEditColumnNameExpectDuplicateColumnNameException() {
        Column firstColumn = board.getColumn(0);

        try {
            board.editColumnName(firstColumn, COMPLETED_COLUMN_NAME);
            fail("An exception should have been thrown");
        } catch (DuplicateColumnException e) {
            // This exception should have been thrown
        }

        assertEquals(KanbanBoard.DEFAULT_BACKLOG_COLUMN_NAME, firstColumn.getName());
    }

    @Test
    public void testEditColumnNameExpectDuplicateColumnNameExceptionEmptyName() {
        Column firstColumn = board.getColumn(0);
        Column secondColumn = board.getColumn(1);

        try {
            board.editColumnName(firstColumn, "");
        } catch (DuplicateColumnException e) {
            fail("An exception should not have been thrown");
        }

        assertEquals(Column.DEFAULT_COLUMN_NAME, firstColumn.getName());

        try {
            board.editColumnName(secondColumn, "");
            fail("An exception should have been thrown");
        } catch (DuplicateColumnException e) {
            // This exception should have been thrown
        }

        assertEquals(KanbanBoard.DEFAULT_WIP_COLUMN_NAME, secondColumn.getName());
    }

    @Test
    public void testEditColumnNameEmptyColumnNameDefault() {
        Column firstColumn = board.getColumn(0);

        try {
            board.editColumnName(firstColumn, "");
        } catch (DuplicateColumnException e) {
            fail("An exception should not have been thrown");
        }

        assertEquals(Column.DEFAULT_COLUMN_NAME, firstColumn.getName());
    }

    @Test
    public void testEditColumnNameCompletedNoException() {
        // Column is a completed column, becomes not completed
        Column oldCompletedColumn = board.getCompletedColumn();

        try {
            board.editColumnName(oldCompletedColumn, "Not Completed Column");
        } catch (DuplicateColumnException e) {
            fail("An exception should not have been thrown");
        }

        assertNull(board.getCompletedColumn());
        assertEquals("Not Completed Column", oldCompletedColumn.getName());

        // Column is not a completed column, becomes completed
        Column firstColumn = board.getColumn(0);

        try {
            board.editColumnName(firstColumn, COMPLETED_COLUMN_NAME);
        } catch (DuplicateColumnException e) {
            fail("An exception should not have been thrown");
        }

        assertNotNull(board.getCompletedColumn());
        assertEquals(firstColumn, board.getCompletedColumn());
        assertEquals(COMPLETED_COLUMN_NAME, firstColumn.getName());
    }

    @Test
    public void testMoveCardOnce() {
        board.getColumn(0).addCard(card1);
        board.getColumn(1).addCard(card2);

        assertTrue(board.getColumn(0).getCards().contains(card1));
        assertTrue(board.getColumn(1).getCards().contains(card2));

        // Normal move
        board.moveCard(card1, board.getColumn(1));

        assertFalse(board.getColumn(0).getCards().contains(card1));
        assertTrue(board.getColumn(1).getCards().contains(card1));

        // Column is not part of the board
        board.moveCard(card2, column1);

        assertTrue(board.getColumn(1).getCards().contains(card2));
        assertFalse(column1.getCards().contains(card2));
    }

    @Test
    public void testGetTotalStoryPoints() {
        try {
            board.addColumn(column1);
        } catch (DuplicateColumnException e) {
            fail("An exception should not have been thrown");
        }

        assertTrue(board.getColumns().contains(column1));

        // No cards
        assertEquals(0, board.getTotalStoryPoints());

        board.moveCard(card1, board.getCompletedColumn());
        board.moveCard(card2, column1);
        board.moveCard(card3, board.getColumn(0));
        board.moveCard(card4, board.getColumn(0));

        assertEquals(10, board.getTotalStoryPoints());
    }

    @Test
    public void testGetCompletedStoryPoints() {
        // No cards
        assertEquals(0, board.getCompletedStoryPoints());

        board.moveCard(card1, board.getCompletedColumn());
        board.moveCard(card2, board.getCompletedColumn());
        board.moveCard(card3, board.getColumn(0));
        board.moveCard(card4, board.getColumn(1));

        assertEquals(3, board.getCompletedStoryPoints());

        // Without a completed column
        board.removeColumn(board.getCompletedColumn());

        assertEquals(0, board.getCompletedStoryPoints());
    }

    @Test
    public void testGetCardCount() {
        // No cards
        assertEquals(0, board.getCardCount(true));
        assertEquals(0, board.getCardCount(false));

        // Cards in columns except for completed
        board.moveCard(card1, board.getColumn(0));
        board.moveCard(card2, board.getColumn(1));
        board.moveCard(card3, board.getColumn(1));

        assertEquals(3, board.getCardCount(true));
        assertEquals(3, board.getCardCount(false));

        // Cards in all columns
        board.moveCard(card4, board.getCompletedColumn());

        assertEquals(4, board.getCardCount(true));
        assertEquals(3, board.getCardCount(false));

        // Without a completed column
        board.removeColumn(board.getCompletedColumn());

        assertEquals(3, board.getCardCount(true));
        assertEquals(3, board.getCardCount(false));
    }

    private Card makeCardOrFail(String title,
                                String description,
                                String assignee,
                                CardType type,
                                Set<String> tags,
                                int storyPoints) {
        try {
            return new Card(title, description, assignee, type, tags, storyPoints);
        } catch (NegativeStoryPointsException e) {
            fail("An exception should not have been thrown");
        }

        return null;
    }
}
