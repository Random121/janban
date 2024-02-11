package model;

import static org.junit.jupiter.api.Assertions.*;

import model.exceptions.DuplicateColumnException;
import model.exceptions.EmptyColumnNameException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ProjectTest {

    @BeforeEach
    public void setup() {

    }

    @Test
    public void constructorTest() {
        Project projectSuccess = null;

        try {
            projectSuccess = new Project("My Project",
                                         "This is my project",
                                         "Completed");
        } catch (DuplicateColumnException | EmptyColumnNameException e) {
            fail("This should not fail");
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
            fail("This should fail for invalid column name");
        } catch (DuplicateColumnException e) {
            // This exception should occur
        } catch (EmptyColumnNameException e) {
            fail("Wrong exception");
        }

        try {
            Project projectFail = new Project("My Project",
                                              "This is my project",
                                              "");
            fail("This should fail for invalid column name");
        } catch (EmptyColumnNameException e) {
            // This exception should occur
        } catch (DuplicateColumnException e) {
            fail("Wrong exception");
        }
    }
}
