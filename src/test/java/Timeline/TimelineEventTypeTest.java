package Timeline;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Simple test to verify TimelineEventType enum exists and works correctly.
 * This prevents JUnit from throwing "LauncherDiscoveryRequest must not be null" errors.
 */
class TimelineEventTypeTest {

    @Test
    void testEnumValuesExist() {
        // Verify all expected enum values exist
        assertNotNull(TimelineEventType.NOTES_GENERATED);
        assertNotNull(TimelineEventType.FLASHCARDS_GENERATED);
        assertNotNull(TimelineEventType.QUIZ_GENERATED);
        assertNotNull(TimelineEventType.QUIZ_SUBMITTED);
    }

    @Test
    void testEnumValueOf() {
        // Verify valueOf works correctly
        assertEquals(TimelineEventType.NOTES_GENERATED, TimelineEventType.valueOf("NOTES_GENERATED"));
        assertEquals(TimelineEventType.FLASHCARDS_GENERATED, TimelineEventType.valueOf("FLASHCARDS_GENERATED"));
        assertEquals(TimelineEventType.QUIZ_GENERATED, TimelineEventType.valueOf("QUIZ_GENERATED"));
        assertEquals(TimelineEventType.QUIZ_SUBMITTED, TimelineEventType.valueOf("QUIZ_SUBMITTED"));
    }

    @Test
    void testEnumValues() {
        // Verify values() returns all enum values
        TimelineEventType[] values = TimelineEventType.values();
        assertEquals(4, values.length);
        assertTrue(java.util.Arrays.asList(values).contains(TimelineEventType.NOTES_GENERATED));
        assertTrue(java.util.Arrays.asList(values).contains(TimelineEventType.FLASHCARDS_GENERATED));
        assertTrue(java.util.Arrays.asList(values).contains(TimelineEventType.QUIZ_GENERATED));
        assertTrue(java.util.Arrays.asList(values).contains(TimelineEventType.QUIZ_SUBMITTED));
    }

    @Test
    void testEnumValueOfThrowsException() {
        // Verify valueOf throws IllegalArgumentException for invalid name
        assertThrows(IllegalArgumentException.class, () -> TimelineEventType.valueOf("INVALID_TYPE"));
    }
}


