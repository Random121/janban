package model;

import static org.junit.jupiter.api.Assertions.*;

import model.exceptions.DuplicateColumnException;
import model.exceptions.EmptyColumnNameException;
import org.junit.jupiter.api.Test;

public class ProjectTest {
    @Test
    public void constructorTest() {
        Project projectSuccess = null;

        try {
            projectSuccess = new Project("My Project",
                                         "This is my project",
                                         "Completed");
        } catch (DuplicateColumnException | EmptyColumnNameException e) {
            fail("An exception should not have been thrown");
        }

        assertNotNull(projectSuccess);
        assertEquals("My Project", projectSuccess.getName());
        assertEquals("This is my project", projectSuccess.getDescription());
        assertNotNull(projectSuccess.getKanbanBoard());
        assertEquals("Completed", projectSuccess.getKanbanBoard().getCompletedColumn().getName());

        try {
            Project projectFail = new Project("My Project",
                                              "This is my project",
                                              "Backlog");
            fail("An exception should have been thrown");
        } catch (DuplicateColumnException e) {
            // This exception should have been thrown
        } catch (EmptyColumnNameException e) {
            fail("Wrong exception thrown");
        }

        try {
            Project projectFail = new Project("My Project",
                                              "This is my project",
                                              "");
            fail("An exception should have been thrown");
        } catch (EmptyColumnNameException e) {
            // This exception should have been thrown
        } catch (DuplicateColumnException e) {
            fail("Wrong exception thrown");
        }
    }
}
