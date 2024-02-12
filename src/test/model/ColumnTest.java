package model;

import static org.junit.jupiter.api.Assertions.*;

import model.exceptions.EmptyCardTitleException;
import model.exceptions.EmptyColumnNameException;
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

    private Card makeCard(String title,
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

    @BeforeEach
    public void setup() {
        try {
            column = new Column("Backlog");
        } catch (EmptyColumnNameException e) {
            fail("An exception should not have been thrown");
        }

        card1 = makeCard("Card 1 (keyword1, keyword2)",
                         "Description 1 keyword2",
                         "Assignee 1",
                         CardType.USER_STORY,
                         new HashSet<>() {{
                             add("keyword2");
                         }},
                         1);

        card2 = makeCard("Card 2",
                         "Description 2.",
                         "Assignee 2",
                         CardType.ISSUE,
                         new HashSet<>(),
                         2);

        card3 = makeCard("Card 3 (keyword1, keyword2)",
                         "Description 3 keyword1 keyword2",
                         "Assignee 3 keyword1",
                         CardType.TASK,
                         new HashSet<>() {{
                             add("keyword1");
                             add("keyword2");
                             add("other_tag");
                         }},
                         3);

        card4 = makeCard("Card 4",
                         "Description 4",
                         "Assignee 4",
                         CardType.USER_STORY,
                         new HashSet<>(),
                         4);
    }

    @Test
    public void constructorTest() {
        Column columnSuccess = null;

        try {
            columnSuccess = new Column("My Column");
        } catch (EmptyColumnNameException e) {
            fail("An exception should not have been thrown");
        }

        assertNotNull(columnSuccess);
        assertEquals("My Column", columnSuccess.getName());
        assertNotNull(columnSuccess.getCards());
        assertTrue(columnSuccess.getCards().isEmpty());

        try {
            new Column("");
            fail("An exception should have been thrown");
        } catch (EmptyColumnNameException e) {
            // This exception should have been thrown
        }
    }

    @Test
    public void addCardOnceTest() {
        column.addCard(card1);

        assertEquals(1, column.getCards().size());
        assertEquals(card1, column.getCard(0));

        assertEquals(column, card1.getContainingColumn());
    }

    @Test
    public void addCardMultipleTest() {
        column.addCard(card1);

        assertEquals(1, column.getCards().size());
        assertEquals(card1, column.getCard(0));

        assertEquals(column, card1.getContainingColumn());

        // Add duplicate card, shouldn't do anything
        column.addCard(card1);

        assertEquals(1, column.getCards().size());
        assertEquals(card1, column.getCard(0));

        assertEquals(column, card1.getContainingColumn());

        // Add new card
        column.addCard(card2);

        assertEquals(2, column.getCards().size());
        assertEquals(card1, column.getCard(0));
        assertEquals(card2, column.getCard(1));

        assertEquals(column, card1.getContainingColumn());
        assertEquals(column, card2.getContainingColumn());
    }

    @Test
    public void removeCardOnceTest() {
        column.addCard(card1);
        column.removeCard(card1);

        assertTrue(column.getCards().isEmpty());

        assertNull(card1.getContainingColumn());
    }

    @Test
    public void removeCardMultipleTest() {
        column.addCard(card1);
        column.addCard(card3);

        // Remove non-existent card, do nothing
        column.removeCard(card2);

        assertEquals(2, column.getCards().size());

        assertNull(card2.getContainingColumn());

        // Remove existing card
        column.removeCard(card1);

        assertEquals(1, column.getCards().size());
        assertEquals(card3, column.getCard(0));

        assertNull(card1.getContainingColumn());

        // Remove existing last card
        column.removeCard(card3);

        assertTrue(column.getCards().isEmpty());

        assertNull(card3.getContainingColumn());
    }

    @Test
    public void getCardsWithQueryTest() {
        column.addCard(card1);
        column.addCard(card2);
        column.addCard(card3);
        column.addCard(card4);

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
    public void getCardsOfTypeTest() {
        column.addCard(card1);
        column.addCard(card2);
        column.addCard(card3);
        column.addCard(card4);

        List<Card> userStories = column.getCardsOfType(CardType.USER_STORY);

        assertEquals(2, userStories.size());
        assertEquals(card1, userStories.get(0));
        assertEquals(card4, userStories.get(1));

        List<Card> tasks = column.getCardsOfType(CardType.TASK);

        assertEquals(1, tasks.size());
        assertEquals(card3, tasks.get(0));
    }

    @Test
    public void getTotalStoryPointsTest() {
        assertEquals(0, column.getTotalStoryPoints());

        column.addCard(card1);
        column.addCard(card2);
        column.addCard(card3);
        column.addCard(card4);

        assertEquals(10, column.getTotalStoryPoints());
    }

    @Test
    public void setNameSuccessTest() {
        try {
            column.setName("New Name");
        } catch (EmptyColumnNameException e) {
            fail("An exception should not have been thrown");
        }

        assertEquals("New Name", column.getName());
    }

    @Test
    public void setNameEmptyTest() {
        try {
            column.setName("New Name");
            fail("An exception should have been thrown");
        } catch (EmptyColumnNameException e) {
            // This exception should have been thrown
        }
    }
}
