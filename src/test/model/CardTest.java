package model;

import static org.junit.jupiter.api.Assertions.*;

import model.exceptions.EmptyCardTitleException;
import model.exceptions.NegativeStoryPointsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

public class CardTest {
    private Card card;

    @BeforeEach
    public void setup() {
        Set<String> tags = new HashSet<>() {{
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
    public void constructorTest() {
        Set<String> tags = new HashSet<>() {{
            add("fix");
            add("ui");
        }};

        Card cardSuccess = null;

        try {
            cardSuccess = new Card("Center a div on the UI",
                                   "This div needs to be centered",
                                   "John Doe",
                                   CardType.ISSUE,
                                   tags,
                                   8);
        } catch (NegativeStoryPointsException | EmptyCardTitleException e) {
            fail("An exception should not have been thrown");
        }

        assertNotNull(cardSuccess);
        assertEquals("Center a div on the UI", cardSuccess.getTitle());
        assertEquals("This div needs to be centered", cardSuccess.getDescription());
        assertEquals("John Doe", cardSuccess.getAssignee());
        assertEquals(CardType.ISSUE, cardSuccess.getType());
        assertEquals(tags, cardSuccess.getTags());
        assertEquals(8, cardSuccess.getStoryPoints());
        assertNull(cardSuccess.getContainingColumn());

        try {
            Card cardFailTitle = new Card("",
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

        try {
            Card cardFailStoryPoints = new Card("This is a title",
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
    public void getQueryRelevancyScoreTest() {
        // One keyword that matches the title, description, and tags
        Set<String> singleKeywordMatchAll = new HashSet<>() {{
            add("center");
        }};

        assertEquals(3, card.getQueryRelevancyScore(singleKeywordMatchAll));

        // One keyword that matches the title, description, and tags but has random case (randomly
        // upper and lower case)
        Set<String> singleKeywordMatchAllRandomCase = new HashSet<>() {{
            add("CeNTeR");
        }};

        assertEquals(3, card.getQueryRelevancyScore(singleKeywordMatchAllRandomCase));

        // One keyword that matches nothing
        Set<String> singleKeywordMatchNone = new HashSet<>() {{
            add("left");
        }};

        assertEquals(0, card.getQueryRelevancyScore(singleKeywordMatchNone));

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
}
