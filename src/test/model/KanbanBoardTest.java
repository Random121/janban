package model;

import static org.junit.jupiter.api.Assertions.*;

import model.exceptions.DuplicateColumnException;
import model.exceptions.EmptyCardTitleException;
import model.exceptions.EmptyColumnNameException;
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
        try {
            board = new KanbanBoard(COMPLETED_COLUMN_NAME);
        } catch (DuplicateColumnException | EmptyColumnNameException e) {
            fail("An exception should not have been thrown");
        }

        column1 = makeColumnOrFail("Column 1");
        column2 = makeColumnOrFail("Column 2");

        card1 = makeCardOrFail("Card 1", "Description 1", "Person 1", CardType.ISSUE, new HashSet<>(), 1);
        card2 = makeCardOrFail("Card 2", "Description 2", "Person 2", CardType.TASK, new HashSet<>(), 2);
        card3 = makeCardOrFail("Card 3", "Description 3", "Person 3", CardType.ISSUE, new HashSet<>(), 3);
        card4 = makeCardOrFail("Card 4", "Description 4", "Person 4", CardType.TASK, new HashSet<>(), 4);
    }

    @Test
    public void testConstructorNoException() {
        KanbanBoard board = null;

        try {
            board = new KanbanBoard(COMPLETED_COLUMN_NAME);
        } catch (DuplicateColumnException | EmptyColumnNameException e) {
            fail("An exception should not have been thrown");
        }

        assertNotNull(board);
        assertNotNull(board.getCompletedColumn());

        assertEquals(3, board.getColumns().size());

        assertEquals("Backlog", board.getColumn(0).getName());
        assertEquals("In Progress", board.getColumn(1).getName());

        assertEquals(COMPLETED_COLUMN_NAME, board.getCompletedColumn().getName());
        assertEquals(board.getColumn(2), board.getCompletedColumn());
    }

    @Test
    public void testConstructorExpectDuplicateColumnNameException() {
        try {
            new KanbanBoard("Backlog");
            fail("An exception should have been thrown");
        } catch (DuplicateColumnException e) {
            // This exception should have been thrown
        } catch (EmptyColumnNameException e) {
            fail("Wrong exception thrown");
        }
    }

    @Test
    public void testConstructorExpectEmptyColumnNameException() {
        try {
            new KanbanBoard("");
            fail("An exception should have been thrown");
        } catch (DuplicateColumnException e) {
            fail("Wrong exception thrown");
        } catch (EmptyColumnNameException e) {
            // This exception should have been thrown
        }
    }

    @Test
    public void testAddColumnOnceNoException() {
        // Normal add
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
        Column duplicateColumn = null;

        try {
            duplicateColumn = new Column("In Progress");
        } catch (EmptyColumnNameException e) {
            fail("An exception should not have been thrown");
        }

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
        Column completedColumn = null;

        try {
            completedColumn = new Column(COMPLETED_COLUMN_NAME);
        } catch (EmptyColumnNameException e) {
            fail("An exception should not have been thrown");
        }

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
        // Normal edit
        Column secondColumn = board.getColumn(1);

        try {
            board.editColumnName(secondColumn, "New Column 2");
        } catch (DuplicateColumnException | EmptyColumnNameException e) {
            fail("An exception should not have been thrown");
        }

        assertEquals("New Column 2", secondColumn.getName());

        // Column is not part of the board
        String oldColumn2Name = column2.getName();

        try {
            board.editColumnName(column2, "New Column 2");
        } catch (DuplicateColumnException | EmptyColumnNameException e) {
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
        } catch (EmptyColumnNameException e) {
            fail("Wrong exception thrown");
        }

        assertEquals("Backlog", firstColumn.getName());
    }

    @Test
    public void testEditColumnNameExpectEmptyColumnNameException() {
        Column firstColumn = board.getColumn(0);

        try {
            board.editColumnName(firstColumn, "");
            fail("An exception should have been thrown");
        } catch (DuplicateColumnException e) {
            fail("Wrong exception thrown");
        } catch (EmptyColumnNameException e) {
            // This exception should have been thrown
        }

        assertEquals("Backlog", firstColumn.getName());
    }

    @Test
    public void testEditColumnNameCompletedNoException() {
        // Column is a completed column, becomes not completed
        Column oldCompletedColumn = board.getCompletedColumn();

        try {
            board.editColumnName(oldCompletedColumn, "Not Completed Column");
        } catch (DuplicateColumnException | EmptyColumnNameException e) {
            fail("An exception should not have been thrown");
        }

        assertNull(board.getCompletedColumn());
        assertEquals("Not Completed Column", oldCompletedColumn.getName());

        // Column is not a completed column, becomes completed
        Column firstColumn = board.getColumn(0);

        try {
            board.editColumnName(firstColumn, COMPLETED_COLUMN_NAME);
        } catch (DuplicateColumnException | EmptyColumnNameException e) {
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

    private Column makeColumnOrFail(String name) {
        try {
            return new Column(name);
        } catch (EmptyColumnNameException e) {
            fail("An exception should not have been thrown");
        }

        return null;
    }

    private Card makeCardOrFail(String title,
                                String description,
                                String assignee,
                                CardType type,
                                Set<String> tags,
                                int storyPoints) {
        try {
            return new Card(title, description, assignee, type, tags, storyPoints);
        } catch (NegativeStoryPointsException | EmptyCardTitleException e) {
            fail("An exception should not have been thrown");
        }

        return null;
    }
}
