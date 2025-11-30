package Timeline;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import usecases.Timeline.CourseIdMapper;
import usecases.Timeline.FileTimelineRepository;
import usecases.Timeline.TimelineEvent;
import usecases.Timeline.TimelineEventType;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class FileTimelineRepositoryTest {
    private static final String FILE_NAME = "timeline.json";
    private FileTimelineRepository repository;
    private File timelineFile;
    private File backupFile;
    
    @BeforeEach
    void setUp() throws Exception {
        // Backup existing timeline.json if it exists
        timelineFile = new File(FILE_NAME);
        if (timelineFile.exists()) {
            backupFile = new File(FILE_NAME + ".backup");
            Files.copy(timelineFile.toPath(), backupFile.toPath());
            timelineFile.delete();
        }
        
        repository = new FileTimelineRepository();
    }
    
    @AfterEach
    void tearDown() throws Exception {
        // Clean up test file
        if (timelineFile != null && timelineFile.exists()) {
            timelineFile.delete();
        }
        
        // Restore backup if it existed
        if (backupFile != null && backupFile.exists()) {
            Files.copy(backupFile.toPath(), timelineFile.toPath());
            backupFile.delete();
        }
    }
    
    @Test
    void testSaveAndFindByCourseNewestFirst() {
        UUID courseId = UUID.randomUUID();
        UUID contentId1 = UUID.randomUUID();
        UUID contentId2 = UUID.randomUUID();
        
        // Create and save first event
        TimelineEvent event1 = new TimelineEvent(courseId, contentId1, TimelineEventType.NOTES_GENERATED);
        event1.setTitle("First Note");
        event1.setSnippet("First snippet");
        repository.save(event1);
        
        // Create and save second event
        TimelineEvent event2 = new TimelineEvent(courseId, contentId2, TimelineEventType.NOTES_GENERATED);
        event2.setTitle("Second Note");
        event2.setSnippet("Second snippet");
        repository.save(event2);
        
        // Retrieve events - should be newest first
        List<TimelineEvent> events = repository.findByCourseNewestFirst(courseId);
        assertEquals(2, events.size());
        assertEquals("Second Note", events.get(0).getTitle());
        assertEquals("First Note", events.get(1).getTitle());
    }
    
    @Test
    void testFindByCourseNewestFirstWithNoEvents() {
        UUID courseId = UUID.randomUUID();
        List<TimelineEvent> events = repository.findByCourseNewestFirst(courseId);
        assertTrue(events.isEmpty());
    }
    
    @Test
    void testSaveWithFullContent() {
        UUID courseId = UUID.randomUUID();
        UUID contentId = UUID.randomUUID();
        
        TimelineEvent event = new TimelineEvent(courseId, contentId, TimelineEventType.NOTES_GENERATED);
        event.setTitle("Full Notes");
        event.setSnippet("Snippet");
        event.setFullNotesText("This is the full notes text content");
        event.setFlashcardData("{\"courseName\":\"Test\",\"flashcards\":[]}");
        event.setTestData("{\"questions\":[\"Q1\"],\"answers\":[\"A1\"]}");
        event.setEvaluationData("{\"score\":10}");
        
        repository.save(event);
        
        List<TimelineEvent> events = repository.findByCourseNewestFirst(courseId);
        assertEquals(1, events.size());
        TimelineEvent retrieved = events.get(0);
        assertEquals("Full Notes", retrieved.getTitle());
        assertEquals("This is the full notes text content", retrieved.getFullNotesText());
        assertNotNull(retrieved.getFlashcardData());
        assertNotNull(retrieved.getTestData());
        assertNotNull(retrieved.getEvaluationData());
    }
    
    @Test
    void testLoadFromFileWhenFileDoesNotExist() {
        // Repository should start empty when file doesn't exist
        UUID courseId = UUID.randomUUID();
        List<TimelineEvent> events = repository.findByCourseNewestFirst(courseId);
        assertTrue(events.isEmpty());
    }
    
    @Test
    void testLoadFromFileWhenFileIsEmpty() throws IOException {
        // Create empty file
        timelineFile.createNewFile();
        
        // Create new repository - should handle empty file gracefully
        FileTimelineRepository newRepo = new FileTimelineRepository();
        UUID courseId = UUID.randomUUID();
        List<TimelineEvent> events = newRepo.findByCourseNewestFirst(courseId);
        assertTrue(events.isEmpty());
    }
    
    // Note: Removed testLoadFromFileWhenJsonStringIsNull() because the jsonString == null check
    // was removed from the code. Files.readString() never returns null, so the check was unreachable.
    
    
    @Test
    void testLoadFromFileWithValidData() throws Exception {
        UUID courseId = UUID.randomUUID();
        UUID contentId = UUID.randomUUID();
        
        // Save an event
        TimelineEvent event = new TimelineEvent(courseId, contentId, TimelineEventType.FLASHCARDS_GENERATED);
        event.setTitle("Test Flashcards");
        event.setNumCards(10);
        repository.save(event);
        
        // Create new repository instance - should load from file
        FileTimelineRepository newRepo = new FileTimelineRepository();
        List<TimelineEvent> events = newRepo.findByCourseNewestFirst(courseId);
        assertEquals(1, events.size());
        assertEquals("Test Flashcards", events.get(0).getTitle());
        assertEquals(10, events.get(0).getNumCards());
    }
    
    @Test
    void testLoadFromFileWithCourseIdMapping() throws Exception {
        String courseIdString = "CSC207_TEST";
        UUID courseUuid = CourseIdMapper.getUuidForCourseId(courseIdString);
        UUID contentId = UUID.randomUUID();
        
        // Save an event with course ID mapping
        TimelineEvent event = new TimelineEvent(courseUuid, contentId, TimelineEventType.NOTES_GENERATED);
        event.setTitle("CSC207_TEST");
        event.setSnippet("Test notes");
        repository.save(event);
        
        // Create new repository - should restore mapping
        FileTimelineRepository newRepo = new FileTimelineRepository();
        List<TimelineEvent> events = newRepo.findByCourseNewestFirst(courseUuid);
        assertEquals(1, events.size());
    }
    
    @Test
    void testLoadFromFileWithCourseIdStringInData() throws Exception {
        UUID courseUuid = UUID.randomUUID();
        UUID contentId = UUID.randomUUID();
        // Use unique course ID to avoid conflicts with other tests
        String courseIdString = "PHL245_TEST_" + UUID.randomUUID().toString().substring(0, 8);
        
        // Manually create JSON with courseIdString field
        String json = String.format(
            "{\"%s\":[{\"courseId\":\"%s\",\"courseIdString\":\"%s\",\"contentId\":\"%s\"," +
            "\"type\":\"NOTES_GENERATED\",\"occurredAt\":\"2024-01-01T00:00:00Z\"," +
            "\"title\":\"PHL 245\",\"snippet\":\"Test\"}]}",
            courseUuid, courseUuid, courseIdString, contentId
        );
        
        try (FileWriter writer = new FileWriter(timelineFile)) {
            writer.write(json);
        }
        
        // Create new repository - should restore mapping
        FileTimelineRepository newRepo = new FileTimelineRepository();
        List<TimelineEvent> events = newRepo.findByCourseNewestFirst(courseUuid);
        assertEquals(1, events.size());
        assertEquals("PHL 245", events.get(0).getTitle());
        
        // Verify mapping was restored
        assertTrue(CourseIdMapper.hasUuid(courseIdString));
        assertEquals(courseUuid, CourseIdMapper.getUuidForCourseId(courseIdString));
    }
    
    @Test
    void testLoadFromFileWithNullCourseIdString() throws Exception {
        // Test line 71: data.courseIdString != null -> false branch
        UUID courseUuid = UUID.randomUUID();
        UUID contentId = UUID.randomUUID();
        
        // Manually create JSON without courseIdString field (will be null)
        String json = String.format(
            "{\"%s\":[{\"courseId\":\"%s\",\"contentId\":\"%s\"," +
            "\"type\":\"NOTES_GENERATED\",\"occurredAt\":\"2024-01-01T00:00:00Z\"," +
            "\"title\":\"Test Course\",\"snippet\":\"Test\"}]}",
            courseUuid, courseUuid, contentId
        );
        
        try (FileWriter writer = new FileWriter(timelineFile)) {
            writer.write(json);
        }
        
        // Create new repository - should handle null courseIdString gracefully
        FileTimelineRepository newRepo = new FileTimelineRepository();
        List<TimelineEvent> events = newRepo.findByCourseNewestFirst(courseUuid);
        assertEquals(1, events.size());
        assertEquals("Test Course", events.get(0).getTitle());
    }
    
    @Test
    void testLoadFromFileWithEmptyCourseIdString() throws Exception {
        // Test line 71: data.courseIdString != null -> true, but !data.courseIdString.isEmpty() -> false
        UUID courseUuid = UUID.randomUUID();
        UUID contentId = UUID.randomUUID();
        
        // Manually create JSON with empty courseIdString field
        String json = String.format(
            "{\"%s\":[{\"courseId\":\"%s\",\"courseIdString\":\"\",\"contentId\":\"%s\"," +
            "\"type\":\"NOTES_GENERATED\",\"occurredAt\":\"2024-01-01T00:00:00Z\"," +
            "\"title\":\"Test Course\",\"snippet\":\"Test\"}]}",
            courseUuid, courseUuid, contentId
        );
        
        try (FileWriter writer = new FileWriter(timelineFile)) {
            writer.write(json);
        }
        
        // Create new repository - should handle empty courseIdString gracefully
        FileTimelineRepository newRepo = new FileTimelineRepository();
        List<TimelineEvent> events = newRepo.findByCourseNewestFirst(courseUuid);
        assertEquals(1, events.size());
        assertEquals("Test Course", events.get(0).getTitle());
    }
    
    @Test
    void testLoadFromFileWithNullInferredCourseId() throws Exception {
        // Test line 89: inferredCourseId != null -> false branch
        // This happens when neither courseIdString nor title extraction yields a valid course ID
        UUID courseUuid = UUID.randomUUID();
        UUID contentId = UUID.randomUUID();
        
        // Create JSON with no courseIdString and title that doesn't match course code pattern
        String json = String.format(
            "{\"%s\":[{\"courseId\":\"%s\",\"contentId\":\"%s\"," +
            "\"type\":\"NOTES_GENERATED\",\"occurredAt\":\"2024-01-01T00:00:00Z\"," +
            "\"title\":\"Regular Title Without Course Code\",\"snippet\":\"Test\"}]}",
            courseUuid, courseUuid, contentId
        );
        
        try (FileWriter writer = new FileWriter(timelineFile)) {
            writer.write(json);
        }
        
        // Create new repository - inferredCourseId should be null
        FileTimelineRepository newRepo = new FileTimelineRepository();
        List<TimelineEvent> events = newRepo.findByCourseNewestFirst(courseUuid);
        assertEquals(1, events.size());
        assertEquals("Regular Title Without Course Code", events.get(0).getTitle());
    }
    
    
    @Test
    void testLoadFromFileWithTitleExtraction() throws Exception {
        UUID courseUuid = UUID.randomUUID();
        UUID contentId = UUID.randomUUID();
        // Use unique course code to avoid conflicts with other tests
        // Pattern: 2-4 uppercase letters, optional space, 3 digits
        // Generate unique code using timestamp to ensure uniqueness
        long timestamp = System.currentTimeMillis();
        String uniqueSuffix = String.valueOf(timestamp % 1000); // Last 3 digits
        // Pad to 3 digits if needed
        while (uniqueSuffix.length() < 3) {
            uniqueSuffix = "0" + uniqueSuffix;
        }
        String uniquePrefix = "TST"; // Test prefix
        String courseCode = uniquePrefix + " " + uniqueSuffix;
        String extractedCode = uniquePrefix + uniqueSuffix;
        
        // Manually create JSON with title that matches course code pattern
        String json = String.format(
            "{\"%s\":[{\"courseId\":\"%s\",\"contentId\":\"%s\"," +
            "\"type\":\"NOTES_GENERATED\",\"occurredAt\":\"2024-01-01T00:00:00Z\"," +
            "\"title\":\"%s\",\"snippet\":\"Test\"}]}",
            courseUuid, courseUuid, contentId, courseCode
        );
        
        try (FileWriter writer = new FileWriter(timelineFile)) {
            writer.write(json);
        }
        
        // Create new repository - should extract course ID from title
        FileTimelineRepository newRepo = new FileTimelineRepository();
        List<TimelineEvent> events = newRepo.findByCourseNewestFirst(courseUuid);
        assertEquals(1, events.size());
        
        // Verify mapping was restored from title
        // Note: The extraction logic takes first 7 chars, so "TST" + 3 digits should be extracted
        assertTrue(CourseIdMapper.hasUuid(extractedCode));
        assertEquals(courseUuid, CourseIdMapper.getUuidForCourseId(extractedCode));
    }
    
    @Test
    void testLoadFromFileWithInvalidJson() throws Exception {
        // Create file with invalid JSON
        try (FileWriter writer = new FileWriter(timelineFile)) {
            writer.write("invalid json {");
        }
        
        // Should handle gracefully and start with empty data
        FileTimelineRepository newRepo = new FileTimelineRepository();
        UUID courseId = UUID.randomUUID();
        List<TimelineEvent> events = newRepo.findByCourseNewestFirst(courseId);
        assertTrue(events.isEmpty());
    }
    
    @Test
    void testLoadFromFileWithNullDataMap() throws Exception {
        // Create file with null JSON
        try (FileWriter writer = new FileWriter(timelineFile)) {
            writer.write("null");
        }
        
        // Should handle gracefully
        FileTimelineRepository newRepo = new FileTimelineRepository();
        UUID courseId = UUID.randomUUID();
        List<TimelineEvent> events = newRepo.findByCourseNewestFirst(courseId);
        assertTrue(events.isEmpty());
    }
    
    @Test
    void testLoadFromFileWithWhitespaceOnly() throws Exception {
        // Create file with only whitespace
        try (FileWriter writer = new FileWriter(timelineFile)) {
            writer.write("   \n\t  ");
        }
        
        // Should handle gracefully
        FileTimelineRepository newRepo = new FileTimelineRepository();
        UUID courseId = UUID.randomUUID();
        List<TimelineEvent> events = newRepo.findByCourseNewestFirst(courseId);
        assertTrue(events.isEmpty());
    }
    
    @Test
    void testLoadFromFileWithTitleExtractionNoMatch() throws Exception {
        UUID courseUuid = UUID.randomUUID();
        UUID contentId = UUID.randomUUID();
        
        // Create JSON with title that doesn't match course code pattern
        String json = String.format(
            "{\"%s\":[{\"courseId\":\"%s\",\"contentId\":\"%s\"," +
            "\"type\":\"NOTES_GENERATED\",\"occurredAt\":\"2024-01-01T00:00:00Z\"," +
            "\"title\":\"Regular Title\",\"snippet\":\"Test\"}]}",
            courseUuid, courseUuid, contentId
        );
        
        try (FileWriter writer = new FileWriter(timelineFile)) {
            writer.write(json);
        }
        
        // Create new repository - should load but not extract course ID
        FileTimelineRepository newRepo = new FileTimelineRepository();
        List<TimelineEvent> events = newRepo.findByCourseNewestFirst(courseUuid);
        assertEquals(1, events.size());
        assertEquals("Regular Title", events.get(0).getTitle());
    }
    
    @Test
    void testLoadFromFileWithEmptyTitle() throws Exception {
        UUID courseUuid = UUID.randomUUID();
        UUID contentId = UUID.randomUUID();
        
        // Create JSON with empty title
        String json = String.format(
            "{\"%s\":[{\"courseId\":\"%s\",\"contentId\":\"%s\"," +
            "\"type\":\"NOTES_GENERATED\",\"occurredAt\":\"2024-01-01T00:00:00Z\"," +
            "\"title\":\"\",\"snippet\":\"Test\"}]}",
            courseUuid, courseUuid, contentId
        );
        
        try (FileWriter writer = new FileWriter(timelineFile)) {
            writer.write(json);
        }
        
        // Create new repository - should load but not extract course ID from empty title
        FileTimelineRepository newRepo = new FileTimelineRepository();
        List<TimelineEvent> events = newRepo.findByCourseNewestFirst(courseUuid);
        assertEquals(1, events.size());
    }
    
    @Test
    void testLoadFromFileWithNullTitle() throws Exception {
        UUID courseUuid = UUID.randomUUID();
        UUID contentId = UUID.randomUUID();
        
        // Create JSON with null title
        String json = String.format(
            "{\"%s\":[{\"courseId\":\"%s\",\"contentId\":\"%s\"," +
            "\"type\":\"NOTES_GENERATED\",\"occurredAt\":\"2024-01-01T00:00:00Z\"," +
            "\"snippet\":\"Test\"}]}",
            courseUuid, courseUuid, contentId
        );
        
        try (FileWriter writer = new FileWriter(timelineFile)) {
            writer.write(json);
        }
        
        // Create new repository - should load but not extract course ID from null title
        FileTimelineRepository newRepo = new FileTimelineRepository();
        List<TimelineEvent> events = newRepo.findByCourseNewestFirst(courseUuid);
        assertEquals(1, events.size());
    }
    
    @Test
    void testSaveWithAllEventTypes() {
        UUID courseId = UUID.randomUUID();
        
        // Save all event types
        TimelineEvent notes = new TimelineEvent(courseId, UUID.randomUUID(), TimelineEventType.NOTES_GENERATED);
        notes.setTitle("Notes");
        repository.save(notes);
        
        TimelineEvent flashcards = new TimelineEvent(courseId, UUID.randomUUID(), TimelineEventType.FLASHCARDS_GENERATED);
        flashcards.setTitle("Flashcards");
        flashcards.setNumCards(10);
        repository.save(flashcards);
        
        TimelineEvent quiz = new TimelineEvent(courseId, UUID.randomUUID(), TimelineEventType.QUIZ_GENERATED);
        quiz.setTitle("Quiz");
        quiz.setNumQuestions(5);
        repository.save(quiz);
        
        TimelineEvent submitted = new TimelineEvent(courseId, UUID.randomUUID(), TimelineEventType.QUIZ_SUBMITTED);
        submitted.setTitle("Quiz Submitted");
        submitted.setNumQuestions(5);
        submitted.setScore(4.5);
        repository.save(submitted);
        
        List<TimelineEvent> events = repository.findByCourseNewestFirst(courseId);
        assertEquals(4, events.size());
        assertEquals(TimelineEventType.QUIZ_SUBMITTED, events.get(0).getType());
        assertEquals(TimelineEventType.QUIZ_GENERATED, events.get(1).getType());
        assertEquals(TimelineEventType.FLASHCARDS_GENERATED, events.get(2).getType());
        assertEquals(TimelineEventType.NOTES_GENERATED, events.get(3).getType());
    }
    
    @Test
    void testSaveWithNullFields() {
        UUID courseId = UUID.randomUUID();
        UUID contentId = UUID.randomUUID();
        
        TimelineEvent event = new TimelineEvent(courseId, contentId, TimelineEventType.NOTES_GENERATED);
        // Leave all fields null
        repository.save(event);
        
        List<TimelineEvent> events = repository.findByCourseNewestFirst(courseId);
        assertEquals(1, events.size());
        assertNull(events.get(0).getTitle());
        assertNull(events.get(0).getSnippet());
    }
    
    @Test
    void testMultipleCourses() {
        UUID courseId1 = UUID.randomUUID();
        UUID courseId2 = UUID.randomUUID();
        
        TimelineEvent event1 = new TimelineEvent(courseId1, UUID.randomUUID(), TimelineEventType.NOTES_GENERATED);
        event1.setTitle("Course 1 Note");
        repository.save(event1);
        
        TimelineEvent event2 = new TimelineEvent(courseId2, UUID.randomUUID(), TimelineEventType.NOTES_GENERATED);
        event2.setTitle("Course 2 Note");
        repository.save(event2);
        
        List<TimelineEvent> events1 = repository.findByCourseNewestFirst(courseId1);
        List<TimelineEvent> events2 = repository.findByCourseNewestFirst(courseId2);
        
        assertEquals(1, events1.size());
        assertEquals(1, events2.size());
        assertEquals("Course 1 Note", events1.get(0).getTitle());
        assertEquals("Course 2 Note", events2.get(0).getTitle());
    }
    
    @Test
    void testPersistenceAcrossInstances() throws Exception {
        UUID courseId = UUID.randomUUID();
        UUID contentId = UUID.randomUUID();
        
        // Save event in first repository instance
        TimelineEvent event = new TimelineEvent(courseId, contentId, TimelineEventType.NOTES_GENERATED);
        event.setTitle("Persistent Note");
        event.setSnippet("This should persist");
        repository.save(event);
        
        // Create new repository instance - should load persisted data
        FileTimelineRepository newRepo = new FileTimelineRepository();
        List<TimelineEvent> events = newRepo.findByCourseNewestFirst(courseId);
        assertEquals(1, events.size());
        assertEquals("Persistent Note", events.get(0).getTitle());
        assertEquals("This should persist", events.get(0).getSnippet());
    }
    
    @Test
    void testSaveToFileWithIOException() throws Exception {
        // Test lines 125-126: catch block for IOException in saveToFile()
        // Create a directory with the same name as the timeline file
        // This will cause an IOException when trying to write
        if (timelineFile.exists()) {
            timelineFile.delete();
        }
        
        // Create a directory with the timeline file name
        timelineFile.mkdirs();
        
        // Create an event and try to save it - this should trigger saveToFile()
        // which will fail with IOException, but the exception should be caught
        UUID courseId = UUID.randomUUID();
        UUID contentId = UUID.randomUUID();
        TimelineEvent event = new TimelineEvent(courseId, contentId, TimelineEventType.NOTES_GENERATED);
        event.setTitle("Test Event");
        event.setSnippet("Test snippet");
        
        // This should not throw an exception, even though saveToFile() will fail
        repository.save(event);
        
        // Verify the event is still in memory (even though file save failed)
        List<TimelineEvent> events = repository.findByCourseNewestFirst(courseId);
        assertEquals(1, events.size());
        assertEquals("Test Event", events.get(0).getTitle());
        
        // Clean up the directory
        timelineFile.delete();
    }
    
}

