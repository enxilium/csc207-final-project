package Timeline;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TimelineLoggerTest {
    private TestTimelineRepository repository;
    private TimelineLogger logger;
    private UUID courseId;

    @BeforeEach
    void setUp() {
        repository = new TestTimelineRepository();
        logger = new TimelineLogger(repository);
        courseId = UUID.randomUUID();
    }

    @Test
    void testLogNotesGenerated() {
        UUID contentId = UUID.randomUUID();
        logger.logNotesGenerated(courseId, contentId, "Test Notes", "Test snippet");

        List<TimelineEvent> events = repository.findByCourseNewestFirst(courseId);
        assertEquals(1, events.size());
        
        TimelineEvent event = events.get(0);
        assertEquals(TimelineEventType.NOTES_GENERATED, event.getType());
        assertEquals("Test Notes", event.getTitle());
        assertEquals("Test snippet", event.getSnippet());
        assertEquals(contentId, event.getContentId());
    }

    @Test
    void testLogFlashcardsGenerated() {
        UUID contentId = UUID.randomUUID();
        logger.logFlashcardsGenerated(courseId, contentId, 15);

        List<TimelineEvent> events = repository.findByCourseNewestFirst(courseId);
        assertEquals(1, events.size());
        
        TimelineEvent event = events.get(0);
        assertEquals(TimelineEventType.FLASHCARDS_GENERATED, event.getType());
        assertEquals(15, event.getNumCards());
        assertEquals(contentId, event.getContentId());
    }

    @Test
    void testLogQuizGenerated() {
        UUID contentId = UUID.randomUUID();
        logger.logQuizGenerated(courseId, contentId, 10);

        List<TimelineEvent> events = repository.findByCourseNewestFirst(courseId);
        assertEquals(1, events.size());
        
        TimelineEvent event = events.get(0);
        assertEquals(TimelineEventType.QUIZ_GENERATED, event.getType());
        assertEquals(10, event.getNumQuestions());
        assertEquals(contentId, event.getContentId());
    }

    @Test
    void testLogQuizSubmitted() {
        UUID contentId = UUID.randomUUID();
        logger.logQuizSubmitted(courseId, contentId, 10, 8.5);

        List<TimelineEvent> events = repository.findByCourseNewestFirst(courseId);
        assertEquals(1, events.size());
        
        TimelineEvent event = events.get(0);
        assertEquals(TimelineEventType.QUIZ_SUBMITTED, event.getType());
        assertEquals(10, event.getNumQuestions());
        assertEquals(8.5, event.getScore());
        assertEquals(contentId, event.getContentId());
    }

    @Test
    void testMultipleLogs() {
        UUID notesId = UUID.randomUUID();
        UUID cardsId = UUID.randomUUID();
        UUID quizId = UUID.randomUUID();

        logger.logNotesGenerated(courseId, notesId, "Notes", "Snippet");
        logger.logFlashcardsGenerated(courseId, cardsId, 20);
        logger.logQuizGenerated(courseId, quizId, 15);

        List<TimelineEvent> events = repository.findByCourseNewestFirst(courseId);
        assertEquals(3, events.size());
    }
}


