package persistence;

import model.Card;
import model.Column;
import model.KanbanBoard;

import static org.junit.jupiter.api.Assertions.*;

public class JsonTest {

    protected void assertKanbanBoardEqual(KanbanBoard board1, KanbanBoard board2) {
        assertEquals(board1.getName(), board2.getName());
        assertEquals(board1.getDescription(), board2.getDescription());

        Column completedColumn1 = board1.getCompletedColumn();
        Column completedColumn2 = board2.getCompletedColumn();

        if (completedColumn1 == null || completedColumn2 == null) {
            assertNull(completedColumn1);
            assertNull(completedColumn2);
        } else {
            assertEquals(completedColumn1.getName(), completedColumn2.getName());
        }

        assertEquals(board1.getColumnCount(), board2.getColumnCount());

        for (int i = 0; i < board1.getColumnCount(); i++) {
            Column column1 = board1.getColumn(i);
            Column column2 = board2.getColumn(i);

            assertColumnEqual(column1, column2);
        }
    }

    protected void assertColumnEqual(Column column1, Column column2) {
        assertEquals(column1.getName(), column2.getName());

        assertEquals(column1.getCards().size(), column2.getCards().size());

        for (int i = 0; i < column1.getCards().size(); i++) {
            Card card1 = column1.getCards().get(i);
            Card card2 = column2.getCards().get(i);

            assertCardEqual(card1, card2);
        }
    }

    protected void assertCardEqual(Card card1, Card card2) {
        assertEquals(card1.getTitle(), card2.getTitle());
        assertEquals(card1.getDescription(), card2.getDescription());
        assertEquals(card1.getAssignee(), card2.getAssignee());
        assertEquals(card1.getType(), card2.getType());
        assertEquals(card1.getTags(), card2.getTags());
        assertEquals(card1.getStoryPoints(), card2.getStoryPoints());
    }
}
