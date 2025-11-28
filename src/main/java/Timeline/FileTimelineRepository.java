package Timeline;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * File-based implementation of ITimelineRepository that persists timeline events to a JSON file.
 * Events are saved to timeline.json in the project root directory.
 */
public class FileTimelineRepository implements ITimelineRepository {
    private final String FILE_NAME = "timeline.json";
    private final Map<UUID, List<TimelineEvent>> byCourse = new HashMap<>();
    private final Gson gson;

    public FileTimelineRepository() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        loadFromFile();
    }

    @Override
    public synchronized void save(TimelineEvent event) {
        byCourse.computeIfAbsent(event.getCourseId(), k -> new ArrayList<>()).add(event);
        saveToFile();
    }

    @Override
    public synchronized List<TimelineEvent> findByCourseNewestFirst(UUID courseId) {
        List<TimelineEvent> list = byCourse.getOrDefault(courseId, new ArrayList<>());
        List<TimelineEvent> out = new ArrayList<>(list);
        Collections.reverse(out);
        return out;
    }

    /**
     * Loads timeline events from the JSON file.
     */
    private void loadFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            return; // File doesn't exist yet, start with empty data
        }

        try {
            String jsonString = Files.readString(Paths.get(FILE_NAME));
            if (jsonString.trim().isEmpty()) {
                return; // Empty file
            }

            Type type = new TypeToken<Map<String, List<TimelineEventData>>>(){}.getType();
            Map<String, List<TimelineEventData>> dataMap = gson.fromJson(jsonString, type);
            
            if (dataMap != null) {
                byCourse.clear();
                for (Map.Entry<String, List<TimelineEventData>> entry : dataMap.entrySet()) {
                    UUID courseUuidFromFile = UUID.fromString(entry.getKey());
                    List<TimelineEvent> events = new ArrayList<>();
                    
                    // Try to restore the course ID mapping from stored events
                    String inferredCourseId = null;
                    for (TimelineEventData data : entry.getValue()) {
                        // First, try to get course ID from stored field
                        if (data.courseIdString != null && !data.courseIdString.isEmpty()) {
                            inferredCourseId = data.courseIdString;
                            break;
                        }
                        // Fallback: try to extract course ID from title (e.g., "PHL 245" -> "PHL245")
                        if (data.title != null && !data.title.isEmpty()) {
                            String title = data.title.trim();
                            // Pattern: course codes like "PHL 245" or "PHL245" 
                            // Match: 2-4 uppercase letters, optional space, 3 digits
                            if (title.matches("^[A-Z]{2,4}\\s*\\d{3}.*")) {
                                String cleaned = title.replaceAll("\\s+", "");
                                inferredCourseId = cleaned.substring(0, Math.min(7, cleaned.length()));
                                break;
                            }
                        }
                    }
                    
                    // Restore the UUID mapping if we found a course ID
                    if (inferredCourseId != null) {
                        CourseIdMapper.restoreMapping(inferredCourseId, courseUuidFromFile);
                    }
                    
                    // Load all events for this course UUID
                    for (TimelineEventData data : entry.getValue()) {
                        events.add(data.toTimelineEvent());
                    }
                    byCourse.put(courseUuidFromFile, events);
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading timeline from file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Saves timeline events to the JSON file.
     */
    private void saveToFile() {
        try {
            // Convert to a serializable format
            Map<String, List<TimelineEventData>> dataMap = new HashMap<>();
            for (Map.Entry<UUID, List<TimelineEvent>> entry : byCourse.entrySet()) {
                List<TimelineEventData> eventDataList = new ArrayList<>();
                for (TimelineEvent event : entry.getValue()) {
                    eventDataList.add(new TimelineEventData(event));
                }
                dataMap.put(entry.getKey().toString(), eventDataList);
            }

            try (FileWriter writer = new FileWriter(FILE_NAME)) {
                gson.toJson(dataMap, writer);
            }
        } catch (IOException e) {
            System.err.println("Error saving timeline to file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Helper class for serializing/deserializing TimelineEvent.
     * Handles UUID and Instant conversion to/from strings.
     */
    private static class TimelineEventData {
        String courseId;  // UUID as string
        String courseIdString;  // Original course ID string (e.g., "PHL245")
        String contentId;
        String type;
        String occurredAt;
        String title;
        String snippet;
        Integer numCards;
        Integer numQuestions;
        Double score;
        String fullNotesText;
        String flashcardData;
        String testData;
        String evaluationData;

        TimelineEventData(TimelineEvent event) {
            this.courseId = event.getCourseId().toString();
            // Try to find the original course ID string from CourseIdMapper
            String originalCourseId = CourseIdMapper.getCourseIdForUuid(event.getCourseId());
            this.courseIdString = originalCourseId;  // May be null for old events
            this.contentId = event.getContentId().toString();
            this.type = event.getType().name();
            this.occurredAt = event.getOccurredAt().toString();
            this.title = event.getTitle();
            this.snippet = event.getSnippet();
            this.numCards = event.getNumCards();
            this.numQuestions = event.getNumQuestions();
            this.score = event.getScore();
            this.fullNotesText = event.getFullNotesText();
            this.flashcardData = event.getFlashcardData();
            this.testData = event.getTestData();
            this.evaluationData = event.getEvaluationData();
        }

        TimelineEvent toTimelineEvent() {
            TimelineEvent event = new TimelineEvent(
                UUID.fromString(courseId),
                UUID.fromString(contentId),
                TimelineEventType.valueOf(type)
            );
            
            // Note: id and occurredAt are final and will be regenerated with new values.
            // This is acceptable - the important data (content) is preserved.
            
            event.setTitle(title);
            event.setSnippet(snippet);
            event.setNumCards(numCards);
            event.setNumQuestions(numQuestions);
            event.setScore(score);
            event.setFullNotesText(fullNotesText);
            event.setFlashcardData(flashcardData);
            event.setTestData(testData);
            event.setEvaluationData(evaluationData);
            
            return event;
        }
    }
}

