package model;

import static org.junit.jupiter.api.Assertions.*;

import model.exceptions.EmptyCardTitleException;
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
            add("fix");
            add("ui");
            add("urgent");
            add("div");
            add("center");
        }};

        try {
            card = new Card("Center a div on the UI",
                            "This div needs to be centered",
                            "John Doe",
                            CardType.ISSUE,
                            tags,
                            8);
        } catch (NegativeStoryPointsException | EmptyCardTitleException e) {
            fail("An exception should not have been thrown");
        }
    }

    @Test
    public void testConstructor() {
        Card card = null;

        try {
            card = new Card("Center a div on the UI",
                                   "This div needs to be centered",
                                   "John Doe",
                                   CardType.ISSUE,
                                   tags,
                                   8);
        } catch (NegativeStoryPointsException | EmptyCardTitleException e) {
            fail("An exception should not have been thrown");
        }

        assertNotNull(card);
        assertEquals("Center a div on the UI", card.getTitle());
        assertEquals("This div needs to be centered", card.getDescription());
        assertEquals("John Doe", card.getAssignee());
        assertEquals(CardType.ISSUE, card.getType());
        assertEquals(tags, card.getTags());
        assertEquals(8, card.getStoryPoints());
        assertNull(card.getContainingColumn());
    }

    @Test
    public void testConstructorEmptyTitle() {
        try {
            new Card("",
                     "This div needs to be centered",
                     "Jacky Chan",
                     CardType.USER_STORY,
                     tags,
                     8);
            fail("An exception should have been thrown");
        } catch (NegativeStoryPointsException e) {
            fail("Wrong exception thrown");
        } catch (EmptyCardTitleException e) {
            // This exception should have been thrown
        }
    }

    @Test
    public void testConstructorNegativeStoryPoints() {
        try {
            new Card("This is a title",
                     "This is a description",
                     "Jane Doe",
                     CardType.TASK,
                     tags,
                     -1);
            fail("An exception should have been thrown");
        } catch (NegativeStoryPointsException e) {
            // This exception should have been thrown
        } catch (EmptyCardTitleException e) {
            fail("Wrong exception thrown");
        }
    }

    @Test
    public void testGetQueryRelevancyScoreSingleKeyword() {
        // One keyword that matches the title, description, and tags
        Set<String> singleKeywordMatchAll = new HashSet<>() {{
            add("center");
        }};

        assertEquals(3, card.getQueryRelevancyScore(singleKeywordMatchAll));
    }

    @Test
    public void testGetQueryRelevancyScoreRandomCase() {
        // One keyword that matches the title, description, and tags but has random case (randomly
        // upper and lower case)
        Set<String> singleKeywordMatchAllRandomCase = new HashSet<>() {{
            add("CeNTeR");
        }};

        assertEquals(3, card.getQueryRelevancyScore(singleKeywordMatchAllRandomCase));
    }

    @Test
    public void testGetQueryRelevancyScoreMatchNone() {
        // Keyword that matches nothing
        Set<String> singleKeywordMatchNone = new HashSet<>() {{
            add("left");
        }};

        assertEquals(0, card.getQueryRelevancyScore(singleKeywordMatchNone));
    }

    @Test
    public void testGetQueryRelevancyScoreMultiple() {
        // Multiple keyword that matches sometimes or not at all
        // some also has random case
        Set<String> multipleKeywords = new HashSet<>() {{
            add("Right"); // No match
            add("div"); // Match title, description, tag
            add("UrGeNt"); // Match tag
            add("UI"); // Match tag, title
        }};

        assertEquals(6, card.getQueryRelevancyScore(multipleKeywords));
    }

    @Test
    public void testSetTitle() {
        try {
            card.setTitle("New Title");
        } catch (EmptyCardTitleException e) {
            fail("An exception should not have been thrown");
        }

        assertEquals("New Title", card.getTitle());
    }

    @Test
    public void testSetTitleEmpty() {
        try {
            card.setTitle("");
            fail("An exception should have been thrown");
        } catch (EmptyCardTitleException e) {
            // This exception should have been thrown
        }
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
    public void testSetStoryPoints() {
        try {
            card.setStoryPoints(50);
        } catch (NegativeStoryPointsException e) {
            fail("An exception should not have been thrown");
        }

        assertEquals(50, card.getStoryPoints());
    }

    @Test
    public void testSetStoryPointsNegative() {
        try {
            card.setStoryPoints(-10);
            fail("An exception should have been thrown");
        } catch (NegativeStoryPointsException e) {
            // This exception should have been thrown
        }
    }
}
