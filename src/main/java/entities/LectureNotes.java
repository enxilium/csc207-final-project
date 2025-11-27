package entities;

import java.time.LocalDateTime;

/**
 * Entity representing a set of generated lecture notes
 * for a particular course and topic.
 */
public class LectureNotes {

    private final String courseId;
    private final String topic;
    private final String content;      // the actual notes text (plain text / markdown)
    private final LocalDateTime generatedAt;

    public LectureNotes(String courseId,
                        String topic,
                        String content,
                        LocalDateTime generatedAt) {
        this.courseId = courseId;
        this.topic = topic;
        this.content = content;
        this.generatedAt = generatedAt;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getTopic() {
        return topic;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }
}