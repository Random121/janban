package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Event class
 * Taken from https://github.students.cs.ubc.ca/CPSC210/AlarmSystem.
 */
public class EventTest {
    private Event e;
    private Date d;

    // NOTE: these tests might fail if time at which line (2) below is executed
    // is different from time that line (1) is executed.  Lines (1) and (2) must
    // run in same millisecond for this test to make sense and pass.

    // NOTE: these tests are stupid, honestly should just compare if they are close enough
    // rather than them being equivalent.

    @BeforeEach
    public void runBefore() {
        e = new Event("Sensor open at door");   // (1)
        d = Calendar.getInstance().getTime();   // (2)
    }

    @Test
    public void testEvent() {
        // A margin of error (in ms) when comparing if two dates are equivalent
        final int MARGIN_OF_ERROR = 1000;

        assertEquals("Sensor open at door", e.getDescription());
        assertTrue((d.getTime() - e.getDate().getTime()) < MARGIN_OF_ERROR);
    }

    @Test
    public void testToString() {
        assertEquals(d.toString() + "\n" + "Sensor open at door", e.toString());
    }

    @Test
    public void testEquals() {
        assertFalse(e.equals(null));
        assertNotEquals(e, new Object());
    }

    @Test
    public void testHashCode() {
        assertEquals(e.hashCode(), e.hashCode());
        assertNotEquals(new Object().hashCode(), e.hashCode());
    }
}