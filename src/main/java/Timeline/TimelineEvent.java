package Timeline;

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
    private final Instant occurredAt;
    private String title;
    private String snippet;
    private Integer numCards;
    private Integer numQuestions;
    private Double score;

    public TimelineEvent(UUID courseId, UUID contentId, TimelineEventType type) {
        this.id = UUID.randomUUID();
        this.courseId = courseId;
        this.contentId = contentId;
        this.type = type;
        this.occurredAt = Instant.now();
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
}
