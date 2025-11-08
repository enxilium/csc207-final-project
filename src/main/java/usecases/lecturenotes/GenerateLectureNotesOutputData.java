package usecases.lecturenotes;

import java.time.LocalDateTime;

public class GenerateLectureNotesOutputData {
    private final String courseId;
    private final String topic;
    private final String notesText;
    private final LocalDateTime generatedAt;

    public GenerateLectureNotesOutputData(String courseId, String topic,
                                          String notesText, LocalDateTime generatedAt) {
        this.courseId = courseId;
        this.topic = topic;
        this.notesText = notesText;
        this.generatedAt = generatedAt;
    }

    public String getCourseId() { return courseId; }
    public String getTopic() { return topic; }
    public String getNotesText() { return notesText; }
    public LocalDateTime getGeneratedAt() { return generatedAt; }
}