package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

// Unit tests for the Event class.
public class EventTest {
    private Event e;
    private Date d;

    @BeforeEach
    public void runBefore() {
        e = new Event("Sensor open at door");
        d = Calendar.getInstance().getTime();
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
        assertNotEquals(null, e);
        assertNotEquals(e, new Object());
    }

    @Test
    public void testHashCode() {
        assertEquals(e.hashCode(), e.hashCode());
        assertNotEquals(new Object().hashCode(), e.hashCode());
    }
}