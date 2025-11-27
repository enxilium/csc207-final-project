package Timeline;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TimelineEventTest {

    @Test
    void testConstructor() {
        UUID courseId = UUID.randomUUID();
        UUID contentId = UUID.randomUUID();
        TimelineEvent event = new TimelineEvent(courseId, contentId, TimelineEventType.NOTES_GENERATED);

        assertNotNull(event.getId());
        assertEquals(courseId, event.getCourseId());
        assertEquals(contentId, event.getContentId());
        assertEquals(TimelineEventType.NOTES_GENERATED, event.getType());
        assertNotNull(event.getOccurredAt());
    }

    @Test
    void testGettersAndSetters() {
        UUID courseId = UUID.randomUUID();
        UUID contentId = UUID.randomUUID();
        TimelineEvent event = new TimelineEvent(courseId, contentId, TimelineEventType.FLASHCARDS_GENERATED);

        event.setTitle("Test Title");
        event.setSnippet("Test Snippet");
        event.setNumCards(10);
        event.setNumQuestions(5);
        event.setScore(4.5);

        assertEquals("Test Title", event.getTitle());
        assertEquals("Test Snippet", event.getSnippet());
        assertEquals(10, event.getNumCards());
        assertEquals(5, event.getNumQuestions());
        assertEquals(4.5, event.getScore());
    }

    @Test
    void testNullSetters() {
        UUID courseId = UUID.randomUUID();
        UUID contentId = UUID.randomUUID();
        TimelineEvent event = new TimelineEvent(courseId, contentId, TimelineEventType.NOTES_GENERATED);

        event.setTitle(null);
        event.setSnippet(null);
        event.setNumCards(null);
        event.setNumQuestions(null);
        event.setScore(null);

        assertNull(event.getTitle());
        assertNull(event.getSnippet());
        assertNull(event.getNumCards());
        assertNull(event.getNumQuestions());
        assertNull(event.getScore());
    }

    @Test
    void testOccurredAtIsSet() {
        UUID courseId = UUID.randomUUID();
        UUID contentId = UUID.randomUUID();
        Instant before = Instant.now();
        TimelineEvent event = new TimelineEvent(courseId, contentId, TimelineEventType.QUIZ_GENERATED);
        Instant after = Instant.now();

        assertTrue(event.getOccurredAt().isAfter(before.minusSeconds(1)) || event.getOccurredAt().equals(before));
        assertTrue(event.getOccurredAt().isBefore(after.plusSeconds(1)) || event.getOccurredAt().equals(after));
    }

    @Test
    void testDifferentEventTypes() {
        UUID courseId = UUID.randomUUID();
        UUID contentId = UUID.randomUUID();

        TimelineEvent notes = new TimelineEvent(courseId, contentId, TimelineEventType.NOTES_GENERATED);
        assertEquals(TimelineEventType.NOTES_GENERATED, notes.getType());

        TimelineEvent flashcards = new TimelineEvent(courseId, contentId, TimelineEventType.FLASHCARDS_GENERATED);
        assertEquals(TimelineEventType.FLASHCARDS_GENERATED, flashcards.getType());

        TimelineEvent quiz = new TimelineEvent(courseId, contentId, TimelineEventType.QUIZ_GENERATED);
        assertEquals(TimelineEventType.QUIZ_GENERATED, quiz.getType());

        TimelineEvent submitted = new TimelineEvent(courseId, contentId, TimelineEventType.QUIZ_SUBMITTED);
        assertEquals(TimelineEventType.QUIZ_SUBMITTED, submitted.getType());
    }
}


