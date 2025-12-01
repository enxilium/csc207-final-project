package entities;

import java.time.Instant;
import java.util.UUID;

//    id is id of the timeline event
//    courseid is id of the course e.g. course csc207
//    contentid is id of the cntent generated
//    title is title of the notes
//    snippet is description of the notes
public class TimelineEvent {
    private final UUID id;
    private final UUID courseId;
    private final UUID contentId;
    private final TimelineEventType type;
    private Instant occurredAt;
    private String title;
    private String snippet;
    private Integer numCards;
    private Integer numQuestions;
    private Double score;

    // Full content storage
    private String fullNotesText;  // Full notes content
    private String flashcardData;  // JSON or serialized flashcard data
    private String testData;       // JSON or serialized test data (questions, answers, etc.)
    private String evaluationData; // JSON or serialized evaluation data (results, feedback, etc.)

    public TimelineEvent(UUID courseId, UUID contentId, TimelineEventType type) {
        this.id = UUID.randomUUID();
        this.courseId = courseId;
        this.contentId = contentId;
        this.type = type;
        this.occurredAt = Instant.now();
    }

    /**
     * Sets the occurredAt timestamp.
     * Used when restoring events from persistence.
     *
     * @param occurredAt the timestamp to set
     */
    public void setOccurredAt(Instant occurredAt) {
        this.occurredAt = occurredAt;
    }

    // Getters
    public UUID getId() { return id; }
    public UUID getCourseId() { return courseId; }
    public UUID getContentId() { return contentId; }
    public TimelineEventType getType() { return type; }
    public Instant getOccurredAt() { return occurredAt; }
    public String getTitle() { return title; }
    public String getSnippet() { return snippet; }
    public Integer getNumCards() { return numCards; }
    public Integer getNumQuestions() { return numQuestions; }
    public Double getScore() { return score; }

    // Setters
    public void setTitle(String title) { this.title = title; }
    public void setSnippet(String snippet) { this.snippet = snippet; }
    public void setNumCards(Integer numCards) { this.numCards = numCards; }
    public void setNumQuestions(Integer numQuestions) { this.numQuestions = numQuestions; }
    public void setScore(Double score) { this.score = score; }
    
    // Full content getters and setters
    public String getFullNotesText() { return fullNotesText; }
    public void setFullNotesText(String fullNotesText) { this.fullNotesText = fullNotesText; }
    
    public String getFlashcardData() { return flashcardData; }
    public void setFlashcardData(String flashcardData) { this.flashcardData = flashcardData; }
    
    public String getTestData() { return testData; }
    public void setTestData(String testData) { this.testData = testData; }
    
    public String getEvaluationData() { return evaluationData; }
    public void setEvaluationData(String evaluationData) { this.evaluationData = evaluationData; }
}


