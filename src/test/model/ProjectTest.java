package model;

import static org.junit.jupiter.api.Assertions.*;

import model.exceptions.DuplicateColumnException;
import model.exceptions.EmptyColumnNameException;
import org.junit.jupiter.api.Test;

public class ProjectTest {
    @Test
    public void testConstructorNoException() {
        Project project = null;

        try {
            project = new Project("My Project",
                                         "This is my project",
                                         "Completed");
        } catch (DuplicateColumnException | EmptyColumnNameException e) {
            fail("An exception should not have been thrown");
        }

        assertNotNull(project);
        assertEquals("My Project", project.getName());
        assertEquals("This is my project", project.getDescription());
        assertNotNull(project.getKanbanBoard());
        assertEquals("Completed", project.getKanbanBoard().getCompletedColumn().getName());
    }

    @Test
    public void testConstructorDuplicateColumnException() {
        try {
            new Project("My Project",
                        "This is my project",
                        "Backlog");
            fail("An exception should have been thrown");
        } catch (DuplicateColumnException e) {
            // This exception should have been thrown
        } catch (EmptyColumnNameException e) {
            fail("Wrong exception thrown");
        }
    }

    @Test
    public void testConstructorEmptyColumnNameException() {
        try {
            new Project("My Project",
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
