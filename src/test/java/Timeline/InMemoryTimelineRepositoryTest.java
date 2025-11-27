package Timeline;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTimelineRepositoryTest {
    private InMemoryTimelineRepository repository;
    private UUID courseId1;
    private UUID courseId2;

    @BeforeEach
    void setUp() {
        repository = new InMemoryTimelineRepository();
        courseId1 = UUID.randomUUID();
        courseId2 = UUID.randomUUID();
    }

    @Test
    void testSaveAndRetrieve() {
        UUID contentId = UUID.randomUUID();
        TimelineEvent event = new TimelineEvent(courseId1, contentId, TimelineEventType.NOTES_GENERATED);
        event.setTitle("Test Notes");

        repository.save(event);

        List<TimelineEvent> events = repository.findByCourseNewestFirst(courseId1);
        assertEquals(1, events.size());
        assertEquals(event, events.get(0));
    }

    @Test
    void testFindByCourseNewestFirst() {
        UUID contentId1 = UUID.randomUUID();
        UUID contentId2 = UUID.randomUUID();
        
        TimelineEvent event1 = new TimelineEvent(courseId1, contentId1, TimelineEventType.NOTES_GENERATED);
        TimelineEvent event2 = new TimelineEvent(courseId1, contentId2, TimelineEventType.FLASHCARDS_GENERATED);

        repository.save(event1);
        repository.save(event2);

        List<TimelineEvent> events = repository.findByCourseNewestFirst(courseId1);
        assertEquals(2, events.size());
        // Newest first means event2 should be first
        assertEquals(event2, events.get(0));
        assertEquals(event1, events.get(1));
    }

    @Test
    void testEmptyCourse() {
        List<TimelineEvent> events = repository.findByCourseNewestFirst(courseId1);
        assertTrue(events.isEmpty());
    }

    @Test
    void testMultipleCourses() {
        UUID contentId1 = UUID.randomUUID();
        UUID contentId2 = UUID.randomUUID();
        
        TimelineEvent event1 = new TimelineEvent(courseId1, contentId1, TimelineEventType.NOTES_GENERATED);
        TimelineEvent event2 = new TimelineEvent(courseId2, contentId2, TimelineEventType.FLASHCARDS_GENERATED);

        repository.save(event1);
        repository.save(event2);

        List<TimelineEvent> events1 = repository.findByCourseNewestFirst(courseId1);
        List<TimelineEvent> events2 = repository.findByCourseNewestFirst(courseId2);

        assertEquals(1, events1.size());
        assertEquals(1, events2.size());
        assertEquals(event1, events1.get(0));
        assertEquals(event2, events2.get(0));
    }

    @Test
    void testThreadSafety() throws InterruptedException {
        UUID contentId = UUID.randomUUID();
        TimelineEvent event = new TimelineEvent(courseId1, contentId, TimelineEventType.NOTES_GENERATED);

        Thread t1 = new Thread(() -> repository.save(event));
        Thread t2 = new Thread(() -> repository.save(event));

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        List<TimelineEvent> events = repository.findByCourseNewestFirst(courseId1);
        assertEquals(2, events.size());
    }

    @Test
    void testFindByCourseNewestFirstWithEmptyList() {
        UUID unknownCourseId = UUID.randomUUID();
        List<TimelineEvent> events = repository.findByCourseNewestFirst(unknownCourseId);
        assertTrue(events.isEmpty());
    }

    @Test
    void testSaveMultipleEventsSameCourse() {
        UUID contentId1 = UUID.randomUUID();
        UUID contentId2 = UUID.randomUUID();
        UUID contentId3 = UUID.randomUUID();
        
        TimelineEvent event1 = new TimelineEvent(courseId1, contentId1, TimelineEventType.NOTES_GENERATED);
        TimelineEvent event2 = new TimelineEvent(courseId1, contentId2, TimelineEventType.FLASHCARDS_GENERATED);
        TimelineEvent event3 = new TimelineEvent(courseId1, contentId3, TimelineEventType.QUIZ_GENERATED);

        repository.save(event1);
        repository.save(event2);
        repository.save(event3);

        List<TimelineEvent> events = repository.findByCourseNewestFirst(courseId1);
        assertEquals(3, events.size());
        // Newest first: event3, event2, event1
        assertEquals(event3, events.get(0));
        assertEquals(event2, events.get(1));
        assertEquals(event1, events.get(2));
    }

    @Test
    void testSaveWithNullCourseId() {
        UUID contentId = UUID.randomUUID();
        TimelineEvent event = new TimelineEvent(null, contentId, TimelineEventType.NOTES_GENERATED);
        // This should work (null is a valid UUID value)
        repository.save(event);
        List<TimelineEvent> events = repository.findByCourseNewestFirst(null);
        assertEquals(1, events.size());
    }
}


