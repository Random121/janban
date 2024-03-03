package model;

import static org.junit.jupiter.api.Assertions.*;

import model.exceptions.NegativeStoryPointsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

public class CardTest {
    private Set<String> tags;
    private Card card;

    @BeforeEach
    public void setup() {
        tags = new HashSet<>() {{
            add("keyword1");
            add("keyword2");
            add("keyword3");
            add("keyword4");
            add("keyword5");
        }};

        try {
            card = new Card("Card 1, keyword1, keyword3",
                            "Description 1, keyword1",
                            "Assignee 1",
                            CardType.ISSUE,
                            tags,
                            8);
        } catch (NegativeStoryPointsException e) {
            fail("An exception should not have been thrown");
        }
    }

    @Test
    public void testConstructorNoException() {
        Card card = null;

        try {
            card = new Card("Card 1",
                            "Description 1",
                            "Assignee 1",
                            CardType.ISSUE,
                            tags,
                            8);
        } catch (NegativeStoryPointsException e) {
            fail("An exception should not have been thrown");
        }

        assertNotNull(card);
        assertEquals("Card 1", card.getTitle());
        assertEquals("Description 1", card.getDescription());
        assertEquals("Assignee 1", card.getAssignee());
        assertEquals(CardType.ISSUE, card.getType());
        assertEquals(tags, card.getTags());
        assertEquals(8, card.getStoryPoints());
        assertNull(card.getContainingColumn());
    }

    @Test
    public void testConstructorEmptyTitleDefault() {
        Card card = null;

        try {
            card = new Card("",
                            "Description 1",
                            "Leo",
                            CardType.USER_STORY,
                            tags,
                            8);
        } catch (NegativeStoryPointsException e) {
            fail("An exception should not have been thrown");
        }

        assertNotNull(card);
        assertEquals(Card.EMPTY_CARD_TITLE, card.getTitle());
    }

    @Test
    public void testConstructorExpectNegativeStoryPointsException() {
        try {
            new Card("Card 1",
                     "Description 1",
                     "Dogyun",
                     CardType.TASK,
                     tags,
                     -1);
            fail("An exception should have been thrown");
        } catch (NegativeStoryPointsException e) {
            // This exception should have been thrown
        }
    }

    @Test
    public void testGetQueryRelevancyScoreSingleKeyword() {
        // One keyword that matches the title, description, and tags
        Set<String> singleKeywordMatchAll = new HashSet<>() {{
            add("keyword1");
        }};

        assertEquals(3, card.getQueryRelevancyScore(singleKeywordMatchAll));
    }

    @Test
    public void testGetQueryRelevancyScoreRandomCase() {
        // One keyword that matches the title, description, and tags but has random case (randomly
        // upper and lower case)
        Set<String> singleKeywordMatchAllRandomCase = new HashSet<>() {{
            add("KeYwOrD1");
        }};

        assertEquals(3, card.getQueryRelevancyScore(singleKeywordMatchAllRandomCase));
    }

    @Test
    public void testGetQueryRelevancyScoreMatchNone() {
        // Keyword that matches nothing
        Set<String> singleKeywordMatchNone = new HashSet<>() {{
            add("keyword100");
        }};

        assertEquals(0, card.getQueryRelevancyScore(singleKeywordMatchNone));
    }

    @Test
    public void testGetQueryRelevancyScoreMultiple() {
        // Multiple keyword that matches sometimes or not at all
        // some also has random case
        Set<String> multipleKeywords = new HashSet<>() {{
            add("random_tag"); // No match
            add("keyword1"); // Match title, description, tag
            add("keyword5"); // Match tag
            add("keyword3"); // Match tag, title
        }};

        assertEquals(6, card.getQueryRelevancyScore(multipleKeywords));
    }

    @Test
    public void testSetTitleNotEmpty() {
        card.setTitle("New Title");

        assertEquals("New Title", card.getTitle());
    }

    @Test
    public void testSetTitleEmptyTitleDefault() {
        card.setTitle("");

        assertEquals(Card.EMPTY_CARD_TITLE, card.getTitle());
    }

    @Test
    public void testSetDescription() {
        card.setDescription("New Description");

        assertEquals("New Description", card.getDescription());
    }

    @Test
    public void testSetAssignee() {
        card.setAssignee("New Assignee");

        assertEquals("New Assignee", card.getAssignee());
    }

    @Test
    public void testSetType() {
        card.setType(CardType.ISSUE);

        assertEquals(CardType.ISSUE, card.getType());
    }

    @Test
    public void testSetTags() {
        Set<String> newTags = new HashSet<>() {{
            add("new_tag");
            add("another_tag");
        }};

        card.setTags(newTags);

        assertEquals(newTags, card.getTags());
    }

    @Test
    public void testSetStoryPointsNoException() {
        try {
            card.setStoryPoints(50);
        } catch (NegativeStoryPointsException e) {
            fail("An exception should not have been thrown");
        }

        assertEquals(50, card.getStoryPoints());
    }

    @Test
    public void testSetStoryPointsExpectNegativeStoryPointsException() {
        try {
            card.setStoryPoints(-10);
            fail("An exception should have been thrown");
        } catch (NegativeStoryPointsException e) {
            // This exception should have been thrown
        }

        assertEquals(8, card.getStoryPoints());
    }
}
