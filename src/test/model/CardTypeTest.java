package model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class CardTypeTest {
    @Test
    public void toStringTest() {
        assertEquals("User Story", CardType.USER_STORY.toString());
        assertEquals("Task", CardType.TASK.toString());
        assertEquals("Issue", CardType.ISSUE.toString());
    }
}
