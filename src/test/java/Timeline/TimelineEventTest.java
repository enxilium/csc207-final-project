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

    @Test
    void testFullContentSetters() {
        UUID courseId = UUID.randomUUID();
        UUID contentId = UUID.randomUUID();
        TimelineEvent event = new TimelineEvent(courseId, contentId, TimelineEventType.NOTES_GENERATED);

        // Test setFullNotesText and getFullNotesText
        event.setFullNotesText("Full notes content");
        assertEquals("Full notes content", event.getFullNotesText());

        // Test setFlashcardData and getFlashcardData
        event.setFlashcardData("{\"courseName\":\"Test\",\"flashcards\":[]}");
        assertEquals("{\"courseName\":\"Test\",\"flashcards\":[]}", event.getFlashcardData());

        // Test setTestData and getTestData (line 64)
        event.setTestData("{\"questions\":[\"Q1\"],\"answers\":[\"A1\"]}");
        assertEquals("{\"questions\":[\"Q1\"],\"answers\":[\"A1\"]}", event.getTestData());

        // Test setEvaluationData and getEvaluationData (line 67)
        event.setEvaluationData("{\"score\":10,\"feedback\":[\"Good\"]}");
        assertEquals("{\"score\":10,\"feedback\":[\"Good\"]}", event.getEvaluationData());
    }

    @Test
    void testFullContentSettersWithNull() {
        UUID courseId = UUID.randomUUID();
        UUID contentId = UUID.randomUUID();
        TimelineEvent event = new TimelineEvent(courseId, contentId, TimelineEventType.NOTES_GENERATED);

        // Test setters with null values
        event.setFullNotesText(null);
        assertNull(event.getFullNotesText());

        event.setFlashcardData(null);
        assertNull(event.getFlashcardData());

        event.setTestData(null);
        assertNull(event.getTestData());

        event.setEvaluationData(null);
        assertNull(event.getEvaluationData());
    }
}


