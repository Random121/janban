package model;

import static org.junit.jupiter.api.Assertions.*;

import model.exceptions.NegativeStoryPointsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ColumnTest {
    private Column column;
    private Card card1;
    private Card card2;
    private Card card3;
    private Card card4;

    @BeforeEach
    public void setup() {
        column = new Column("Column 1");

        card1 = makeCardOrFail("Card 1 (keyword1, keyword2)",
                               "Description 1 keyword2",
                               "Assignee 1",
                               CardType.USER_STORY,
                               new HashSet<>() {{
                                   add("keyword2");
                               }},
                               1);

        card2 = makeCardOrFail("Card 2",
                               "Description 2.",
                               "Assignee 2",
                               CardType.ISSUE,
                               new HashSet<>(),
                               2);

        card3 = makeCardOrFail("Card 3 (keyword1, keyword2)",
                               "Description 3 keyword1 keyword2",
                               "Assignee 3 keyword1",
                               CardType.TASK,
                               new HashSet<>() {{
                                   add("keyword1");
                                   add("keyword2");
                                   add("other_tag");
                               }},
                               3);

        card4 = makeCardOrFail("Card 4",
                               "Description 4",
                               "Assignee 4",
                               CardType.USER_STORY,
                               new HashSet<>(),
                               4);
    }

    @Test
    public void testConstructor() {
        Column column = new Column("Column 1");

        assertNotNull(column);
        assertEquals("Column 1", column.getName());
        assertNotNull(column.getCards());
        assertTrue(column.getCards().isEmpty());
    }

    @Test
    public void testConstructorEmptyNameDefault() {
        Column column = new Column("");

        assertEquals(Column.EMPTY_COLUMN_NAME, column.getName());
    }

    @Test
    public void testAddCardOnce() {
        column.addCard(card1);

        assertEquals(1, column.getCards().size());
        assertTrue(column.getCards().contains(card1));

        assertEquals(column, card1.getContainingColumn());
    }

    @Test
    public void testAddCardMultiple() {
        column.addCard(card1);

        assertEquals(1, column.getCards().size());
        assertTrue(column.getCards().contains(card1));

        assertEquals(column, card1.getContainingColumn());

        // Add duplicate card, shouldn't do anything
        column.addCard(card1);

        assertEquals(1, column.getCards().size());
        assertTrue(column.getCards().contains(card1));

        assertEquals(column, card1.getContainingColumn());

        // Add new card
        column.addCard(card2);

        assertEquals(2, column.getCards().size());
        assertTrue(column.getCards().contains(card2));

        assertEquals(column, card2.getContainingColumn());
    }

    @Test
    public void testRemoveCardOnce() {
        column.addCard(card1);

        assertTrue(column.getCards().contains(card1));

        column.removeCard(card1);

        assertTrue(column.getCards().isEmpty());
        assertFalse(column.getCards().contains(card1));

        assertNull(card1.getContainingColumn());
    }

    @Test
    public void testRemoveCardMultiple() {
        column.addCard(card1);
        column.addCard(card3);

        assertTrue(column.getCards().contains(card1));
        assertTrue(column.getCards().contains(card3));

        // Remove non-existent card, do nothing
        column.removeCard(card2);

        assertEquals(2, column.getCards().size());
        assertFalse(column.getCards().contains(card2));

        assertTrue(column.getCards().contains(card1));
        assertTrue(column.getCards().contains(card3));

        assertNull(card2.getContainingColumn());

        // Remove existing card
        column.removeCard(card1);

        assertEquals(1, column.getCards().size());
        assertFalse(column.getCards().contains(card1));
        assertTrue(column.getCards().contains(card3));

        assertNull(card1.getContainingColumn());

        // Remove existing last card
        column.removeCard(card3);

        assertTrue(column.getCards().isEmpty());
        assertFalse(column.getCards().contains(card1));
        assertFalse(column.getCards().contains(card3));

        assertNull(card3.getContainingColumn());
    }

    @Test
    public void testGetCardsWithQuery() {
        addAllCards();

        // No keywords, get all results
        List<Card> noKeywords = column.getCardsWithQuery(new HashSet<>());

        assertEquals(4, noKeywords.size());

        // Keyword matches none
        List<Card> keywordMatchNone = column.getCardsWithQuery(new HashSet<>() {{
            add("matches_none");
        }});

        assertTrue(keywordMatchNone.isEmpty());

        // Keyword matches some
        List<Card> keywordMatchesSome = column.getCardsWithQuery(new HashSet<>() {{
            add("keyword1");
            add("keyword2");
        }});

        assertEquals(2, keywordMatchesSome.size());
        assertEquals(card3, keywordMatchesSome.get(0));
        assertEquals(card1, keywordMatchesSome.get(1));
    }

    @Test
    public void testGetCardsOfType() {
        addAllCards();

        List<Card> userStories = column.getCardsOfType(CardType.USER_STORY);

        assertEquals(2, userStories.size());
        assertEquals(card1, userStories.get(0));
        assertEquals(card4, userStories.get(1));

        List<Card> tasks = column.getCardsOfType(CardType.TASK);

        assertEquals(1, tasks.size());
        assertEquals(card3, tasks.get(0));
    }

    @Test
    public void testGetTotalStoryPoints() {
        assertEquals(0, column.getTotalStoryPoints());

        addAllCards();

        assertEquals(10, column.getTotalStoryPoints());
    }

    @Test
    public void testSetName() {
        column.setName("New Name");

        assertEquals("New Name", column.getName());
    }

    @Test
    public void testSetNameEmptyNameDefault() {
        column.setName("");

        assertEquals(Column.EMPTY_COLUMN_NAME, column.getName());
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

    private void addAllCards() {
        column.addCard(card1);
        column.addCard(card2);
        column.addCard(card3);
        column.addCard(card4);

        assertTrue(column.getCards().contains(card1));
        assertTrue(column.getCards().contains(card2));
        assertTrue(column.getCards().contains(card3));
        assertTrue(column.getCards().contains(card4));
    }
}
